package de.malkusch.ha.automation.application;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import de.malkusch.ha.automation.model.CheckTrashDayService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public final class ScheduleCheckTrashDayApplicationService {

    private final CheckTrashDayService checker;

    @Scheduled(cron = "59 59 5 * * *")
    public void checkTrashDay() {
        checker.checkTomorrow();
    }
}
