package de.malkusch.ha.automation.infrastructure.calendar.ical4j;

import java.util.Optional;

import de.malkusch.ha.automation.model.TrashCan;

public interface TrashCanMapper {
    Optional<TrashCan> toTrashCan(String summary);
}
