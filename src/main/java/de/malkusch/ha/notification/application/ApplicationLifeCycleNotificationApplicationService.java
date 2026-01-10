package de.malkusch.ha.notification.application;

import de.malkusch.ha.notification.model.Notification.SilentNotification;
import de.malkusch.ha.notification.model.NotificationService;
import org.springframework.stereotype.Service;

@Service
public final class ApplicationLifeCycleNotificationApplicationService implements AutoCloseable {

    private final NotificationService notifier;

    public ApplicationLifeCycleNotificationApplicationService(NotificationService notifier) {
        this.notifier = notifier;
        notifier.send(new SilentNotification("Trash application started"));
    }

    @Override
    public void close() {
        notifier.send(new SilentNotification("Trash application stopped"));
    }
}