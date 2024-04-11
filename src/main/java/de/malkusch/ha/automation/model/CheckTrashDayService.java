package de.malkusch.ha.automation.model;

import static de.malkusch.ha.shared.infrastructure.event.EventPublisher.publish;

import org.springframework.stereotype.Service;

import de.malkusch.ha.shared.infrastructure.event.Event;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public final class CheckTrashDayService {

    private volatile TrashCollection last = TrashCollection.EMTPY;

    public void check(NextTrashCollection next) {
        if (!next.isTomorrow()) {
            return;
        }
        var trashCollection = next.nextTrashCollection();
        if (last.equals(trashCollection)) {
            remindNext(trashCollection);

        } else {
            noticeNext(trashCollection);
            last = trashCollection;
        }
    }

    private void remindNext(TrashCollection next) {
        log.info("Reminding tomorrow's trash day for {}", next);
        publish(new TomorrowsTrashDayReminded(next));
    }

    private void noticeNext(TrashCollection next) {
        log.info("Noticed tomorrow's trash day for {}", next);
        publish(new TomorrowsTrashDayNoticed(next));
    }

    public record TomorrowsTrashDayNoticed(TrashCollection nextCollection) implements Event {
    }

    public record TomorrowsTrashDayReminded(TrashCollection nextCollection) implements Event {
    }
}
