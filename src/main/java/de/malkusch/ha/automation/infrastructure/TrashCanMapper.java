package de.malkusch.ha.automation.infrastructure;

import java.util.Optional;

import de.malkusch.ha.automation.model.TrashCan;

interface TrashCanMapper {
    Optional<TrashCan> toTrashCan(String summary);
}
