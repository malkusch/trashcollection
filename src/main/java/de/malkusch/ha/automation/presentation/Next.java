package de.malkusch.ha.automation.presentation;

import org.springframework.stereotype.Service;

import de.malkusch.ha.automation.application.PrintNextCollectionApplicationService;
import de.malkusch.telgrambot.Handler.TextHandler.Handling;
import de.malkusch.telgrambot.Command;
import de.malkusch.telgrambot.TelegramApi;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public final class Next implements Handling {

    public static final Command COMMAND = new Command("next");
    
    private final PrintNextCollectionApplicationService service;

    @Override
    public void handle(TelegramApi api) {
        service.printNext();
    }
}
