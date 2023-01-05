package de.malkusch.ha.automation.infrastructure.ical;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.fortuna.ical4j.data.ParserException;

@Configuration
public class CalendarConfiguration {

    @Bean
    CalendarFile calendarFile(@Value("${calendar.file}") String path) throws IOException {
        return new CalendarFile(path);
    }

    @Bean
    CalendarIO calendarIO(CalendarHttp http, CalendarFile file)
            throws IOException, InterruptedException, ParserException {

        return new CalendarIO(http, file);
    }
}
