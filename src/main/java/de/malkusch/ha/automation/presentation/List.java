package de.malkusch.ha.automation.presentation;

import org.springframework.stereotype.Service;

import de.malkusch.ha.automation.application.ListNextCollectionsApplicationService;
import de.malkusch.telgrambot.Handler.TextHandler.Handling;
import de.malkusch.telgrambot.Command;
import de.malkusch.telgrambot.TelegramApi;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public final class List implements Handling {

    public static final Command COMMAND = new Command("list");
    
    private final ListNextCollectionsApplicationService service;

    @Override
    public void handle(TelegramApi api) {
        service.listNext();
    }
}
