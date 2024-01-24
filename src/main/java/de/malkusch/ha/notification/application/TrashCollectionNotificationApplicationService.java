package de.malkusch.ha.notification.application;

import static java.util.Locale.GERMANY;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import de.malkusch.ha.automation.application.ListNextCollectionsApplicationService.NextCollectionsListed;
import de.malkusch.ha.automation.application.PrintNextCollectionApplicationService.NextCollectionPrinted;
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
        var message = String.format("Die nächste Müllabfuhr kommt am %s: %s", //
                date(event.nextCollection), //
                trashCans(event.nextCollection));
        var notification = new Notification(message);
        notificationService.send(notification);
    }

    @EventListener
    public void onTrashDay(TomorrowsTrashDayNoticed event) {
        var message = String.format("Morgen (%s) kommt die Müllabfuhr: %s", //
                date(event.nextCollection), //
                trashCans(event.nextCollection));
        var notification = new Notification(message);
        notificationService.send(notification);
    }

    @EventListener
    public void onList(NextCollectionsListed event) {
        var message = event.next().stream() //
                .map(it -> trashCollection(it)) //
                .reduce((a, b) -> a + "\n" + b) //
                .orElse("keine Müllabfuhr");
        var notification = new Notification(message);
        notificationService.send(notification);
    }

    @EventListener
    public void onNext(NextCollectionPrinted event) {
        var message = trashCollection(event.next());
        var notification = new Notification(message);
        notificationService.send(notification);
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
