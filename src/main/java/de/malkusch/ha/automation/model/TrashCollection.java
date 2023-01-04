package de.malkusch.ha.automation.model;

import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import lombok.Value;

@Value
public class TrashCollection {

    private final LocalDate date;
    private final Set<TrashCan> trashCans;

    public TrashCollection(LocalDate date, Collection<TrashCan> trashCans) {
        this.date = requireNonNull(date);
        this.trashCans = unmodifiableSet(new HashSet<>(requireNonNull(trashCans)));
        if (trashCans.isEmpty()) {
            throw new IllegalArgumentException("Trash collection must have trash cans");
        }
    }

    public LocalDate date() {
        return date;
    }

    public String toString() {
        return String.format("%s: %s", date, trashCans);
    }
}
