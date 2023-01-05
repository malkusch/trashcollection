package de.malkusch.ha.automation.infrastructure.ical;

import static net.fortuna.ical4j.model.Property.PRODID;
import static net.fortuna.ical4j.model.Property.VERSION;

import java.io.FileNotFoundException;
import java.io.IOException;

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

    public CalendarIO(CalendarHttp http, CalendarFile file) throws IOException, InterruptedException, ParserException {

        this.http = http;
        this.file = file;

        try {
            calendar = file.load();

        } catch (FileNotFoundException e) {
            log.info("Local calender file doesn't exist, downloading calendar");
            calendar = update();

        } catch (Exception e) {
            log.warn("Failed loading calendar from file", e);
            calendar = update();
        }

        log.info("Loaded calender {}", calendarName(calendar));
    }

    public Calendar fetch() {
        return calendar;
    }

    @Scheduled(cron = "${calendar.update}")
    public Calendar update() throws IOException, InterruptedException, ParserException {
        log.info("Updating calendar");

        var downloaded = http.download();
        if (!calendarName(calendar).equals(calendarName(downloaded))) {
            log.info("Calender {} updated to {}", calendarName(calendar), calendarName(downloaded));
        }
        calendar = downloaded;

        file.store(calendar);

        return calendar;
    }

    private String calendarName(Calendar calendar) {
        if (calendar == null) {
            return "";
        }
        return String.format("V %s, %s, %s", //
                calendar.getProperty(VERSION).map(Property::getValue).orElse(""), //
                calendar.getProperty(PRODID).map(Property::getValue).orElse(""), //
                calendar.getProperty("X-WR-CALDESC").map(Property::getValue).orElse("") //
        );
    }

    @Override
    public void close() throws Exception {
        file.store(calendar);
    }
}
