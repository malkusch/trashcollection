package de.malkusch.ha.automation.model;

import static de.malkusch.ha.shared.infrastructure.event.EventPublisher.publish;

import java.time.Clock;
import java.time.LocalDate;

import org.springframework.stereotype.Service;

import de.malkusch.ha.shared.infrastructure.event.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NextTrashCollection {

    private final TrashCollectionCalendar calendar;
    private final Clock clock;
    private volatile TrashCollection next;

    NextTrashCollection(TrashCollectionCalendar calendar, Clock clock) {
        this.calendar = calendar;
        this.clock = clock;

        this.next = calendar.findNextTrashCollectionAfter(LocalDate.now(clock));
        log.info("Next trash collection: {}", next);
    }

    public void done() {
        var tomorrow = LocalDate.now(clock).plusDays(1);
        if (next.date().isAfter(tomorrow)) {
            log.warn("Next is too far in the future: {}", next);
            return;
        }
        done(calendar.findNextTrashCollectionAfter(next.date()));
    }

    private void done(TrashCollection changed) {
        if (changed.equals(next)) {
            return;
        }
        if (changed.date().isBefore(next.date())) {
            throw new IllegalArgumentException(String.format("%s is before %s", changed, next));
        }
        next = changed;
        publish(new NextTrashCollectionChanged(changed));
    }

    public void checkNextChanged() {
        log.debug("Checking if next changed");
        var changedNext = calendar.findNextTrashCollectionAfter(LocalDate.now(clock));
        if (changedNext.equals(this.next)) {
            return;
        }

        log.info("Next trash collection changed from {} to {}", next, changedNext);
        done(changedNext);
    }

    public TrashCollection nextTrashCollection() {
        return next;
    }

    @RequiredArgsConstructor
    public static final class NextTrashCollectionChanged implements Event {
        public final TrashCollection nextCollection;
    }
}
