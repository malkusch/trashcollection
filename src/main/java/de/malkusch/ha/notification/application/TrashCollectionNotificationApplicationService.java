package de.malkusch.ha.notification.application;

import de.malkusch.ha.automation.model.CheckTrashDayService.TomorrowsTrashDayNoticed;
import de.malkusch.ha.automation.model.CheckTrashDayService.TomorrowsTrashDayReminded;
import de.malkusch.ha.automation.model.NextTrashCollection.NextTrashCollectionChanged;
import de.malkusch.ha.automation.model.TrashCollection;
import de.malkusch.ha.notification.model.Notification.CallbackNotification;
import de.malkusch.ha.notification.model.Notification.CallbackNotification.Callback;
import de.malkusch.ha.notification.model.Notification.SilentNotification;
import de.malkusch.ha.notification.model.Notification.TextNotification;
import de.malkusch.ha.notification.model.NotificationService;
import de.malkusch.ha.shared.infrastructure.TrashCollectionFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import static de.malkusch.ha.shared.infrastructure.TrashCollectionFormatter.date;
import static de.malkusch.ha.shared.infrastructure.TrashCollectionFormatter.trashCans;

@Service
@RequiredArgsConstructor
public final class TrashCollectionNotificationApplicationService {

    private final NotificationService notificationService;

    @EventListener
    public void onChanged(NextTrashCollectionChanged event) {
        var message = String.format("Die nächste Müllabfuhr kommt am %s: %s", //
                date(event.nextCollection()), //
                trashCans(event.nextCollection()));
        var notification = new SilentNotification(message);
        notificationService.send(notification);
    }

    @EventListener
    public void onTrashDay(TomorrowsTrashDayNoticed event) {
        var message = String.format("Morgen (%s) kommt die Müllabfuhr: %s", //
                date(event.nextCollection()), //
                trashCans(event.nextCollection()));
        var done = done(event.nextCollection());
        var notification = new CallbackNotification(message, done);
        notificationService.send(notification);
    }

    @EventListener
    public void onTrashDayReminder(TomorrowsTrashDayReminded event) {
        var message = "Reminder: Der Müll ist noch nicht erledigt";
        var notification = new TextNotification(message);
        notificationService.send(notification);
    }

    private final TrashCollectionFormatter trashCollectionFormatter;

    private Callback done(TrashCollection trashCollection) {
        return new Callback("Erledigt", trashCollectionFormatter.json(trashCollection));
    }
}
