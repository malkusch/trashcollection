package de.malkusch.ha.automation.infrastructure.calendar.ical4j;

import static de.malkusch.ha.automation.model.TrashCan.ORGANIC;
import static de.malkusch.ha.automation.model.TrashCan.PAPER;
import static de.malkusch.ha.automation.model.TrashCan.PLASTIC;
import static de.malkusch.ha.automation.model.TrashCan.RESIDUAL;

import java.util.Optional;

import org.springframework.stereotype.Service;

import de.malkusch.ha.automation.model.TrashCan;

@Service
public final class DefaultMapper implements TrashCanMapper {

    @Override
    public Optional<TrashCan> toTrashCan(String summary) {
        var summary_lower = summary.toLowerCase();

        if (summary_lower.contains("papier")) {
            return Optional.of(PAPER);

        } else if (summary_lower.contains("gelber sack")) {
            return Optional.of(PLASTIC);

        } else if (summary_lower.contains("biomüll")) {
            return Optional.of(ORGANIC);

        } else if (summary_lower.contains("restmüll")) {
            return Optional.of(RESIDUAL);
        } else {
            return Optional.empty();
        }
    }
}
