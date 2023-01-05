package de.malkusch.ha.automation.infrastructure.calendar;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import de.malkusch.ha.automation.model.TrashCollection;
import de.malkusch.ha.automation.model.TrashCollectionCalendar;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public final class InMemoryTrashCollectionCalendar implements TrashCollectionCalendar, AutoCloseable {

    private volatile Collection<TrashCollection> collections;
    private final InMemoryCalendarProvider provider;
    private final InMemoryCalendarCache cache;

    public static interface InMemoryCalendarProvider {

        Collection<TrashCollection> fetch() throws IOException, InterruptedException;

    }

    InMemoryTrashCollectionCalendar(InMemoryCalendarProvider provider, InMemoryCalendarCache cache) throws IOException {
        this.provider = provider;
        this.cache = cache;

        try {
            this.collections = provider.fetch();

        } catch (Exception e) {
            log.warn("Failed fetching Calendar, falling back to cached file", e);
            this.collections = cache.load();
        }
    }

    @Override
    public TrashCollection findNextTrashCollectionAfter(LocalDate after) {
        return collections.stream() //
                .filter(it -> it.date().isAfter(after)) //
                .min((a, b) -> a.date().compareTo(b.date())) //
                .orElseThrow(() -> new IllegalStateException("Can't find next trash collection after " + after));
    }

    @Scheduled(cron = "${calendar.update}")
    void update() throws IOException, InterruptedException {
        log.info("Updating calendar");
        this.collections = provider.fetch();
        cache.store(collections);
    }

    @Override
    public void close() throws Exception {
        cache.store(collections);
    }
}
