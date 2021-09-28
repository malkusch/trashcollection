package de.malkusch.ha.automation.infrastructure;

import static java.util.stream.Collectors.toSet;
import static net.fortuna.ical4j.model.Component.VEVENT;
import static net.fortuna.ical4j.model.Property.SUMMARY;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.malkusch.ha.automation.model.TrashCan;
import de.malkusch.ha.automation.model.TrashCollectionCalendar;
import de.malkusch.ha.shared.infrastructure.http.HttpClient;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.filter.predicate.PeriodRule;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.Property;

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
    public Set<TrashCan> findTrashCollection(LocalDate date) {
        update();

        var period = new Period<>(date, date);
        return calendar.getComponents(VEVENT).stream().filter(new PeriodRule<>(period)).flatMap(this::map)
                .collect(toSet());
    }

    private Stream<TrashCan> map(Component event) {
        return event.getProperties().getFirst(SUMMARY).map(Property::getValue).flatMap(mapper::toTrashCan).stream();
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
