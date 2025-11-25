package de.malkusch.ha.automation.infrastructure.calendar.ical4j;

import de.malkusch.ha.automation.infrastructure.calendar.CalendarProvider;
import de.malkusch.ha.automation.infrastructure.calendar.TrashCollections;
import de.malkusch.ha.automation.model.TrashCan;
import de.malkusch.ha.automation.model.TrashCollection;
import lombok.RequiredArgsConstructor;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.DtStart;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.groupingBy;
import static net.fortuna.ical4j.model.Component.VEVENT;
import static net.fortuna.ical4j.model.Property.SUMMARY;

@RequiredArgsConstructor
public final class Ical4jCalendarProvider implements CalendarProvider {

    private final TrashCanMapper mapper;
    private final Ical4jHttpFactory http;

    @Override
    public TrashCollections fetch(LocalDate fetchDate) throws IOException, InterruptedException {
        List<VEvent> events = http.download(fetchDate).getComponents(VEVENT);

        var limit = fetchDate.plusMonths(15);
        var incompleteCollections = events.stream() //
                .flatMap(it -> this.toIncompleteTrashCollection(it).stream()) //
                .filter(it -> it.date().isBefore(limit)) //
                .collect(groupingBy(IncompleteTrashCollection::date));

        var collections = incompleteCollections.entrySet().stream() //
                .map(it -> toTrashCollection(it.getKey(), it.getValue())) //
                .toList();

        return new TrashCollections(collections);
    }

    private record IncompleteTrashCollection(LocalDate date, TrashCan can) {
    }

    private static TrashCollection toTrashCollection(LocalDate date, List<IncompleteTrashCollection> dateCans) {
        if (!dateCans.stream().allMatch(it -> it.date.equals(date))) {
            throw new IllegalStateException(
                    String.format("All dates %s must match the collection date %s", dateCans, date));
        }
        var cans = dateCans.stream().map(IncompleteTrashCollection::can).toList();
        return new TrashCollection(date, cans);
    }

    private Optional<IncompleteTrashCollection> toIncompleteTrashCollection(VEvent event) {
        var date = collectionDate(event);

        var summary = event.getProperty(SUMMARY) //
                .map(Property::getValue) //
                .orElseThrow(() -> new IllegalStateException(event + " has no summary"));

        return mapper.toTrashCan(summary) //
                .map(can -> new IncompleteTrashCollection(date, can));
    }

    private static LocalDate collectionDate(VEvent event) {
        var date = ofNullable(event.getDateTimeStart()).map(DtStart::getDate)
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
