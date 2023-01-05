package de.malkusch.ha.automation.infrastructure.calendar.ical4j;

import static net.fortuna.ical4j.model.Component.VEVENT;
import static net.fortuna.ical4j.model.Property.SUMMARY;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import de.malkusch.ha.automation.infrastructure.calendar.InMemoryTrashCollectionCalendar.InMemoryCalendarProvider;
import de.malkusch.ha.automation.infrastructure.calendar.InMemoryTrashCollectionCalendar.TrashCollections;
import de.malkusch.ha.automation.model.TrashCan;
import de.malkusch.ha.automation.model.TrashCollection;
import lombok.RequiredArgsConstructor;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.DtStart;

@Service
@RequiredArgsConstructor
public final class Ical4jInMemoryCalendarProvider implements InMemoryCalendarProvider {

    private final TrashCanMapper mapper;
    private final Ical4jHttpFactory http;

    @Override
    public TrashCollections fetch() throws IOException, InterruptedException {
        List<VEvent> events = http.download().getComponents(VEVENT);

        var limit = LocalDate.now().plusYears(1);
        var dateCans = events.stream() //
                .flatMap(this::toDateCan) //
                .filter(it -> it.date().isBefore(limit)) //
                .collect(Collectors.groupingBy(DateCan::date));

        var collections = dateCans.entrySet().stream() //
                .map(it -> toTrashCollection(it.getKey(), it.getValue())) //
                .toList();

        return new TrashCollections(collections);
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
}
