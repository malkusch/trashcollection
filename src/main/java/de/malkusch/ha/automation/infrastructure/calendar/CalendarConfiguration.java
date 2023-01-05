package de.malkusch.ha.automation.infrastructure.calendar;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CalendarConfiguration {

    @Bean
    InMemoryCalendarCache calendarCache(@Value("${calendar.file}") String path) throws IOException {
        return new InMemoryCalendarCache(path);
    }
}
