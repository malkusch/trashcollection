package de.malkusch.ha.notification.application;

import org.springframework.stereotype.Service;

import de.malkusch.ha.notification.model.Notification.TextNotification;
import de.malkusch.ha.notification.model.NotificationService;

@Service
public final class ApplicationLifeCycleNotificationApplicationService implements AutoCloseable {

    private final NotificationService notifier;

    public ApplicationLifeCycleNotificationApplicationService(NotificationService notifier) {
        this.notifier = notifier;
        notifier.send(new TextNotification("Trash application started"));
    }

    @Override
    public void close() throws Exception {
        notifier.send(new TextNotification("Trash application stopped"));
    }
}