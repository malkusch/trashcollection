package de.malkusch.ha.notification.application;

import de.malkusch.ha.automation.model.NextTrashCollection.NextTrashCollectionChanged;
import de.malkusch.ha.notification.model.Notification.CallbackNotification;
import de.malkusch.ha.notification.model.Notification.SilentNotification;
import de.malkusch.ha.notification.model.NotificationService;
import de.malkusch.ha.shared.infrastructure.JsonConfiguration;
import de.malkusch.ha.shared.infrastructure.TrashCollectionFormatter;
import org.junit.jupiter.api.Test;

import static de.malkusch.ha.test.CheckTrashDayServiceTests.tomorrowsTrashDayNoticed;
import static de.malkusch.ha.test.TrashCollectionTests.trashCollection;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TrashCollectionNotificationApplicationServiceTest {

    private final NotificationService notificationService = mock(NotificationService.class);
    private final TrashCollectionNotificationApplicationService service = new TrashCollectionNotificationApplicationService(
            notificationService, new TrashCollectionFormatter(new JsonConfiguration().objectMapper()));

    @Test
    void onChangedShouldSendMessage() {
        service.onChanged(new NextTrashCollectionChanged(trashCollection("2023-01-12/RO")));

        verify(notificationService)
                .send(eq(new SilentNotification("Die nächste Müllabfuhr kommt am Do. 12.1.23: [ORGANIC, RESIDUAL]")));
    }

    @Test
    void onTrashDayShouldSendMessage() {
        service.onTrashDay(tomorrowsTrashDayNoticed("2023-01-12/RO"));

        verify(notificationService).send(argThat((CallbackNotification it) -> it.message()
                .equals("Morgen (Do. 12.1.23) kommt die Müllabfuhr: [ORGANIC, RESIDUAL]")));
    }
}
