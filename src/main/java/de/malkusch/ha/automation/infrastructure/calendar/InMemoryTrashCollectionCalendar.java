package de.malkusch.ha.automation.infrastructure.calendar;

import static de.malkusch.ha.shared.infrastructure.event.EventPublisher.publish;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
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

    private static final Comparator<? super TrashCollection> SORT_BY_DATE = (a, b) -> a.date().compareTo(b.date());

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

        public String toString() {
            var min = stream().min(SORT_BY_DATE).map(it -> it.date()).get();
            var max = stream().max(SORT_BY_DATE).map(it -> it.date()).get();
            return String.format("[%s - %s, n=%d]", min, max, collections.size());
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

        log.info("Loaded calendar: {}", collections);
    }

    @Override
    public TrashCollection findNextTrashCollectionAfter(LocalDate after) {
        return collections.stream() //
                .filter(it -> it.date().isAfter(after)) //
                .min(SORT_BY_DATE) //
                .orElseThrow(() -> new IllegalStateException("Can't find next trash collection after " + after));
    }

    @Override
    public Stream<TrashCollection> findAll() {
        return collections.stream();
    }

    @Scheduled(cron = "${calendar.update}")
    void update() throws IOException, InterruptedException {
        try {
            log.info("Updating calendar");
            collections = provider.fetch();
            log.info("Updated calendar: {}", collections);
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
