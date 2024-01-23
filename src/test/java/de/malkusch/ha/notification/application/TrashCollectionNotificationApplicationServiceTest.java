package de.malkusch.ha.notification.application;

import static de.malkusch.ha.test.TrashCollectionTests.trashCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;

import de.malkusch.ha.automation.model.CheckTrashDayService.TomorrowsTrashDayNoticed;
import de.malkusch.ha.automation.model.NextTrashCollection.NextTrashCollectionChanged;
import de.malkusch.ha.notification.model.Notification;
import de.malkusch.ha.notification.model.NotificationService;

public class TrashCollectionNotificationApplicationServiceTest {

    private final NotificationService notificationService = mock(NotificationService.class);
    private final TrashCollectionNotificationApplicationService service = new TrashCollectionNotificationApplicationService(
            notificationService);

    @Test
    void onChangedShouldSendMessage() {
        service.onChanged(new NextTrashCollectionChanged(trashCollection("2023-01-12/RO")));

        verify(notificationService)
                .send(eq(new Notification("Die nächste Müllabfuhr kommt am Do. 12.1.23: [ORGANIC, RESIDUAL]")));
    }

    @Test
    void onTrashDayShouldSendMessage() {
        service.onTrashDay(new TomorrowsTrashDayNoticed(trashCollection("2023-01-12/RO")));

        verify(notificationService)
                .send(eq(new Notification("Morgen (Do. 12.1.23) kommt die Müllabfuhr: [ORGANIC, RESIDUAL]")));
    }
}
