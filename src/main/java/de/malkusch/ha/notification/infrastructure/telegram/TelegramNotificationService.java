package de.malkusch.ha.notification.infrastructure.telegram;

import de.malkusch.ha.notification.model.Notification;
import de.malkusch.ha.notification.model.Notification.CallbackNotification;
import de.malkusch.ha.notification.model.Notification.TextNotification;
import de.malkusch.ha.notification.model.NotificationService;
import de.malkusch.telgrambot.Command;
import de.malkusch.telgrambot.TelegramApi;
import de.malkusch.telgrambot.TelegramApi.Button;
import de.malkusch.telgrambot.Update.CallbackUpdate.Callback;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class TelegramNotificationService implements NotificationService {

    private final TelegramApi telegram;

    @Override
    public void send(Notification notification) {
        if (notification instanceof TextNotification text) {
            send(text);

        } else if (notification instanceof CallbackNotification callback) {
            send(callback);

        } else {
            telegram.send(notification.toString());
        }
    }

    private void send(TextNotification notification) {
        telegram.send(notification.toString());
    }

    private void send(CallbackNotification notification) {
        var payload = notification.callback().payload();
        var done = new Command("done");  //XXX
        var button = new Button(notification.callback().name(),  new Callback(done, payload));
        telegram.send(notification.message(), button);
    }
}
