package de.malkusch.ha.automation.presentation;

import de.malkusch.ha.automation.application.ApplicationServiceSchedules.CheckNextTrashCollectionSchedule;
import de.malkusch.ha.automation.application.ApplicationServiceSchedules.CheckTrashDaySchedule;
import de.malkusch.telgrambot.Command;
import de.malkusch.telgrambot.UpdateReceiver.CommandReceiver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

public final class Debug {

    @Service
    @RequiredArgsConstructor
    public static final class Trashday implements CommandReceiver {

        public static final Command COMMAND = new Command("checkTrashDay");

        private final CheckTrashDaySchedule check;

        @Override
        public void receive() {
            check.checkTrashDay();
        }
    }

    @Service
    @RequiredArgsConstructor
    public static final class Changed implements CommandReceiver {

        public static final Command COMMAND = new Command("checkNextChanged");

        private final CheckNextTrashCollectionSchedule check;

        @Override
        public void receive() {
            check.checkNextChanged();
        }
    }
}
