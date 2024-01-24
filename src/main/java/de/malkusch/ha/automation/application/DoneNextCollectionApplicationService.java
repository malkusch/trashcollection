package de.malkusch.ha.automation.application;

import org.springframework.stereotype.Service;

import de.malkusch.ha.automation.model.NextTrashCollection;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public final class DoneNextCollectionApplicationService {

    private final NextTrashCollection next;

    public void done() {
        next.done();
    }
}
