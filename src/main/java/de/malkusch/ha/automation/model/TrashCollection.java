package de.malkusch.ha.automation.model;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public record TrashCollection(LocalDate date, Set<TrashCan> trashCans) {

    public static final TrashCollection EMTPY = new TrashCollection(LocalDate.MIN, TrashCan.values());

    public TrashCollection(LocalDate date, Collection<TrashCan> trashCans) {
        this(date, new HashSet<>(trashCans));
    }

    public TrashCollection(LocalDate date, TrashCan... trashCans) {
        this(date, asList(trashCans));
    }

    public TrashCollection {
        requireNonNull(date);
        requireNonNull(trashCans);
        if (trashCans.isEmpty()) {
            throw new IllegalArgumentException("Trash collection must have trash cans");
        }
    }

    public LocalDate date() {
        return date;
    }

    public String toString() {
        var cans = Arrays.toString(trashCans.toArray());
        return String.format("%s: %s", date, cans);
    }
}
