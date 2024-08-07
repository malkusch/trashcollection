package de.malkusch.ha.automation.presentation;

import de.malkusch.ha.automation.application.PrintNextCollectionApplicationService;
import de.malkusch.ha.notification.infrastructure.telegram.TelegramEnabled;
import de.malkusch.telgrambot.Command;
import de.malkusch.telgrambot.TelegramApi;
import de.malkusch.telgrambot.UpdateReceiver.CommandReceiver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static de.malkusch.ha.shared.infrastructure.TrashCollectionFormatter.trashCollection;

@Service
@RequiredArgsConstructor
@TelegramEnabled
public final class Next implements CommandReceiver {

    public static final Command COMMAND = new Command("next");

    private final PrintNextCollectionApplicationService service;
    private final TelegramApi telegram;

    @Override
    public void receive() {
        var next = service.printNext();
        var message = trashCollection(next.next());
        telegram.send(message);
    }
}
