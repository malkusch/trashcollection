package de.malkusch.ha.automation.infrastructure.calendar;

import de.malkusch.ha.automation.model.TrashCollection;
import de.malkusch.ha.automation.model.TrashCollectionCalendar;
import de.malkusch.ha.shared.infrastructure.event.ErrorLogged;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.stream.Stream;

import static de.malkusch.ha.shared.infrastructure.event.EventPublisher.publish;

@Slf4j
final class InMemoryTrashCollectionCalendar implements TrashCollectionCalendar, AutoCloseable {

    static final Comparator<? super TrashCollection> SORT_BY_DATE = (a, b) -> a.date().compareTo(b.date());

    private volatile TrashCollections collections;
    private final CalendarProvider provider;
    private final InMemoryCalendarCache cache;
    private final Clock clock;

    InMemoryTrashCollectionCalendar(CalendarProvider provider, InMemoryCalendarCache cache, Clock clock)
            throws IOException {

        this.provider = provider;
        this.cache = cache;
        this.clock = clock;

        try {
            collections = provider.fetch(LocalDate.now(clock));

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

                .orElseGet(() -> {
                    var last = collections.last();
                    log.warn("Can't find last trash collection after {}, falling back to last {}", after, last);
                    return last;
                });
    }

    @Override
    public Stream<TrashCollection> findNextTrashCollectionsAfter(LocalDate after) {
        return collections.stream() //
                .filter(it -> it.date().isAfter(after)) //
                .sorted(SORT_BY_DATE);
    }

    @Override
    public Stream<TrashCollection> findAll() {
        return collections.stream();
    }

    @Scheduled(cron = "${calendar.update}")
    void update() throws InterruptedException {
        try {
            collections = provider.fetch(LocalDate.now(clock));
            log.info("Updated calendar: {}", collections);
            cache.store(collections);

        } catch (InterruptedException e) {
            throw e;

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
