package de.malkusch.ha.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;

public class MockedClock {

    public final Clock clock = mock(Clock.class);

    public MockedClock() {
    }

    public MockedClock(LocalDate date) {
        mockDate(date);
    }

    public MockedClock(String date) {
        mockDate(date);
    }

    public void mockDate(String date) {
        mockDate(LocalDate.parse(date));
    }

    public void mockDate(LocalDate date) {
        var instant = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
        when(clock.instant()).thenReturn(instant);
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    }
}
