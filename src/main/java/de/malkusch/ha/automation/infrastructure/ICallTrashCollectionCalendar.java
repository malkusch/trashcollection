package de.malkusch.ha.automation.infrastructure;

import static net.fortuna.ical4j.model.Component.VEVENT;
import static net.fortuna.ical4j.model.Property.SUMMARY;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.malkusch.ha.automation.model.TrashCan;
import de.malkusch.ha.automation.model.TrashCollection;
import de.malkusch.ha.automation.model.TrashCollectionCalendar;
import de.malkusch.ha.shared.infrastructure.http.HttpClient;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.filter.predicate.PeriodRule;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.DtStart;

@Service
@Slf4j
final class ICallTrashCollectionCalendar implements TrashCollectionCalendar {

    private final HttpClient http;
    private final String url;
    private final TrashCanMapper mapper;
    private volatile Calendar calendar;

    ICallTrashCollectionCalendar(HttpClient http, @Value("${trashday.url}") String url, TrashCanMapper mapper)
            throws IOException, InterruptedException, ParserException {

        this.http = http;
        this.url = url;
        this.mapper = mapper;
        this.calendar = download();
    }

    @Override
    public TrashCollection findNextTrashCollectionAfter(LocalDate after) {
        update();

        var limit = after.plusMonths(3);
        var period = new Period<>(after, limit);

        List<VEvent> events = calendar.getComponents(VEVENT);
        var dateCans = events.stream() //
                .filter(new PeriodRule<>(period)) //
                .flatMap(this::toDateCan) //
                .filter(it -> it.date.isAfter(after)) //
                .collect(Collectors.groupingBy(DateCan::date));

        var next = dateCans.entrySet().stream() //
                .map(it -> toTrashCollection(it.getKey(), it.getValue())) //
                .min((a, b) -> a.date().compareTo(b.date()));

        return next.orElseThrow(() -> new IllegalStateException("Can't find next trash collection after " + after));
    }

    private static record DateCan(LocalDate date, TrashCan can) {
    }

    private static TrashCollection toTrashCollection(LocalDate date, List<DateCan> dateCans) {
        var cans = dateCans.stream().map(DateCan::can).toList();
        return new TrashCollection(date, cans);
    }

    private Stream<DateCan> toDateCan(VEvent event) {
        var date = event.getStartDate().map(DtStart::getDate).orElse(null);
        if (!(date instanceof LocalDate)) {
            return Stream.empty();
        }

        var can = event.getProperty(SUMMARY).map(Property::getValue).flatMap(mapper::toTrashCan);
        if (can.isEmpty()) {
            return Stream.empty();
        }
        var dateCan = new DateCan((LocalDate) date, can.get());
        return Stream.of(dateCan);
    }

    private void update() {
        try {
            calendar = download();
        } catch (IOException | InterruptedException | ParserException e) {
            log.warn("Failed to update calendar {}", url, e);
        }
    }

    private Calendar download() throws IOException, InterruptedException, ParserException {
        log.debug("Downloading {}", url);
        try (var response = http.get(url)) {
            return new CalendarBuilder().build(response.body);
        }
    }
}
