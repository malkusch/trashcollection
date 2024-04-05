package de.malkusch.ha.automation.model;

import static de.malkusch.ha.automation.model.NextTrashCollectionTests.nextTrashCollectionChanged;
import static de.malkusch.ha.test.TrashCollectionTests.trashCollection;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import de.malkusch.ha.automation.model.NextTrashCollection.NotNextException;
import de.malkusch.ha.automation.model.NextTrashCollection.TooFarInFutureException;
import de.malkusch.ha.shared.infrastructure.event.EventPublisherTests;
import de.malkusch.ha.test.MockedClock;

public class NextTrashCollectionTest {

    private final MockedClock mockedClock = new MockedClock();

    @ParameterizedTest
    @CsvSource({ //
            "2023-01-01, 2023-01-01, 2023-01-05/RO", //
            "2023-01-01, 2023-01-02, 2023-01-05/RO", //
            "2023-01-01, 2023-01-04, 2023-01-05/RO", //
            "2023-01-02, 2023-01-18, 2023-01-19/RO", //
            "2023-01-03, 2023-01-18, 2023-01-19/RO", //
            "2023-01-04, 2023-01-18, 2023-01-19/RO", //
    })
    public void doneShouldChangeNext(String first, String later, String expected) throws Exception {
        var next = nextTrashCollection(first);
        var beforeDone = next.nextTrashCollection();

        mockedClock.mockDate(later);
        next.done();

        var afterDone = next.nextTrashCollection();
        assertNotEquals(beforeDone, afterDone);
        assertEquals(trashCollection(expected), afterDone);
    }

    @ParameterizedTest
    @CsvSource({ //
            "2023-01-01, 2023-01-02/PP, 2023-01-05/RO", //
            "2023-01-04, 2023-01-05/RO, 2023-01-19/RO", //
            "2023-01-18, 2023-01-19/RO, 2023-01-30/PP", //
    })
    public void doneShouldChangeGivenNext(String now, String trashcollection, String expected) throws Exception {
        var next = nextTrashCollection(now);
        var beforeDone = next.nextTrashCollection();

        next.done(trashCollection(trashcollection));

        var afterDone = next.nextTrashCollection();
        assertNotEquals(beforeDone, afterDone);
        assertEquals(trashCollection(expected), afterDone);
    }

    @ParameterizedTest
    @CsvSource({ //
            "2023-01-02, 2023-01-02, 2023-01-05/RO", //
            "2023-01-02, 2023-01-03, 2023-01-05/RO", //
            "2023-01-03, 2023-01-03, 2023-01-05/RO", //

            "2023-01-05, 2023-01-05, 2023-01-19/RO", //
            "2023-01-05, 2023-01-06, 2023-01-19/RO", //
            "2023-01-05, 2023-01-17, 2023-01-19/RO", //

            "2023-01-06, 2023-01-06, 2023-01-19/RO", //
            "2023-01-06, 2023-01-17, 2023-01-19/RO", //

            "2023-01-17, 2023-01-17, 2023-01-19/RO", //
    })
    public void doneShouldNotChange(String now, String later, String expected) throws Exception {
        var next = nextTrashCollection(now);
        var beforeDone = next.nextTrashCollection();

        mockedClock.mockDate(later);
        assertThrows(TooFarInFutureException.class, next::done);

        var afterDone = next.nextTrashCollection();
        assertEquals(beforeDone, afterDone);
        assertEquals(trashCollection(expected), afterDone);
    }

    @ParameterizedTest
    @CsvSource({ //
            "2023-01-02, 2023-01-05/RO", //
            "2023-01-02, 2023-01-05/RO", //
            "2023-01-03, 2023-01-05/RO", //

            "2023-01-05, 2023-01-19/RO", //
            "2023-01-05, 2023-01-19/RO", //
            "2023-01-05, 2023-01-19/RO", //

            "2023-01-06, 2023-01-19/RO", //
            "2023-01-06, 2023-01-19/RO", //

            "2023-01-17, 2023-01-19/RO", //
    })
    public void doneShouldNotChangeWhenTooFarInFuture(String now, String trashcollection) throws Exception {
        var next = nextTrashCollection(now);
        var beforeDone = next.nextTrashCollection();

        assertThrows(TooFarInFutureException.class, () -> next.done(trashCollection(trashcollection)));

        var afterDone = next.nextTrashCollection();
        assertEquals(beforeDone, afterDone);
        assertEquals(trashCollection(trashcollection), beforeDone);
    }

