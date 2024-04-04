package de.malkusch.ha.automation.model;

import static de.malkusch.ha.shared.infrastructure.event.EventPublisher.publish;
import static java.time.LocalDate.now;

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

    public static class TooFarInFutureException extends Exception {

    }

    public static class NotNextException extends Exception {

    }

    public void done() throws TooFarInFutureException {
        try {
            done(next);

        } catch (NotNextException e) {
            throw new IllegalStateException(e);
        }
    }

    public void done(TrashCollection doneCollection) throws TooFarInFutureException, NotNextException {
        if (!doneCollection.equals(next)) {
            throw new NotNextException();
        }

        var tomorrow = LocalDate.now(clock).plusDays(1);
        if (doneCollection.date().isAfter(tomorrow)) {
            throw new TooFarInFutureException();
        }

        change(calendar.findNextTrashCollectionAfter(doneCollection.date()));
    }

    private void change(TrashCollection changed) {
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
        change(changedNext);
    }

    public TrashCollection nextTrashCollection() {
        return next;
    }
    
    public boolean isTomorrow() {
        var today = now(clock);
        var tomorrow = today.plusDays(1);
        return next.date().isEqual(tomorrow);
    }

    @RequiredArgsConstructor
    public static final class NextTrashCollectionChanged implements Event {
        public final TrashCollection nextCollection;
    }
}
