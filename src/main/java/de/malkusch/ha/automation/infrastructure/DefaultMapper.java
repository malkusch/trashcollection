package de.malkusch.ha.automation.infrastructure;

import static de.malkusch.ha.automation.model.TrashCan.ORGANIC;
import static de.malkusch.ha.automation.model.TrashCan.PAPER;
import static de.malkusch.ha.automation.model.TrashCan.PLASTIC;
import static de.malkusch.ha.automation.model.TrashCan.RESIDUAL;

import java.util.Optional;

import org.springframework.stereotype.Service;

import de.malkusch.ha.automation.model.TrashCan;

@Service
final class DefaultMapper implements TrashCanMapper {

    @Override
    public Optional<TrashCan> toTrashCan(String summary) {

        if (summary.contains("Altpapier")) {
            return Optional.of(PAPER);

        } else if (summary.contains("Gelber Sack")) {
            return Optional.of(PLASTIC);

        } else if (summary.contains("Biomüll")) {
            return Optional.of(ORGANIC);

        } else if (summary.contains("Restmüll")) {
            return Optional.of(RESIDUAL);
        } else {
            return Optional.empty();
        }
    }
}
