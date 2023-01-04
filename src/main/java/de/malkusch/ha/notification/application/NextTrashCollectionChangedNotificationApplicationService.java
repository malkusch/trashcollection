package de.malkusch.ha.notification.application;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import de.malkusch.ha.automation.model.NextTrashCollection.NextTrashCollectionChanged;
import de.malkusch.ha.notification.model.Notification;
import de.malkusch.ha.notification.model.NotificationService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public final class NextTrashCollectionChangedNotificationApplicationService {

    private final NotificationService notificationService;

    @EventListener
    public void onTrashDay(NextTrashCollectionChanged event) {
        var message = String.format("Die nächste Müllabfuhr kommt am %s: %s", event.nextCollection.date(),
                event.nextCollection.getTrashCans());
        var notification = new Notification(message);
        notificationService.send(notification);
    }
}
