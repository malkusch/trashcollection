package de.malkusch.ha.automation.presentation;

import de.malkusch.ha.automation.application.ListNextCollectionsApplicationService;
import de.malkusch.ha.automation.application.ListNextCollectionsApplicationService.ListNext;
import de.malkusch.ha.notification.infrastructure.telegram.TelegramEnabled;
import de.malkusch.ha.shared.infrastructure.TrashCollectionFormatter;
import de.malkusch.telgrambot.Command;
import de.malkusch.telgrambot.TelegramApi;
import de.malkusch.telgrambot.UpdateReceiver.CommandReceiver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@TelegramEnabled
public final class List implements CommandReceiver {

    public static final Command COMMAND = new Command("list");

    private final ListNextCollectionsApplicationService service;
    private final TelegramApi telegram;

    @Override
    public void receive() {
        var list = service.listNext();

        telegram.send(message(list));
    }

    private static String message(ListNext list) {
        return list.next().stream() //
                .map(TrashCollectionFormatter::trashCollection) //
                .reduce((a, b) -> a + "\n" + b) //
                .orElse("keine Müllabfuhr");
    }
}
