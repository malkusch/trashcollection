package de.malkusch.ha.automation.model;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extension;

import de.malkusch.ha.shared.infrastructure.event.EventPublisherTests;
import de.malkusch.ha.test.CalenderTests;
import de.malkusch.ha.test.MockedClock;
import de.malkusch.ha.test.TestCalendar;

@ExtendWith(EventPublisherTests.class)
public class NextTrashCollectionTests implements Extension {

    private final MockedClock mockedClock;

    public NextTrashCollectionTests(MockedClock mockedClock) {
        this.mockedClock = mockedClock;
    }

    public NextTrashCollectionTests() {
        this(new MockedClock());
    }

    public NextTrashCollection nextTrashCollection(String now) {
        mockedClock.mockDate(now);
        var calendar = CalenderTests.calendar(TestCalendar.CALENDAR_2023);
        var next = new NextTrashCollection(calendar, mockedClock.clock);
        return next;
    }
}
