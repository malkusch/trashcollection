package de.malkusch.ha.automation.presentation;

import org.springframework.stereotype.Service;

import de.malkusch.ha.automation.application.DoneNextCollectionApplicationService;
import de.malkusch.ha.automation.model.NextTrashCollection.NotNextException;
import de.malkusch.ha.automation.model.NextTrashCollection.TooFarInFutureException;
import de.malkusch.ha.shared.infrastructure.TrashCollectionFormatter;
import de.malkusch.telgrambot.Command;
import de.malkusch.telgrambot.Handler.CallbackHandler.Handling;
import de.malkusch.telgrambot.Handler.CallbackHandler.Result;
import de.malkusch.telgrambot.Message.CallbackMessage;
import de.malkusch.telgrambot.TelegramApi;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public final class Done implements Handling {

    public static final Command COMMAND = new Command("done");

    private final DoneNextCollectionApplicationService service;
    private final TrashCollectionFormatter trashCollectionFormatter;

    @Override
    public Result handle(TelegramApi api, CallbackMessage message) {
        try {
            var trashCollection = trashCollectionFormatter.parse(message.callback().data());
            service.done(trashCollection);
            return new Result(true);

        } catch (TooFarInFutureException e) {
            return new Result(false, "Zu früh zum erledigen");

        } catch (NotNextException e) {
            return new Result(true);
        }
    }
}
