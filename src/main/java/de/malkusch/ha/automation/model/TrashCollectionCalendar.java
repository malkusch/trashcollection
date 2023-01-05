package de.malkusch.ha.automation.model;

import java.time.LocalDate;
import java.util.stream.Stream;

public interface TrashCollectionCalendar {

    TrashCollection findNextTrashCollectionAfter(LocalDate after);

    Stream<TrashCollection> findAll();

}
