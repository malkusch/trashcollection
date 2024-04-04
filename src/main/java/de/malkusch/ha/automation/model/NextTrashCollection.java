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

    public static class TooOldException extends Exception {

    }

    public static class TooFarInFutureException extends Exception {

    }

    public static class NotNextException extends Exception {

    }

    public void done() throws TooFarInFutureException {
        try {
            done(next);

        } catch (NotNextException | TooOldException e) {
            throw new IllegalStateException(e);
        }
    }

    private volatile TrashCollection lastDone = TrashCollection.EMTPY;

    public void done(TrashCollection doneCollection) throws TooFarInFutureException, NotNextException, TooOldException {
        if (!doneCollection.equals(next)) {
            throw new NotNextException();
        }

        var tomorrow = LocalDate.now(clock).plusDays(1);
        if (doneCollection.date().isAfter(tomorrow)) {
            throw new TooFarInFutureException();
        }

        if (doneCollection.date().isBefore(lastDone.date())) {
            throw new TooOldException();
        }
        change(calendar.findNextTrashCollectionAfter(doneCollection.date()));
        lastDone = doneCollection;
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

        var after = LocalDate.now(clock);
        if (after.isBefore(lastDone.date())) {
            after = lastDone.date();
        }
        var changedNext = calendar.findNextTrashCollectionAfter(after);

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
