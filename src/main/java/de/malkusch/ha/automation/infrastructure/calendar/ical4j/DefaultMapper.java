package de.malkusch.ha.automation.infrastructure.calendar.ical4j;

import static de.malkusch.ha.automation.model.TrashCan.ORGANIC;
import static de.malkusch.ha.automation.model.TrashCan.PAPER;
import static de.malkusch.ha.automation.model.TrashCan.PLASTIC;
import static de.malkusch.ha.automation.model.TrashCan.RESIDUAL;

import java.util.Optional;

import de.malkusch.ha.automation.model.TrashCan;

public final class DefaultMapper implements TrashCanMapper {

    @Override
    public Optional<TrashCan> toTrashCan(String summary) {
        var summary_lower = summary.toLowerCase();

        if (summary_lower.contains("papier")) {
            return Optional.of(PAPER);

        } else if (summary_lower.contains("gelber sack")) {
            return Optional.of(PLASTIC);

        } else if (summary_lower.contains("bio")) {
            return Optional.of(ORGANIC);

        } else if (summary_lower.contains("restmüll")) {
            return Optional.of(RESIDUAL);

        } else if (summary_lower.contains("umweltmobil")) {
            return Optional.empty();

        } else {
            throw new IllegalArgumentException("Couldn't map " + summary);
        }
    }
}
