package de.malkusch.ha.automation.infrastructure.ical;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import net.fortuna.ical4j.data.ParserException;

public class CalendarFileTest {

    @Test
    public void loadAndStoreShouldBeEqual()
            throws URISyntaxException, IOException, InterruptedException, ParserException {

        var input = Paths.get(getClass().getResource("schedule.ics").toURI());
        var reader = new CalendarFile(input);
        var source = reader.load();
        var output = Paths.get("/tmp/CalendarFileTest-loadAndStoreShouldBeEqual");
        try {
            var writer = new CalendarFile(output);

            writer.store(source);

            var copy = writer.load();
            assertEquals(source, copy);

        } finally {
            Files.deleteIfExists(output);
        }
    }

}
