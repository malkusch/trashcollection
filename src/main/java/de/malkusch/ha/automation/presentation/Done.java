package de.malkusch.ha.automation.presentation;

import de.malkusch.ha.automation.application.DoneNextCollectionApplicationService;
import de.malkusch.ha.automation.model.NextTrashCollection.NotNextException;
import de.malkusch.ha.automation.model.NextTrashCollection.TooFarInFutureException;
import de.malkusch.ha.automation.model.NextTrashCollection.TooOldException;
import de.malkusch.ha.notification.infrastructure.telegram.TelegramEnabled;
import de.malkusch.ha.shared.infrastructure.TrashCollectionFormatter;
import de.malkusch.telgrambot.Command;
import de.malkusch.telgrambot.Update.CallbackUpdate;
import de.malkusch.telgrambot.UpdateReceiver.CallbackReceiver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static de.malkusch.telgrambot.Reaction.THUMBS_UP;

@Service
@RequiredArgsConstructor
@TelegramEnabled
public final class Done implements CallbackReceiver {

    public static final Command COMMAND = new Command("done");

    private final DoneNextCollectionApplicationService service;
    private final TrashCollectionFormatter trashCollectionFormatter;

    @Override
    public Result receive(CallbackUpdate update) {
        try {
            var trashCollection = trashCollectionFormatter.parseJson(update.callback().data());
            service.done(trashCollection);
            return new Result(true, THUMBS_UP);

        } catch (TooOldException e) {
            return new Result(true, "Zu alt zum Erledigen");

        } catch (TooFarInFutureException e) {
            return new Result(false, "Zu früh zum Erledigen");

        } catch (NotNextException e) {
            return new Result(true);
        }
    }
}
