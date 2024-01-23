package de.malkusch.ha.automation.infrastructure.calendar.ical4j;

import java.io.IOException;
import java.time.LocalDate;

import com.damnhandy.uri.template.UriTemplate;

import de.malkusch.ha.shared.infrastructure.http.HttpClient;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;

@Slf4j
public final class Ical4jHttpFactory {

    private final HttpClient http;
    private final UriTemplate uriTemplate;

    public Ical4jHttpFactory(HttpClient http, String uriTemplate) {
        this.http = http;
        this.uriTemplate = UriTemplate.fromTemplate(uriTemplate);
    }

    public Calendar download(LocalDate fetchDate) throws IOException, InterruptedException {
        var url = url(fetchDate);
        log.debug("Downloading {}", url);
        try (var response = http.get(url)) {
            return new CalendarBuilder().build(response.body);
        } catch (ParserException e) {
            throw new IOException("Can't parse response of " + url, e);
        }
    }

    private String url(LocalDate fetchDate) {
        return uriTemplate.set("year", fetchDate.getYear()).expand();
    }
}
