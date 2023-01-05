package de.malkusch.ha.automation.infrastructure.ical;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;

@Slf4j
public class CalendarIO implements AutoCloseable {

    private final CalendarHttp http;
    private final CalendarFile file;
    private volatile Calendar calendar;
    private volatile Instant lastUpdate = Instant.now();
    private final Duration cacheExpiration;

    public CalendarIO(CalendarHttp http, CalendarFile file, Duration cacheExpiration)
            throws IOException, InterruptedException, ParserException {

        this.http = http;
        this.file = file;
        this.cacheExpiration = cacheExpiration;

        try {
            calendar = http.download();

        } catch (IOException | InterruptedException | ParserException e) {
            log.warn("Failed downloading calendar, falling back to local file", e);
            calendar = file.load();
            lastUpdate = file.lastUpdate();
            log.warn("Calendar recovered from {}", lastUpdate);
        }
    }

    public Calendar fetch() {
        var expiration = lastUpdate.plus(cacheExpiration);
        if (Instant.now().isBefore(expiration)) {
            log.debug("Using cached calendar from {}", lastUpdate);
            return calendar;
        }

        try {
            calendar = http.download();
            lastUpdate = Instant.now();

        } catch (IOException | InterruptedException | ParserException e) {
            log.warn("Failed to download calendar", e);
        }
        return calendar;
    }

    @Override
    public void close() throws Exception {
        file.store(calendar);
    }
}
