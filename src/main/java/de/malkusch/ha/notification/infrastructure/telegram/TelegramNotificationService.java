package de.malkusch.ha.notification.infrastructure.telegram;

import de.malkusch.ha.notification.model.Notification;
import de.malkusch.ha.notification.model.NotificationService;
import de.malkusch.ha.shared.infrastructure.telegram.TelegramApi;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class TelegramNotificationService implements NotificationService {

    private final TelegramApi telegram;

    @Override
    public void send(Notification notification) {
        telegram.send(notification.toString());
    }
}
