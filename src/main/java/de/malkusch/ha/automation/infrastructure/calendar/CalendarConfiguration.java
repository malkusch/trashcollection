package de.malkusch.ha.automation.infrastructure.calendar;

import java.io.IOException;
import java.time.Clock;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import de.malkusch.ha.automation.infrastructure.calendar.ical4j.DefaultMapper;
import de.malkusch.ha.automation.infrastructure.calendar.ical4j.Ical4jCalendarProvider;
import de.malkusch.ha.automation.infrastructure.calendar.ical4j.Ical4jHttpFactory;
import de.malkusch.ha.automation.model.TrashCollectionCalendar;
import de.malkusch.ha.shared.infrastructure.http.HttpClient;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class CalendarConfiguration {

    @Data
    @Component
    @ConfigurationProperties("calendar")
    static class Properties {
        private String cache;
        private String url;
    }

    @ConfigurationProperties("calendar")
    public static record CalendarProperties(String cache, String url) {
    }

    private final HttpClient http;
    private final Clock clock;
    private final Properties properties;

    public static InMemoryTrashCollectionCalendar calendar(Clock clock, HttpClient http, CalendarProperties properties)
            throws IOException {

        var icalMapper = new DefaultMapper();
        var icalFactory = new Ical4jHttpFactory(http, properties.url);
        var ical4jProvider = new Ical4jCalendarProvider(icalMapper, icalFactory);

        var rolloverProvider = new RolloverCalendarProvider(ical4jProvider);

        var cache = new InMemoryCalendarCache(properties.cache);

        return new InMemoryTrashCollectionCalendar(rolloverProvider, cache, clock);
    }

    @Bean
    TrashCollectionCalendar calendar() throws IOException {
        return calendar(clock, http, new CalendarProperties(properties.cache, properties.url));
    }
}
