package de.malkusch.ha.automation.application;

import static de.malkusch.ha.shared.infrastructure.event.EventPublisher.publish;

import org.springframework.stereotype.Service;

import de.malkusch.ha.automation.model.NextTrashCollection;
import de.malkusch.ha.automation.model.TrashCollection;
import de.malkusch.ha.shared.infrastructure.event.Event;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public final class PrintNextCollectionApplicationService {

    private final NextTrashCollection next;

    public static final record Next(TrashCollection next) {
    }

    public Next printNext() {
        return new Next(next.nextTrashCollection());
    }
}
