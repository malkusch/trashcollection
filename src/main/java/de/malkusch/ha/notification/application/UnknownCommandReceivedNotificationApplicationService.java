package de.malkusch.ha.notification.application;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import de.malkusch.ha.notification.model.Notification;
import de.malkusch.ha.notification.model.NotificationService;
import de.malkusch.ha.shared.infrastructure.telegram.CommandDispatcher.UnkownCommandReceived;
import de.malkusch.ha.shared.infrastructure.telegram.CommandParser.ReactionMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public final class UnknownCommandReceivedNotificationApplicationService {

    private final NotificationService notificationService;

    @EventListener
    public void onUnknown(UnkownCommandReceived event) {
        if (event.command() instanceof ReactionMessage) {
            return;
        }
        var message = String.format("Unbekannter Befehl: %s\n\n%s", event.command(), event.help());
        var notification = new Notification(message);
        notificationService.send(notification);
    }
}
