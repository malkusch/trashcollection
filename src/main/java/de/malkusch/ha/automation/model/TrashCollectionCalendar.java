package de.malkusch.ha.automation.model;

import java.time.LocalDate;

public interface TrashCollectionCalendar {

    TrashCollection findNextTrashCollectionAfter(LocalDate after);

}
