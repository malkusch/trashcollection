package de.malkusch.ha.test;

import static de.malkusch.ha.test.TestCalendar.CALENDAR_2023;
import static de.malkusch.ha.test.UriMapper.forCalendar;
import static de.malkusch.ha.test.UriMapper.forDate;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;

import de.malkusch.ha.automation.infrastructure.calendar.ical4j.Ical4jHttpFactory;
import de.malkusch.ha.shared.infrastructure.http.HttpClient;
import de.malkusch.ha.shared.infrastructure.http.HttpResponse;

public final class HttpTests {

    public final static String URI_TEMPLATE = "http://example.org?year={year}";

    public static Ical4jHttpFactory httpFactory(UriMapper uriMapper) {
        return httpFactory(uriMapper, URI_TEMPLATE);
    }

    public static Ical4jHttpFactory httpFactory(UriMapper uriMapper, String uriTemplate) {
        var http = http(uriMapper);
        return new Ical4jHttpFactory(http, uriTemplate);
    }

    public static HttpClient http() {
        return http(CALENDAR_2023);
    }

    public static HttpClient http(LocalDate fetchDate) {
        return http(forDate(fetchDate));
    }

    public static HttpClient http(TestCalendar calendar) {
        return http(forCalendar(calendar));
    }

    static HttpClient http(UriMapper uriMapper) {
        var http = mock(HttpClient.class);
        try {
            when(http.get(anyString())).thenAnswer(it -> {
                String url = it.getArgument(0);
                var uri = URI.create(url);
                var calendar = uriMapper.calendar(uri);
                return new HttpResponse(200, url, false, calendar.ics().stream());
            });

        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException(e);
        }
        return http;
    }
}
