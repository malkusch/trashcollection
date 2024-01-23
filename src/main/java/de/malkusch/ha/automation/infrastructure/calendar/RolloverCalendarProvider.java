package de.malkusch.ha.automation.infrastructure.calendar;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Year;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
final class RolloverCalendarProvider implements CalendarProvider {

    private final CalendarProvider provider;

    @Override
    public TrashCollections fetch(LocalDate fetchDate) throws IOException, InterruptedException {
        var collections = provider.fetch(fetchDate);
        log.debug("Fetching collection for {}", fetchDate);
        if (collections.last().date().isAfter(fetchDate)) {
            return collections;
        }

        var nextFetchDate = Year.from(fetchDate).plusYears(1).atDay(1);
        log.debug("Merging next year's ({}) collection", nextFetchDate);
        try {
            var nextCollections = provider.fetch(nextFetchDate);
            var merged = collections.add(nextCollections);
            return merged;

        } catch (IOException e) {
            log.warn("Failed merging next year's ({}) collections", nextFetchDate, e);
            return collections;
        }
    }
}
