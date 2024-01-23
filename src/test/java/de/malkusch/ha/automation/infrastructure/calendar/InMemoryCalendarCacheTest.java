package de.malkusch.ha.automation.infrastructure.calendar;

import static de.malkusch.ha.test.CalenderTests.provider;
import static de.malkusch.ha.test.UriMapper.forDate;
import static java.nio.file.Files.deleteIfExists;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import de.malkusch.ha.test.TestCalendar;
import de.malkusch.ha.test.TestCalendar.TestCalendars;

public class InMemoryCalendarCacheTest {

    private final static Path FILE = Paths.get("/tmp/CalendarFileTest-loadAndStoreShouldBeEqual");
    private InMemoryCalendarCache cache;

    @BeforeEach
    @AfterEach
    public void deleteFile() throws IOException, InterruptedException {
        deleteIfExists(FILE);
    }

    @BeforeEach
    public void setup() throws IOException, InterruptedException {
        cache = new InMemoryCalendarCache(FILE);
    }

    private static Stream<LocalDate> loadAndStoreShouldBeEqual() {
        return TestCalendars.ALL.testCases(TestCalendar::beginOfYear, TestCalendar::endOfYear);
    }

    @ParameterizedTest
    @MethodSource
    public void loadAndStoreShouldBeEqual(LocalDate fetchDate) throws IOException, InterruptedException {
        var provider = provider(forDate(fetchDate));
        var source = provider.fetch(fetchDate);
        cache.store(source);

        var copy = cache.load();

        assertEquals(source, copy);
    }
}
