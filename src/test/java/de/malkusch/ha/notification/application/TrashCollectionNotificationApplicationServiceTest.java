package de.malkusch.ha.notification.application;

import static de.malkusch.ha.test.TrashCollectionTests.trashCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.malkusch.ha.automation.model.CheckTrashDayService.TomorrowsTrashDayNoticed;
import de.malkusch.ha.automation.model.NextTrashCollection.NextTrashCollectionChanged;
import de.malkusch.ha.notification.model.Notification.TextNotification;
import de.malkusch.ha.notification.model.NotificationService;
import de.malkusch.ha.shared.infrastructure.TrashCollectionFormatter;

public class TrashCollectionNotificationApplicationServiceTest {

    private final NotificationService notificationService = mock(NotificationService.class);
    private final TrashCollectionNotificationApplicationService service = new TrashCollectionNotificationApplicationService(
            notificationService, new TrashCollectionFormatter(new ObjectMapper()));

    @Test
    void onChangedShouldSendMessage() {
        service.onChanged(new NextTrashCollectionChanged(trashCollection("2023-01-12/RO")));

        verify(notificationService)
                .send(eq(new TextNotification("Die nächste Müllabfuhr kommt am Do. 12.1.23: [ORGANIC, RESIDUAL]")));
    }

    @Test
    void onTrashDayShouldSendMessage() {
        service.onTrashDay(new TomorrowsTrashDayNoticed(trashCollection("2023-01-12/RO")));

        verify(notificationService)
                .send(eq(new TextNotification("Morgen (Do. 12.1.23) kommt die Müllabfuhr: [ORGANIC, RESIDUAL]")));
    }
}
