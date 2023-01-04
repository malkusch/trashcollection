package de.malkusch.ha.automation.infrastructure.ical;

import java.io.IOException;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;

@Service
@Slf4j
public class CalendarIO implements AutoCloseable {

    private final CalendarHttp http;
    private final CalendarFile file;
    private volatile Calendar calendar;

    public CalendarIO(CalendarHttp http, CalendarFile file) throws IOException, InterruptedException, ParserException {
        this.http = http;
        this.file = file;

        try {
            calendar = http.download();
        } catch (IOException | InterruptedException | ParserException e) {
            log.warn("Failed downloading calendar, falling back to local file", e);
            calendar = file.load();
        }
    }

    public Calendar fetch() {
        try {
            calendar = http.download();
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
