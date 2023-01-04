package de.malkusch.ha.automation.infrastructure;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import de.malkusch.ha.automation.infrastructure.ical.CalendarFile;
import de.malkusch.ha.automation.infrastructure.ical.CalendarHttp;
import de.malkusch.ha.automation.infrastructure.ical.CalendarIO;
import de.malkusch.ha.automation.model.TrashCan;
import de.malkusch.ha.automation.model.TrashCollection;
import de.malkusch.ha.automation.model.TrashCollectionCalendar;
import de.malkusch.ha.shared.infrastructure.http.HttpClient;
import de.malkusch.ha.shared.infrastructure.http.HttpResponse;

public class ICallTrashCollectionCalendarTest {

    private final HttpClient http = mock(HttpClient.class);
    private TrashCollectionCalendar calendar;

    private final static Path CALENDAR_FILE = Paths.get("/tmp/trash-calendar-test");

    @BeforeEach
    public void setupCalendar() throws Exception {
        var url = "ANY";
        when(http.get(url))
                .then(it -> new HttpResponse(200, url, false, getClass().getResourceAsStream("schedule.ics")));
        var calendarIO = new CalendarIO(new CalendarHttp(http, url), new CalendarFile(CALENDAR_FILE));
        calendar = new ICallTrashCollectionCalendar(new DefaultMapper(), calendarIO);
    }

    @AfterEach
    public void deleteCalenderFile() throws IOException {
        Files.delete(CALENDAR_FILE);
    }

    @ParameterizedTest
    @CsvSource({ //
            "2017-05-24, 2017-05-26, RESIDUAL|ORGANIC", //
            "2017-05-25, 2017-05-26, RESIDUAL|ORGANIC", //

            "2017-05-26, 2017-06-01, PAPER|PLASTIC", //
            "2017-05-31, 2017-06-01, PAPER|PLASTIC", //

            "2017-06-01, 2017-06-09, RESIDUAL|ORGANIC", //
            "2017-06-08, 2017-06-09, RESIDUAL|ORGANIC", //

            "2017-06-09, 2017-06-22, RESIDUAL|ORGANIC", //
            "2017-06-21, 2017-06-22, RESIDUAL|ORGANIC", //

            "2017-06-22, 2017-07-05, PAPER|PLASTIC", //
            "2017-07-04, 2017-07-05, PAPER|PLASTIC", //

            "2017-07-05, 2017-07-06, RESIDUAL|ORGANIC", //

            "2017-07-06, 2017-07-20, RESIDUAL|ORGANIC", //
            "2017-07-19, 2017-07-20, RESIDUAL|ORGANIC", //
    })
    public void shouldFindTrashCollection(String afterString, String dateString, String expectedCans) {
        var after = LocalDate.parse(afterString);

        var nextCollection = calendar.findNextTrashCollectionAfter(after);

        var expected = trashCollection(dateString, expectedCans);
        assertEquals(expected, nextCollection);
    }

    private static TrashCollection trashCollection(String dateString, String cansString) {
        var date = LocalDate.parse(dateString);
        var cans = stream(cansString.split("\\|")).map(TrashCan::valueOf).collect(toSet());
        return new TrashCollection(date, cans);
    }
}
