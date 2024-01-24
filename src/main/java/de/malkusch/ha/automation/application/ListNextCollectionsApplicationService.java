package de.malkusch.ha.automation.application;

import static de.malkusch.ha.shared.infrastructure.event.EventPublisher.publish;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import de.malkusch.ha.automation.model.TrashCollection;
import de.malkusch.ha.automation.model.TrashCollectionCalendar;
import de.malkusch.ha.shared.infrastructure.event.Event;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public final class ListNextCollectionsApplicationService {

    private final TrashCollectionCalendar calendar;

    public static final record NextCollectionsListed(List<TrashCollection> next) implements Event {
    }

    public void listNext() {
        var now = LocalDate.now();
        var next = calendar.findNextTrashCollectionsAfter(now) //
                .limit(10)//
                .toList();

        publish(new NextCollectionsListed(next));
    }
}
