package de.malkusch.ha.test;

import static de.malkusch.ha.test.HttpTests.httpFactory;

import de.malkusch.ha.automation.infrastructure.calendar.ical4j.DefaultMapper;
import de.malkusch.ha.automation.infrastructure.calendar.ical4j.Ical4jCalendarProvider;

public final class CalenderTests {

    public static Ical4jCalendarProvider provider(UriMapper mapper) {
        return new Ical4jCalendarProvider(new DefaultMapper(), httpFactory(mapper));
    }
}
