package de.malkusch.ha.automation.application;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import de.malkusch.ha.automation.model.CheckTrashDayService;
import de.malkusch.ha.automation.model.NextTrashCollection;
import lombok.RequiredArgsConstructor;

public class ApplicationServiceSchedules {

    @Service
    @RequiredArgsConstructor
    public static final class CheckNextTrashCollectionSchedule {

        private final NextTrashCollection next;

        @Scheduled(cron = "59 49 5 * * *")
        public void checkNextChanged() {
            next.checkNextChanged();
        }
    }

    @Service
    @RequiredArgsConstructor
    public static final class CheckTrashDaySchedule {

        private final CheckTrashDayService checker;

        @Scheduled(cron = "59 59 */5 * * *")
        public void checkTrashDay() {
            checker.checkTomorrow();
        }
    }
}