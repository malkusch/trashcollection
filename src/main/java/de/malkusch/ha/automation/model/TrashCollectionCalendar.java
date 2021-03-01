package de.malkusch.ha.automation.model;

import java.time.LocalDate;
import java.util.Set;

public interface TrashCollectionCalendar {

    Set<TrashCan> findTrashCollection(LocalDate date);

}
