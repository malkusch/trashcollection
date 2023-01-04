package de.malkusch.ha.automation.infrastructure.ical;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.malkusch.ha.shared.infrastructure.http.HttpClient;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;

@Service
@Slf4j
public final class CalendarHttp {

    private final HttpClient http;
    private final String url;

    public CalendarHttp(HttpClient http, @Value("${trashday.url}") String url) {
        this.http = http;
        this.url = url;
    }

    public Calendar download() throws IOException, InterruptedException, ParserException {
        log.debug("Downloading {}", url);
        try (var response = http.get(url)) {
            return new CalendarBuilder().build(response.body);
        }
    }
}
