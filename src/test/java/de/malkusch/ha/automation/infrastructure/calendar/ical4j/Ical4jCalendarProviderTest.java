package de.malkusch.ha.automation.infrastructure.calendar.ical4j;

import static de.malkusch.ha.test.HttpTests.httpFactory;
import static de.malkusch.ha.test.TrashCollectionTests.fromJson;
import static de.malkusch.ha.test.UriMapper.forDate;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import de.malkusch.ha.test.TestCalendar;
import de.malkusch.ha.test.TestCalendar.TestCalendars;

public class Ical4jCalendarProviderTest {

    public Ical4jCalendarProvider provider(LocalDate fetchDate) throws Exception {
        var http = httpFactory(forDate(fetchDate));
        return new Ical4jCalendarProvider(new DefaultMapper(), http);
    }

    private static Stream<LocalDate> shouldParseTrashCollections() {
        return TestCalendars.ALL.testCases(TestCalendar::beginOfYear, TestCalendar::endOfYear);
    }

    @ParameterizedTest
    @MethodSource
    public void shouldParseTrashCollections(LocalDate fetchDate) throws Exception {
        var provider = provider(fetchDate);

        var parsed = provider.fetch(fetchDate);

        var expected = fromJson(fetchDate);
        assertEquals(expected, parsed);
    }
}
