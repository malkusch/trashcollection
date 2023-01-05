package de.malkusch.ha.automation.infrastructure.ical;

import static net.fortuna.ical4j.model.Property.PRODID;
import static net.fortuna.ical4j.model.Property.VERSION;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import org.springframework.scheduling.annotation.Scheduled;

import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;

@Slf4j
public class CalendarIO implements AutoCloseable {

    private final CalendarHttp http;
    private final CalendarFile file;
    private volatile Calendar calendar;
    private volatile Instant lastUpdate;
    private final Duration cacheExpiration;

    public CalendarIO(CalendarHttp http, CalendarFile file, Duration cacheExpiration)
            throws IOException, InterruptedException, ParserException {

        this.http = http;
        this.file = file;
        this.cacheExpiration = cacheExpiration;

        try {
            calendar = file.load();
            lastUpdate = file.lastUpdate();
            update();

        } catch (Exception e) {
            log.warn("Failed loading calendar from file");
            calendar = http.download();
            lastUpdate = Instant.now();
        }

        log.info("Loaded calender {}", calendarName(calendar));
    }

    public Calendar fetch() {
        update();
        return calendar;
    }

    private void update() {
        var expiration = lastUpdate.plus(cacheExpiration);
        if (Instant.now().isBefore(expiration)) {
            log.debug("Using cached calendar from {}", lastUpdate);
            return;
        }

        try {
            var downloaded = http.download();
            if (!calendarName(calendar).equals(calendarName(downloaded))) {
                log.info("Calender {} updated to {}", calendarName(calendar), calendarName(downloaded));
            }
            calendar = downloaded;
            lastUpdate = Instant.now();

        } catch (IOException | InterruptedException | ParserException e) {
            log.warn("Failed to update calendar", e);
        }
    }

    private String calendarName(Calendar calendar) {
        return String.format("V %s, %s, %s", //
                calendar.getProperty(VERSION).map(Property::getValue).orElse(""), //
                calendar.getProperty(PRODID).map(Property::getValue).orElse(""), //
                calendar.getProperty("X-WR-CALDESC").map(Property::getValue).orElse("") //
        );
    }

    @Scheduled(cron = "59 59 */5 * * *")
    void updateFile() throws Exception {
        if (lastUpdate.isAfter(file.lastUpdate())) {
            file.store(calendar);
        }
    }

    @Override
    public void close() throws Exception {
        updateFile();
    }
}
