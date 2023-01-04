package de.malkusch.ha.automation.application;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import de.malkusch.ha.automation.model.NextTrashCollection;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public final class ScheduleCheckNextTrashCollectionApplicationService {

    private final NextTrashCollection next;

    @Scheduled(cron = "59 59 6 * * *")
    public void checkTrashDay() {
        next.checkNextChanged();
    }
}
