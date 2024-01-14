package de.malkusch.ha.automation.infrastructure.calendar.ical4j;

import java.io.IOException;
import java.time.Year;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.damnhandy.uri.template.UriTemplate;

import de.malkusch.ha.shared.infrastructure.http.HttpClient;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;

@Service
@Slf4j
public final class Ical4jHttpFactory {

    private final HttpClient http;
    private final UriTemplate uriTemplate;

    public Ical4jHttpFactory(HttpClient http, @Value("${trashday.url}") String uriTemplate) {
        this.http = http;
        this.uriTemplate = UriTemplate.fromTemplate(uriTemplate);
    }

    public Calendar download() throws IOException, InterruptedException {
        var url = url(Year.now());
        log.debug("Downloading {}", url);
        try (var response = http.get(url)) {
            return new CalendarBuilder().build(response.body);
        } catch (ParserException e) {
            throw new IOException("Can't parse response of " + url, e);
        }
    }

    private String url(Year year) {
        return uriTemplate.set("year", year.getValue()).expand();
    }
}
