package de.malkusch.ha.notification.application;

import static java.util.Locale.GERMANY;

import java.time.format.DateTimeFormatter;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import de.malkusch.ha.automation.model.CheckTrashDayService.TomorrowsTrashDayNoticed;
import de.malkusch.ha.automation.model.NextTrashCollection.NextTrashCollectionChanged;
import de.malkusch.ha.automation.model.TrashCollection;
import de.malkusch.ha.notification.model.Notification;
import de.malkusch.ha.notification.model.NotificationService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public final class TrashCollectionNotificationApplicationService {

    private final NotificationService notificationService;

    @EventListener
    public void onChanged(NextTrashCollectionChanged event) {
        var message = String.format("Die nächste Müllabfuhr kommt am %s:\n%s", //
                date(event.nextCollection), //
                event.nextCollection.trashCans());
        var notification = new Notification(message);
        notificationService.send(notification);
    }

    @EventListener
    public void onTrashDay(TomorrowsTrashDayNoticed event) {
        var message = String.format("Morgen (%s) kommt die Müllabfuhr:\n%s", //
                date(event.nextCollection), //
                event.nextCollection.trashCans());
        var notification = new Notification(message);
        notificationService.send(notification);
    }

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("E d.M.uu", GERMANY);

    private static String date(TrashCollection trashCollection) {
        return DATE_FORMAT.format(trashCollection.date());
    }
}
