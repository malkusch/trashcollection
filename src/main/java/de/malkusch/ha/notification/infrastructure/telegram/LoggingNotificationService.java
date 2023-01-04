package de.malkusch.ha.notification.infrastructure.telegram;

import de.malkusch.ha.notification.model.Notification;
import de.malkusch.ha.notification.model.NotificationService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
final class LoggingNotificationService implements NotificationService {

    @Override
    public void send(Notification notification) {
        log.info(notification.toString());
    }
}
