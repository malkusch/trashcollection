package de.malkusch.ha.automation.infrastructure.ical;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;

import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.validate.ValidationException;

@Slf4j
public final class CalendarFile {

    private final Path path;

    public CalendarFile(@Value("${calendar.file}") String path) throws IOException {
        this(Paths.get(path));
    }

    public CalendarFile(Path path) throws IOException {
        this.path = path;
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
    }

    public Calendar load() throws IOException, InterruptedException, ParserException {
        try (var stream = new FileInputStream(path.toFile())) {
            var calendar = new CalendarBuilder().build(stream);
            log.debug("Calender loaded from file {}", path);
            return calendar;
        }
    }

    public Instant lastUpdate() throws IOException {
        return Files.getLastModifiedTime(path).toInstant();
    }

    public void store(Calendar calendar) throws ValidationException, IOException {
        var outputter = new CalendarOutputter(false);
        try (var stream = new FileOutputStream(path.toFile())) {
            outputter.output(calendar, stream);
        }
        log.debug("Calender stored in file {}", path);
    }
}
