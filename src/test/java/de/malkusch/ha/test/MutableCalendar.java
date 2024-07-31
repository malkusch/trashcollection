package de.malkusch.ha.test;

import static de.malkusch.ha.test.TrashCollectionTests.trashCollection;
import static java.util.Arrays.stream;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import de.malkusch.ha.automation.model.TrashCollection;
import de.malkusch.ha.automation.model.TrashCollectionCalendar;

public final class MutableCalendar implements TrashCollectionCalendar {

    private static final Comparator<? super TrashCollection> SORT_BY_DATE = (a, b) -> a.date().compareTo(b.date());
    private final Set<TrashCollection> collections;

    public MutableCalendar(String... trashCollections) {
        collections = new HashSet<>(stream(trashCollections).map(TrashCollectionTests::trashCollection).toList());
    }

    public void add(String trashCollection) {
        collections.add(trashCollection(trashCollection));
    }

    @Override
    public Stream<TrashCollection> findAll() {
        return collections.stream();
    }

    @Override
    public Stream<TrashCollection> findNextTrashCollectionsAfter(LocalDate after) {
        return findAll() //
                .filter(it -> it.date().isAfter(after)) //
                .sorted(SORT_BY_DATE);
    }

    @Override
    public TrashCollection findNextTrashCollectionAfter(LocalDate after) {
        return findAll() //
                .filter(it -> it.date().isAfter(after)) //
                .min(SORT_BY_DATE) //
                .orElseThrow();
    }
}
