package de.malkusch.ha.automation.model;

import static de.malkusch.ha.test.CheckTrashDayServiceTests.tomorrowsTrashDayNoticed;
import static de.malkusch.ha.test.CheckTrashDayServiceTests.tomorrowsTrashDayReminded;
import static de.malkusch.ha.test.TrashCollectionTests.trashCollection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import de.malkusch.ha.shared.infrastructure.event.EventPublisherTests;
import de.malkusch.ha.test.MockedClock;

public class CheckTrashDayServiceTest {

    private NextTrashCollection nextTrashCollection;
    private final CheckTrashDayService checkTrashDayService = new CheckTrashDayService();

    @ParameterizedTest
    @ValueSource(strings = { "2023-01-02", "2023-01-03", "2023-01-05", "2023-01-06" })
    public void shouldNotPublishWhenCollectionIsNotTomorrow(String now) {
        setTime(now);

        checkTrashDayService.check(nextTrashCollection);

        eventPublisherTests.assertNoEvent();
    }

    @ParameterizedTest
    @CsvSource({ //
            "2023-01-04, 2023-01-05/RO", //
            "2023-01-18, 2023-01-19/RO", //
    })
    public void shouldNoticeNextWhenCollectionIsTomorrow(String now, String expected) {
        setTime(now);

        checkTrashDayService.check(nextTrashCollection);

        eventPublisherTests.assertEvent(tomorrowsTrashDayNoticed(expected));
    }

    @Test
    public void shouldNoticeWhenNextCollectionIsOnNextDayWithoutDone() throws Exception {
        setTime("2023-10-24");
        checkTrashDayService.check(nextTrashCollection);

        setTime("2023-10-25");
        checkTrashDayService.check(nextTrashCollection);

        eventPublisherTests.assertEvent(tomorrowsTrashDayNoticed("2023-10-26/RO"));
    }

    @Test
    public void shouldNoticeWhenNextCollectionIsOnNextDayWithDone() throws Exception {
        setTime("2023-10-24");
        checkTrashDayService.check(nextTrashCollection);
        nextTrashCollection.done(nextTrashCollection.nextTrashCollection());

        setTime("2023-10-25");
        checkTrashDayService.check(nextTrashCollection);

        eventPublisherTests.assertEvent(tomorrowsTrashDayNoticed("2023-10-26/RO"));
    }

    @Test
    public void shouldRemind() {
        setTime("2023-01-04");
        checkTrashDayService.check(nextTrashCollection);

        checkTrashDayService.check(nextTrashCollection);
        checkTrashDayService.check(nextTrashCollection);

        eventPublisherTests.assertEvents(2, tomorrowsTrashDayReminded("2023-01-05/RO"));
    }

    @Test
    public void shouldNoticeOnlyOnce() {
        setTime("2023-01-04");

        checkTrashDayService.check(nextTrashCollection);
        checkTrashDayService.check(nextTrashCollection);

        eventPublisherTests.assertEvent(tomorrowsTrashDayNoticed("2023-01-05/RO"));
    }

    @Test
    public void shouldStopRemindingAfterDoneAfterNotice() throws Exception {
        setTime("2023-01-04");
        checkTrashDayService.check(nextTrashCollection);

        nextTrashCollection.done(trashCollection("2023-01-05/RO"));
        checkTrashDayService.check(nextTrashCollection);

        eventPublisherTests.assertEvents(1, tomorrowsTrashDayNoticed("2023-01-05/RO"));
        eventPublisherTests.assertEvents(0, tomorrowsTrashDayReminded("2023-01-05/RO"));
    }

    @Test
    public void shouldStopRemindingAfterDoneAfterRemind() throws Exception {
        setTime("2023-01-04");
        checkTrashDayService.check(nextTrashCollection);
        checkTrashDayService.check(nextTrashCollection);

        nextTrashCollection.done(trashCollection("2023-01-05/RO"));
        checkTrashDayService.check(nextTrashCollection);

        eventPublisherTests.assertEvents(1, tomorrowsTrashDayNoticed("2023-01-05/RO"));
        eventPublisherTests.assertEvents(1, tomorrowsTrashDayReminded("2023-01-05/RO"));
    }

    @ParameterizedTest
    @ValueSource(strings = { "2023-01-01/RO", "2023-01-02/RO" })
    public void shouldNotStopRemindingAfterDoneWrong(String done) {
        setTime("2023-01-04");
        checkTrashDayService.check(nextTrashCollection);
        checkTrashDayService.check(nextTrashCollection);

        try {
            nextTrashCollection.done(trashCollection(done));
        } catch (Exception e) {
            // Ignore
        }
        checkTrashDayService.check(nextTrashCollection);

        eventPublisherTests.assertEvents(2, tomorrowsTrashDayReminded("2023-01-05/RO"));
    }

    private final MockedClock mockedClock = new MockedClock();

    @RegisterExtension
    private final NextTrashCollectionTests nextTrashCollectionTests = new NextTrashCollectionTests(mockedClock);

    @RegisterExtension
    private final EventPublisherTests eventPublisherTests = new EventPublisherTests();

    private final void setTime(String now) {
        nextTrashCollection = nextTrashCollectionTests.nextTrashCollection(now);
    }
}
