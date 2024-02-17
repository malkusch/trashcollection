package de.malkusch.ha.automation.model;

import static de.malkusch.ha.shared.infrastructure.event.EventPublisher.publish;
import static java.time.LocalDate.now;

import java.time.Clock;

import org.springframework.stereotype.Service;

import de.malkusch.ha.shared.infrastructure.event.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public final class CheckTrashDayService {

    private final NextTrashCollection next;
    private final Clock clock;

    public void checkTomorrow() {
        log.debug("Checking trash day");
        var today = now(clock);
        var tomorrow = today.plusDays(1);
        var next = this.next.nextTrashCollection();
        if (next.date().isEqual(tomorrow)) {
            log.info("Noticed tomorrow's trash day for {}", next);
            publish(new TomorrowsTrashDayNoticed(next));
        }
    }

    public record TomorrowsTrashDayNoticed(TrashCollection nextCollection) implements Event {
    }
}
