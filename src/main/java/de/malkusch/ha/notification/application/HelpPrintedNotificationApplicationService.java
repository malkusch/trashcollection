package de.malkusch.ha.notification.application;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import de.malkusch.ha.notification.model.Notification;
import de.malkusch.ha.notification.model.NotificationService;
import de.malkusch.ha.shared.infrastructure.telegram.HelpService.HelpPrinted;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public final class HelpPrintedNotificationApplicationService {

    private final NotificationService notificationService;

    @EventListener
    public void onHelp(HelpPrinted event) {
        var message = event.commands().stream() //
                .map(it -> it.toString()) //
                .reduce((a, b) -> a + "\n" + b) //
                .orElseThrow();
        var notification = new Notification(message);
        notificationService.send(notification);
    }
}
