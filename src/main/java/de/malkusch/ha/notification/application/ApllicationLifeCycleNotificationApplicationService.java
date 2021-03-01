package de.malkusch.ha.notification.application;

import org.springframework.stereotype.Service;

import de.malkusch.ha.notification.model.Notification;
import de.malkusch.ha.notification.model.NotificationService;

@Service
public final class ApllicationLifeCycleNotificationApplicationService implements AutoCloseable {

    private final NotificationService notifier;

    public ApllicationLifeCycleNotificationApplicationService(NotificationService notifier) {
        this.notifier = notifier;
        notifier.send(new Notification("Trash application started"));
    }

    @Override
    public void close() throws Exception {
        notifier.send(new Notification("Trash application stopped"));
    }
}