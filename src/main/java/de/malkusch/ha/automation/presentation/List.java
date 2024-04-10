package de.malkusch.ha.automation.presentation;

import static de.malkusch.ha.shared.infrastructure.TrashCollectionFormatter.trashCollection;

import org.springframework.stereotype.Service;

import de.malkusch.ha.automation.application.ListNextCollectionsApplicationService;
import de.malkusch.ha.automation.application.ListNextCollectionsApplicationService.ListNext;
import de.malkusch.telgrambot.Command;
import de.malkusch.telgrambot.Handler.TextHandler.Handling;
import de.malkusch.telgrambot.TelegramApi;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public final class List implements Handling {

    public static final Command COMMAND = new Command("list");

    private final ListNextCollectionsApplicationService service;
    private final TelegramApi telegram;

    @Override
    public void handle(TelegramApi api) {
        var list = service.listNext();

        telegram.send(message(list));
    }

    private static String message(ListNext list) {
        return list.next().stream() //
                .map(it -> trashCollection(it)) //
                .reduce((a, b) -> a + "\n" + b) //
                .orElse("keine Müllabfuhr");
    }
}
