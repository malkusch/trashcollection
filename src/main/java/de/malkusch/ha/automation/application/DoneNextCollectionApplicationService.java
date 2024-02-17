package de.malkusch.ha.automation.application;

import org.springframework.stereotype.Service;

import de.malkusch.ha.automation.model.NextTrashCollection;
import de.malkusch.ha.automation.model.NextTrashCollection.NotNextException;
import de.malkusch.ha.automation.model.NextTrashCollection.TooFarInFutureException;
import de.malkusch.ha.automation.model.TrashCollection;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public final class DoneNextCollectionApplicationService {

    private final NextTrashCollection next;

    public void done(TrashCollection trashCollection) throws TooFarInFutureException, NotNextException {
        next.done(trashCollection);
    }
}
