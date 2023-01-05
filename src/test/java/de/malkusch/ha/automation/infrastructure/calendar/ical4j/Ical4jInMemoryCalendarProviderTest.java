package de.malkusch.ha.automation.infrastructure.calendar.ical4j;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import de.malkusch.ha.automation.infrastructure.calendar.InMemoryCalendarCache;
import de.malkusch.ha.automation.infrastructure.calendar.InMemoryTrashCollectionCalendar.TrashCollections;
import de.malkusch.ha.shared.infrastructure.http.HttpClient;
import de.malkusch.ha.shared.infrastructure.http.HttpResponse;

public class Ical4jInMemoryCalendarProviderTest {

    public Ical4jInMemoryCalendarProvider provider(String file)
            throws IOException, InterruptedException, URISyntaxException {
        var http = mock(HttpClient.class);
        var url = "ANY";
        when(http.get(url)).then(it -> new HttpResponse(200, url, false, getClass().getResourceAsStream(file)));
        return new Ical4jInMemoryCalendarProvider(new DefaultMapper(), new Ical4jHttpFactory(http, url));
    }

    @ParameterizedTest
    @ValueSource(strings = { "2017", "2023" })
    public void shouldProvideCalendar(String year) throws Exception {
        var provider = provider(year + ".ics");

        var parsed = provider.fetch();

        var expected = fromJson(year + ".json");
        assertEquals(expected, parsed);
    }

    private TrashCollections fromJson(String file) throws IOException, URISyntaxException {
        return new InMemoryCalendarCache(Paths.get(getClass().getResource(file).toURI())).load();
    }
}
