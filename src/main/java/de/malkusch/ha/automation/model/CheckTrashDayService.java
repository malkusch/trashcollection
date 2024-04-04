package de.malkusch.ha.automation.model;

import static de.malkusch.ha.shared.infrastructure.event.EventPublisher.publish;

import org.springframework.stereotype.Service;

import de.malkusch.ha.shared.infrastructure.event.Event;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public final class CheckTrashDayService {

    private static enum State {
        REMINDING_NEXT, WAITING_FOR_NEXT
    }

    private volatile State state = State.WAITING_FOR_NEXT;

    public void check(NextTrashCollection next) {
        state = switch (state) {
        case WAITING_FOR_NEXT -> {
            if (next.isTomorrow()) {
                noticeNext(next);
                yield State.REMINDING_NEXT;
            } else {
                yield State.WAITING_FOR_NEXT;
            }
        }
        case REMINDING_NEXT -> {
            if (next.isTomorrow()) {
                remindNext(next);
                yield State.REMINDING_NEXT;
            } else {
                yield State.WAITING_FOR_NEXT;
            }
        }
        };
    }

    private void remindNext(NextTrashCollection next) {
        log.info("Reminding tomorrow's trash day for {}", next);
        publish(new TomorrowsTrashDayReminded(next.nextTrashCollection()));
    }

    private void noticeNext(NextTrashCollection next) {
        log.info("Noticed tomorrow's trash day for {}", next);
        publish(new TomorrowsTrashDayNoticed(next.nextTrashCollection()));
    }

    public record TomorrowsTrashDayNoticed(TrashCollection nextCollection) implements Event {
    }

    public record TomorrowsTrashDayReminded(TrashCollection nextCollection) implements Event {
    }
}
