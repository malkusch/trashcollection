package de.malkusch.ha.automation.presentation;

import org.springframework.stereotype.Service;

import de.malkusch.ha.automation.application.ApplicationServiceSchedules.CheckNextTrashCollectionSchedule;
import de.malkusch.ha.automation.application.ApplicationServiceSchedules.CheckTrashDaySchedule;
import de.malkusch.telgrambot.Command;
import de.malkusch.telgrambot.Handler.TextHandler.Handling;
import de.malkusch.telgrambot.TelegramApi;
import lombok.RequiredArgsConstructor;

public final class Debug {

    @Service
    @RequiredArgsConstructor
    public static final class Trashday implements Handling {

        public static final Command COMMAND = new Command("checkTrashDay");

        private final CheckTrashDaySchedule check;

        @Override
        public void handle(TelegramApi api) {
            check.checkTrashDay();
        }
    }

    @Service
    @RequiredArgsConstructor
    public static final class Changed implements Handling {

        public static final Command COMMAND = new Command("checkNextChanged");

        private final CheckNextTrashCollectionSchedule check;

        @Override
        public void handle(TelegramApi api) {
            check.checkNextChanged();
        }
    }
}
