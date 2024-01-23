package de.malkusch.ha.automation.infrastructure.calendar;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Year;

public interface CalendarProvider {

    TrashCollections fetch(LocalDate fetchDate) throws IOException, InterruptedException;

}