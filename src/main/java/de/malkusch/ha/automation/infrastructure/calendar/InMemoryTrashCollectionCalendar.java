package de.malkusch.ha.automation.infrastructure.calendar;

import static de.malkusch.ha.shared.infrastructure.event.EventPublisher.publish;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.stream.Stream;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import de.malkusch.ha.automation.model.TrashCollection;
import de.malkusch.ha.automation.model.TrashCollectionCalendar;
import de.malkusch.ha.shared.infrastructure.event.ErrorLogged;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public final class InMemoryTrashCollectionCalendar implements TrashCollectionCalendar, AutoCloseable {

    private volatile TrashCollections collections;
    private final InMemoryCalendarProvider provider;
    private final InMemoryCalendarCache cache;

    public static record TrashCollections(Collection<TrashCollection> collections) {

        public TrashCollections {
            requireNonNull(collections);
            if (collections.isEmpty()) {
                throw new IllegalArgumentException("Trash collection is empty");
            }
        }

        public Stream<TrashCollection> stream() {
            return collections.stream();
        }
    }

    public static interface InMemoryCalendarProvider {

        TrashCollections fetch() throws IOException, InterruptedException;

    }

    InMemoryTrashCollectionCalendar(InMemoryCalendarProvider provider, InMemoryCalendarCache cache) throws IOException {
        this.provider = provider;
        this.cache = cache;

        try {
            collections = provider.fetch();

        } catch (Exception e) {
            log.warn("Failed fetching Calendar, falling back to cached file", e);
            collections = cache.load();
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
        try {
            log.info("Updating calendar");
            collections = provider.fetch();
            cache.store(collections);

        } catch (Exception e) {
            var event = new ErrorLogged("Failed updating calendar");
            log.error("Failed updating calendar [{}]", event.reference(), e);
            publish(event);
        }
    }

    @Override
    public void close() throws Exception {
        cache.store(collections);
    }
}
