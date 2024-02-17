package de.malkusch.ha.automation.model;

import static de.malkusch.ha.test.TrashCollectionTests.trashCollection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import de.malkusch.ha.automation.model.CheckTrashDayService.TomorrowsTrashDayNoticed;
import de.malkusch.ha.shared.infrastructure.event.EventPublisherTests;
import de.malkusch.ha.test.MockedClock;

public class CheckTrashDayServiceTest {

    private NextTrashCollection nextTrashCollection;
    private CheckTrashDayService checkTrashDayService;

    @ParameterizedTest
    @ValueSource(strings = { "2023-01-02", "2023-01-03", "2023-01-05", "2023-01-06" })
    public void shouldNotPublishWhenCollectionIsNotTomorrow(String now) {
        setTime(now);

        checkTrashDayService.checkTomorrow();

        eventPublisherTests.assertNoEvent();
    }

    @ParameterizedTest
    @CsvSource({ //
            "2023-01-04, 2023-01-05/RO", //
            "2023-01-18, 2023-01-19/RO", //
    })
    public void shouldPublishWhenCollectionIsTomorrow(String now, String expected) {
        setTime(now);

        checkTrashDayService.checkTomorrow();

        eventPublisherTests.assertEvent(tomorrowsTrashDayNoticed(expected));
    }

    @Test
    public void shouldContinuePublishing() {
        setTime("2023-01-04");

        checkTrashDayService.checkTomorrow();
        checkTrashDayService.checkTomorrow();

        eventPublisherTests.assertEvents(2, tomorrowsTrashDayNoticed("2023-01-05/RO"));
    }

    @Test
    public void shouldStopPublishingAfterDone() throws Exception {
        setTime("2023-01-04");
        checkTrashDayService.checkTomorrow();
        checkTrashDayService.checkTomorrow();

        nextTrashCollection.done(trashCollection("2023-01-05/RO"));
        checkTrashDayService.checkTomorrow();

        eventPublisherTests.assertEvents(2, tomorrowsTrashDayNoticed("2023-01-05/RO"));
    }

    @ParameterizedTest
    @ValueSource(strings = { "2023-01-01/RO", "2023-01-02/RO" })
    public void shouldNotStopPublishingAfterDoneWrong(String done) {
        setTime("2023-01-04");
        checkTrashDayService.checkTomorrow();
        checkTrashDayService.checkTomorrow();

        try {
            nextTrashCollection.done(trashCollection(done));
        } catch (Exception e) {
            // Ignore
        }
        checkTrashDayService.checkTomorrow();

        eventPublisherTests.assertEvents(3, tomorrowsTrashDayNoticed("2023-01-05/RO"));
    }

    private final MockedClock mockedClock = new MockedClock();

    @RegisterExtension
    private final NextTrashCollectionTests nextTrashCollectionTests = new NextTrashCollectionTests(mockedClock);

    @RegisterExtension
    private final EventPublisherTests eventPublisherTests = new EventPublisherTests();

    private final void setTime(String now) {
        nextTrashCollection = nextTrashCollectionTests.nextTrashCollection(now);
        checkTrashDayService = new CheckTrashDayService(nextTrashCollection, mockedClock.clock);
    }

    private static TomorrowsTrashDayNoticed tomorrowsTrashDayNoticed(String trashCollection) {
        return new TomorrowsTrashDayNoticed(trashCollection(trashCollection));
    }
}
