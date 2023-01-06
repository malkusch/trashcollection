package de.malkusch.ha.automation.infrastructure.calendar.ical4j;

import static net.fortuna.ical4j.model.Component.VEVENT;
import static net.fortuna.ical4j.model.Property.SUMMARY;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import de.malkusch.ha.automation.infrastructure.calendar.InMemoryTrashCollectionCalendar.InMemoryCalendarProvider;
import de.malkusch.ha.automation.infrastructure.calendar.InMemoryTrashCollectionCalendar.TrashCollections;
import de.malkusch.ha.automation.model.TrashCan;
import de.malkusch.ha.automation.model.TrashCollection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.DtStart;

@Service
@RequiredArgsConstructor
@Slf4j
public final class Ical4jInMemoryCalendarProvider implements InMemoryCalendarProvider {

    private final TrashCanMapper mapper;
    private final Ical4jHttpFactory http;

    @Override
    public TrashCollections fetch() throws IOException, InterruptedException {
        List<VEvent> events = http.download().getComponents(VEVENT);

        var limit = LocalDate.now().plusMonths(15);
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
        if (!dateCans.stream().allMatch(it -> it.date.equals(date))) {
            throw new IllegalStateException(
                    String.format("All dates %s must match the collection date %s", dateCans, date));
        }
        var cans = dateCans.stream().map(DateCan::can).toList();
        return new TrashCollection(date, cans);
    }

    private Stream<DateCan> toDateCan(VEvent event) {
        var date = collectionDate(event);
        if (date == null) {
            return Stream.empty();
        }

        var summary = event.getProperty(SUMMARY).map(Property::getValue).orElse(null);
        if (summary == null) {
            return Stream.empty();
        }

        var can = mapper.toTrashCan(summary);
        if (can.isEmpty()) {
            log.warn("Couldn't map '{}' to a trash can", summary);
            return Stream.empty();
        }
        var dateCan = new DateCan(date, can.get());
        return Stream.of(dateCan);
    }

    private static LocalDate collectionDate(VEvent event) {
        var date = event.getStartDate().map(DtStart::getDate).orElse(null);
        if (date == null) {
            log.warn("{} has no collection date", event);
            return null;
        }

        try {
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

            log.warn("{} has collection date {} which couldn't be converted to LocalDate", event, date);
            return null;

        } catch (Exception e) {
            log.warn("{} has collection date {} which couldn't be converted to LocalDate", event, date, e);
            return null;
        }
    }
}
