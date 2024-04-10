package de.malkusch.ha.automation.application;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import de.malkusch.ha.automation.model.TrashCollection;
import de.malkusch.ha.automation.model.TrashCollectionCalendar;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public final class ListNextCollectionsApplicationService {

    private final TrashCollectionCalendar calendar;

    public static final record ListNext(List<TrashCollection> next) {
    }

    public ListNext listNext() {
        var now = LocalDate.now();
        var next = calendar.findNextTrashCollectionsAfter(now) //
                .limit(10)//
                .toList();

        return new ListNext(next);
    }
}
