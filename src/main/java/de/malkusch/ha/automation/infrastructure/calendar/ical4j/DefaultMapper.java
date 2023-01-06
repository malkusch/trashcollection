package de.malkusch.ha.automation.infrastructure.calendar.ical4j;

import static de.malkusch.ha.automation.model.TrashCan.ORGANIC;
import static de.malkusch.ha.automation.model.TrashCan.PAPER;
import static de.malkusch.ha.automation.model.TrashCan.PLASTIC;
import static de.malkusch.ha.automation.model.TrashCan.RESIDUAL;

import org.springframework.stereotype.Service;

import de.malkusch.ha.automation.model.TrashCan;

@Service
public final class DefaultMapper implements TrashCanMapper {

    @Override
    public TrashCan toTrashCan(String summary) {
        var summary_lower = summary.toLowerCase();

        if (summary_lower.contains("papier")) {
            return PAPER;

        } else if (summary_lower.contains("gelber sack")) {
            return PLASTIC;

        } else if (summary_lower.contains("biomüll")) {
            return ORGANIC;

        } else if (summary_lower.contains("restmüll")) {
            return RESIDUAL;

        } else {
            throw new IllegalArgumentException("Couldn't map " + summary);
        }
    }
}
