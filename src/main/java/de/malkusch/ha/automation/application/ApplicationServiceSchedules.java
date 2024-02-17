package de.malkusch.ha.automation.application;

import static de.malkusch.ha.shared.infrastructure.event.EventPublisher.publish;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import de.malkusch.ha.automation.model.CheckTrashDayService;
import de.malkusch.ha.automation.model.NextTrashCollection;
import de.malkusch.ha.shared.infrastructure.event.ErrorLogged;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

public class ApplicationServiceSchedules {

    @Service
    @RequiredArgsConstructor
    @Slf4j
    public static final class CheckNextTrashCollectionSchedule {

        private final NextTrashCollection next;

        @Scheduled(cron = "59 49 5/6 * * *")
        public void checkNextChanged() {
            try {
                next.checkNextChanged();

            } catch (Exception e) {
                var event = new ErrorLogged("Failed checking if next changed");
                log.error("Failed checking if next changed [{}]", event.reference(), e);
                publish(event);
            }
        }
    }

    @Service
    @RequiredArgsConstructor
    @Slf4j
    public static final class CheckTrashDaySchedule {

        private final CheckTrashDayService checker;

        @Scheduled(cron = "59 59 5,12,15,17,20,21,22,23 * * *")
        public void checkTrashDay() {
            try {
                checker.checkTomorrow();

            } catch (Exception e) {
                var event = new ErrorLogged("Failed checking if tomorrow is trash day");
                log.error("Failed checking if tomorrow is trash day [{}]", event.reference(), e);
                publish(event);
            }
        }
    }
}