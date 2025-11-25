package de.malkusch.ha.automation.infrastructure.calendar;

import de.malkusch.ha.automation.model.TrashCollection;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Stream;

import static java.lang.Math.abs;
import static java.util.Objects.requireNonNull;

public record TrashCollections(Collection<TrashCollection> collections) {

    public TrashCollections {
        requireNonNull(collections);
        if (collections.isEmpty()) {
            throw new IllegalArgumentException("Trash collection is empty");
        }
    }

    Stream<TrashCollection> stream() {
        return collections.stream();
    }

    TrashCollection first() {
        return collections.stream() //
                .min(InMemoryTrashCollectionCalendar.SORT_BY_DATE) //
                .orElseThrow(() -> new IllegalStateException("Can't find first trash collection"));
    }

    TrashCollection last() {
        return collections.stream() //
                .max(InMemoryTrashCollectionCalendar.SORT_BY_DATE) //
                .orElseThrow(() -> new IllegalStateException("Can't find last trash collection"));
    }

    TrashCollections add(TrashCollections other) {
        var added = new HashSet<>(collections);
        added.addAll(other.collections);
        return new TrashCollections(added);
    }

    public String toString() {
        var hash = abs(hashCode() % 10000);
        return String.format("[%s - %s, n=%d, #=%d]", first().date(), last().date(), collections.size(), hash);
    }
}