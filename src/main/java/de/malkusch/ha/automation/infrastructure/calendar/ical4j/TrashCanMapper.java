package de.malkusch.ha.automation.infrastructure.calendar.ical4j;

import de.malkusch.ha.automation.model.TrashCan;

public interface TrashCanMapper {
    TrashCan toTrashCan(String summary);
}
