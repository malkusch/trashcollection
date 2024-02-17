package de.malkusch.ha.automation.model;

import static de.malkusch.ha.shared.infrastructure.event.EventPublisherTests.ignoreEvents;
import static de.malkusch.ha.test.TrashCollectionTests.trashCollection;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import de.malkusch.ha.test.CalenderTests;
import de.malkusch.ha.test.MockedClock;
import de.malkusch.ha.test.TestCalendar;

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
        next.done();

        var afterDone = next.nextTrashCollection();
        assertEquals(beforeDone, afterDone);
        assertEquals(trashCollection(expected), afterDone);
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

    private NextTrashCollection nextTrashCollection(String now) {
        mockedClock.mockDate(now);
        ignoreEvents();
        var calendar = CalenderTests.calendar(TestCalendar.CALENDAR_2023);
        var next = new NextTrashCollection(calendar, mockedClock.clock);
        return next;
    }
}
