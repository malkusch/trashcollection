package de.malkusch.ha.automation.infrastructure.calendar.ical4j;

import static java.time.LocalDate.now;
import static java.util.stream.Collectors.groupingBy;
import static net.fortuna.ical4j.model.Component.VEVENT;
import static net.fortuna.ical4j.model.Property.SUMMARY;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.List;

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

        var limit = now().plusMonths(15);
        var incompleteCollections = events.stream() //
                .map(this::toIncompleteTrashCollection) //
                .filter(it -> it.date().isBefore(limit)) //
                .collect(groupingBy(IncompleteTrashCollection::date));

        var collections = incompleteCollections.entrySet().stream() //
                .map(it -> toTrashCollection(it.getKey(), it.getValue())) //
                .toList();

        return new TrashCollections(collections);
    }

    private static record IncompleteTrashCollection(LocalDate date, TrashCan can) {
    }

    private static TrashCollection toTrashCollection(LocalDate date, List<IncompleteTrashCollection> dateCans) {
        if (!dateCans.stream().allMatch(it -> it.date.equals(date))) {
            throw new IllegalStateException(
                    String.format("All dates %s must match the collection date %s", dateCans, date));
        }
        var cans = dateCans.stream().map(IncompleteTrashCollection::can).toList();
        return new TrashCollection(date, cans);
    }

    private IncompleteTrashCollection toIncompleteTrashCollection(VEvent event) {
        var date = collectionDate(event);

        var can = event.getProperty(SUMMARY) //
                .map(Property::getValue) //
                .map(mapper::toTrashCan) //
                .orElseThrow(() -> new IllegalStateException(event + " has no summary"));

        return new IncompleteTrashCollection(date, can);
    }

    private static LocalDate collectionDate(VEvent event) {
        var date = event.getStartDate().map(DtStart::getDate)
                .orElseThrow(() -> new IllegalStateException(event + " has no collection date"));

        if (date instanceof LocalDate) {
            return (LocalDate) date;
        }
        if (date instanceof LocalDateTime) {
            return ((LocalDateTime) date).toLocalDate();
        }
        if (date instanceof ZonedDateTime) {
            return ((ZonedDateTime) date).toLocalDate();
        }
        if (date instanceof TemporalAccessor) {
            return LocalDate.from(date);
        }

        throw new IllegalStateException(event + " has invalid collection date " + date);
    }
}
