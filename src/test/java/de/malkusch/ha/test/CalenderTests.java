package de.malkusch.ha.test;

import static de.malkusch.ha.test.HttpTests.http;
import static de.malkusch.ha.test.HttpTests.httpFactory;

import java.io.IOException;

import de.malkusch.ha.automation.infrastructure.calendar.CalendarConfiguration;
import de.malkusch.ha.automation.infrastructure.calendar.ical4j.DefaultMapper;
import de.malkusch.ha.automation.infrastructure.calendar.ical4j.Ical4jCalendarProvider;
import de.malkusch.ha.automation.model.TrashCollectionCalendar;

public final class CalenderTests {

    public static Ical4jCalendarProvider provider(UriMapper mapper) {
        return new Ical4jCalendarProvider(new DefaultMapper(), httpFactory(mapper));
    }

    public static TrashCollectionCalendar calendar(TestCalendar calendar) {
        var clock = new MockedClock(calendar.beginOfYear()).clock;
        var http = http(calendar);
        var properties = new CalendarConfiguration.CalendarProperties("/tmp/trash-calendar-test", "http://example.org");
        try {
            return CalendarConfiguration.calendar(clock, http, properties);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