    @ParameterizedTest
    @CsvSource({ //
            "2023-01-05, 2023-01-05/RO", //
            "2023-01-06, 2023-01-05/RO", //
            "2023-01-18, 2023-01-05/RO", //

            "2023-03-01, 2023-01-19/RO", //
            "2023-03-01, 2023-01-05/RO", //
    })
    public void doneShouldNotChangeWhenNotNext(String now, String trashcollection) throws Exception {
        var next = nextTrashCollection(now);
        var beforeDone = next.nextTrashCollection();

        assertThrows(NotNextException.class, () -> next.done(trashCollection(trashcollection)));

        var afterDone = next.nextTrashCollection();
        assertEquals(beforeDone, afterDone);
    }

    @ParameterizedTest
    @CsvSource({ //
            "2023-01-01, 2023-01-02, 2023-01-05/RO", //
            "2023-01-01, 2023-01-03, 2023-01-05/RO", //
            "2023-01-01, 2023-01-04, 2023-01-05/RO", //

            "2023-01-02, 2023-01-05, 2023-01-19/RO", //
            "2023-01-02, 2023-01-06, 2023-01-19/RO", //
            "2023-01-02, 2023-01-18, 2023-01-19/RO", //

            "2023-01-04, 2023-01-05, 2023-01-19/RO", //
            "2023-01-04, 2023-01-06, 2023-01-19/RO", //
            "2023-01-04, 2023-01-18, 2023-01-19/RO", //
    })
    public void checkNextChangedShouldChange(String now, String later, String expected) {
        var next = nextTrashCollection(now);
        var before = next.nextTrashCollection();

        mockedClock.mockDate(later);
        next.checkNextChanged();

        var after = next.nextTrashCollection();
        assertNotEquals(before, after);
        assertEquals(trashCollection(expected), after);
    }

    @Test
    public void checkNextChangedShouldPublishMessageOnNextDay() {
        var first = "2023-01-03";
        var next = nextTrashCollection(first);
        next.checkNextChanged();

        mockedClock.mockDate("2023-01-05");
        next.checkNextChanged();

        eventPublisherTests.assertEvent(nextTrashCollectionChanged("2023-01-19/RO"));
    }

    @Test
    public void doneShouldPublishMessage() throws Exception {
        var next = nextTrashCollection("2023-01-04");
        next.done(next.nextTrashCollection());

        eventPublisherTests.assertEvent(nextTrashCollectionChanged("2023-01-19/RO"));
    }

    @Test
    public void checkNextChangedShouldNotPublishMessageAfterDone() throws Exception {
        var next = nextTrashCollection("2023-01-04");
        next.done(next.nextTrashCollection());
        mockedClock.mockDate("2023-01-05");

        next.checkNextChanged();

        eventPublisherTests.assertEvent(nextTrashCollectionChanged("2023-01-19/RO"));
    }

    @ParameterizedTest
    @CsvSource({ //
            "2023-01-01, 2023-01-01, 2023-01-02/PP", //

            "2023-01-02, 2023-01-02, 2023-01-05/RO", //
            "2023-01-02, 2023-01-04, 2023-01-05/RO", //

            "2023-01-03, 2023-01-03, 2023-01-05/RO", //
            "2023-01-03, 2023-01-04, 2023-01-05/RO", //

            "2023-01-04, 2023-01-04, 2023-01-05/RO", //

            "2023-01-05, 2023-01-05, 2023-01-19/RO", //
            "2023-01-05, 2023-01-06, 2023-01-19/RO", //
            "2023-01-05, 2023-01-18, 2023-01-19/RO", //

            "2023-01-18, 2023-01-18, 2023-01-19/RO", //
    })
    public void checkNextChangedShouldNotChange(String now, String later, String expected) {
        var next = nextTrashCollection(now);
        var before = next.nextTrashCollection();

        mockedClock.mockDate(later);
        next.checkNextChanged();

        var after = next.nextTrashCollection();
        assertEquals(before, after);
        assertEquals(trashCollection(expected), after);
    }

    @Test
    public void checkNextAfterDoneShouldNotChange() throws Exception {
        var first = "2023-01-01";
        var next = nextTrashCollection(first);
        next.done(next.nextTrashCollection());
        var afterDone = next.nextTrashCollection();

        next.checkNextChanged();
        var afterCheck = next.nextTrashCollection();

        var expected = "2023-01-05/RO";
        assertEquals(afterDone, afterCheck);
        assertEquals(trashCollection(expected), afterCheck);
    }

    @RegisterExtension
    private final NextTrashCollectionTests nextTrashCollectionTests = new NextTrashCollectionTests(mockedClock);

    private NextTrashCollection nextTrashCollection(String now) {
        return nextTrashCollectionTests.nextTrashCollection(now);
    }

    @RegisterExtension
    private final EventPublisherTests eventPublisherTests = new EventPublisherTests();
}
