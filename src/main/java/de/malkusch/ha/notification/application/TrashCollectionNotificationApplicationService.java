package de.malkusch.ha.notification.application;

import static java.util.Locale.GERMANY;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import de.malkusch.ha.automation.application.ListNextCollectionsApplicationService.NextCollectionsListed;
import de.malkusch.ha.automation.application.PrintNextCollectionApplicationService.NextCollectionPrinted;
import de.malkusch.ha.automation.model.CheckTrashDayService.TomorrowsTrashDayNoticed;
import de.malkusch.ha.automation.model.CheckTrashDayService.TomorrowsTrashDayReminded;
import de.malkusch.ha.automation.model.NextTrashCollection.NextTrashCollectionChanged;
import de.malkusch.ha.automation.model.TrashCollection;
import de.malkusch.ha.notification.model.Notification.CallbackNotification;
import de.malkusch.ha.notification.model.Notification.CallbackNotification.Callback;
import de.malkusch.ha.notification.model.Notification.TextNotification;
import de.malkusch.ha.notification.model.NotificationService;
import de.malkusch.ha.shared.infrastructure.TrashCollectionFormatter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public final class TrashCollectionNotificationApplicationService {

    private final NotificationService notificationService;

    @EventListener
    public void onChanged(NextTrashCollectionChanged event) {
        var message = String.format("Die nächste Müllabfuhr kommt am %s: %s", //
                date(event.nextCollection), //
                trashCans(event.nextCollection));
        var notification = new TextNotification(message);
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

    @EventListener
    public void onList(NextCollectionsListed event) {
        var message = event.next().stream() //
                .map(it -> trashCollection(it)) //
                .reduce((a, b) -> a + "\n" + b) //
                .orElse("keine Müllabfuhr");
        var notification = new TextNotification(message);
        notificationService.send(notification);
    }

    @EventListener
    public void onNext(NextCollectionPrinted event) {
        var message = trashCollection(event.next());
        var done = done(event.next());
        var notification = new CallbackNotification(message, done);
        notificationService.send(notification);
    }

    private final TrashCollectionFormatter trashCollectionFormatter;

    private Callback done(TrashCollection trashCollection) {
        return new Callback("Erledigt", trashCollectionFormatter.format(trashCollection));
    }

    private static String trashCollection(TrashCollection trashCollection) {
        return String.format("%s:\t%s", //
                date(trashCollection), //
                trashCans(trashCollection));
    }

    private static String trashCans(TrashCollection trashCollection) {
        var cans = trashCollection.trashCans().stream().sorted().toArray();
        return Arrays.toString(cans);
    }

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("E d.M.uu", GERMANY);

    private static String date(TrashCollection trashCollection) {
        return DATE_FORMAT.format(trashCollection.date());
    }
}
