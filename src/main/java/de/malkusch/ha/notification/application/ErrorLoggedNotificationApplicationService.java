package de.malkusch.ha.notification.application;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import de.malkusch.ha.notification.model.Notification;
import de.malkusch.ha.notification.model.NotificationService;
import de.malkusch.ha.shared.infrastructure.event.ErrorLogged;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public final class ErrorLoggedNotificationApplicationService {

    private final NotificationService notificationService;

    @EventListener
    public void onError(ErrorLogged event) {
        var message = String.format("Fehler [%s]: %s", event.reference(), event.message());
        var notification = new Notification(message);
        notificationService.send(notification);
    }

}
