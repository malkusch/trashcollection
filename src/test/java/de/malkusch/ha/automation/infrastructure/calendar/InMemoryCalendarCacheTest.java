package de.malkusch.ha.automation.infrastructure.calendar;

import static java.nio.file.Files.deleteIfExists;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.malkusch.ha.automation.infrastructure.calendar.InMemoryTrashCollectionCalendar.InMemoryCalendarProvider;
import de.malkusch.ha.automation.infrastructure.calendar.ical4j.DefaultMapper;
import de.malkusch.ha.automation.infrastructure.calendar.ical4j.Ical4jHttpFactory;
import de.malkusch.ha.automation.infrastructure.calendar.ical4j.Ical4jInMemoryCalendarProvider;
import de.malkusch.ha.shared.infrastructure.http.HttpClient;
import de.malkusch.ha.shared.infrastructure.http.HttpResponse;

public class InMemoryCalendarCacheTest {

    private final static Path FILE = Paths.get("/tmp/CalendarFileTest-loadAndStoreShouldBeEqual");
    private InMemoryCalendarCache cache;
    private InMemoryCalendarProvider provider;

    @BeforeEach
    @AfterEach
    public void deleteFile() throws IOException, InterruptedException {
        deleteIfExists(FILE);
    }

    @BeforeEach
    public void setup() throws IOException, InterruptedException {
        var http = mock(HttpClient.class);
        var url = "ANY";
        when(http.get(url)).then(it -> new HttpResponse(200, url, false, getClass().getResourceAsStream("2017.ics")));
        provider = new Ical4jInMemoryCalendarProvider(new DefaultMapper(), new Ical4jHttpFactory(http, url));
        cache = new InMemoryCalendarCache(FILE);
    }

    @Test
    public void loadAndStoreShouldBeEqual() throws IOException, InterruptedException {
        var source = provider.fetch();
        cache.store(source);

        var copy = cache.load();

        assertEquals(source, copy);
    }

}
