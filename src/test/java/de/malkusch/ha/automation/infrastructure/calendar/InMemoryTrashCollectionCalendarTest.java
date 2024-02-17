package de.malkusch.ha.automation.infrastructure.calendar;

import static de.malkusch.ha.test.TrashCollectionTests.trashCollection;
import static java.nio.file.Files.deleteIfExists;
import static java.util.TimeZone.getTimeZone;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.TimeZone;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import de.malkusch.ha.automation.model.TrashCollection;
import de.malkusch.ha.shared.infrastructure.http.HttpClient;
import de.malkusch.ha.shared.infrastructure.http.HttpResponse;
import de.malkusch.ha.test.MockedClock;

public class InMemoryTrashCollectionCalendarTest {

    private static final String URL_TEMPLATE = "http://example.org?year={year}";
    private static final Path CALENDAR_FILE = Paths.get("/tmp/trash-calendar-test");

    private final HttpClient http = mock(HttpClient.class);
    private final MockedClock mockedClock = new MockedClock();

    private static record Scenario(LocalDate now, TimeZone system, TrashCollection expected) {
    }

    private static Stream<Scenario> shouldFindTrashCollection() {
        return Stream.of( //
                scenarios("2023-01-01", "2023-01-01", trashCollection(("2023-01-02/PP"))), //
                scenarios("2023-01-02", "2023-01-04", trashCollection("2023-01-05/RO")), //
                scenarios("2023-01-05", "2023-01-18", trashCollection("2023-01-19/RO")), //
                scenarios("2023-01-19", "2023-01-29", trashCollection("2023-01-30/PP")), //

                scenarios("2023-01-30", "2023-02-01", trashCollection("2023-02-02/RO")), //
                scenarios("2023-02-02", "2023-02-15", trashCollection("2023-02-16/RO")), //
                scenarios("2023-02-16", "2023-02-26", trashCollection("2023-02-27/PP")), //

                scenarios("2023-02-27", "2023-03-01", trashCollection("2023-03-02/RO")), //
                scenarios("2023-03-02", "2023-03-15", trashCollection("2023-03-16/RO")), //
                scenarios("2023-03-16", "2023-03-26", trashCollection("2023-03-27/PP")), //
                scenarios("2023-03-27", "2023-03-29", trashCollection("2023-03-30/RO")), //

                scenarios("2023-03-30", "2023-04-13", trashCollection("2023-04-14/RO")), //
                scenarios("2023-04-14", "2023-04-26", trashCollection("2023-04-27/RO")), //
                scenarios("2023-04-27", "2023-04-27", trashCollection("2023-04-28/PP")), //

                scenarios("2023-04-28", "2023-05-10", trashCollection("2023-05-11/RO")), //
                scenarios("2023-05-11", "2023-05-24", trashCollection("2023-05-25/RO")), //
                scenarios("2023-05-25", "2023-05-30", trashCollection("2023-05-31/PP")), //

                scenarios("2023-05-31", "2023-06-08", trashCollection("2023-06-09/RO")), //
                scenarios("2023-06-09", "2023-06-21", trashCollection("2023-06-22/RO")), //

                scenarios("2023-06-22", "2023-07-02", trashCollection("2023-07-03/PP")), //
                scenarios("2023-07-03", "2023-07-05", trashCollection("2023-07-06/RO")), //
                scenarios("2023-07-06", "2023-07-19", trashCollection("2023-07-20/RO")), //
                scenarios("2023-07-20", "2023-07-27", trashCollection("2023-07-28/PP")), //

                scenarios("2023-07-28", "2023-08-02", trashCollection("2023-08-03/RO")), //
                scenarios("2023-08-03", "2023-08-16", trashCollection("2023-08-17/RO")), //
                scenarios("2023-08-17", "2023-08-24", trashCollection("2023-08-25/PP")), //
                scenarios("2023-08-25", "2023-08-30", trashCollection("2023-08-31/RO")), //

                scenarios("2023-08-31", "2023-09-13", trashCollection("2023-09-14/RO")), //
                scenarios("2023-09-14", "2023-09-21", trashCollection("2023-09-22/PP")), //
                scenarios("2023-09-22", "2023-09-27", trashCollection("2023-09-28/RO")), //

                scenarios("2023-09-28", "2023-10-11", trashCollection("2023-10-12/RO")), //
                scenarios("2023-10-12", "2023-10-24", trashCollection("2023-10-25/PP")), //
                scenarios("2023-10-25", "2023-10-25", trashCollection("2023-10-26/RO")), //

                scenarios("2023-10-26", "2023-11-08", trashCollection("2023-11-09/RO")), //
                scenarios("2023-11-09", "2023-11-21", trashCollection("2023-11-22/PP")), //
                scenarios("2023-11-22", "2023-11-22", trashCollection("2023-11-23/RO")), //

                scenarios("2023-11-23", "2023-12-06", trashCollection("2023-12-07/RO")), //
                scenarios("2023-12-07", "2023-12-19", trashCollection("2023-12-20/PP")), //
                scenarios("2023-12-20", "2023-12-20", trashCollection("2023-12-21/RO")), //

                scenarios("2023-12-21", "2024-01-04", trashCollection("2024-01-05/RO")), //

                scenarios("2024-12-10", "2024-12-18", trashCollection("2024-12-19/RO")) //
        ).flatMap(Function.identity());

    }

    private static Stream<Scenario> scenarios(String startDate, String endDate, TrashCollection expected) {
        var start = LocalDate.parse(startDate);
        var end = LocalDate.parse(endDate);
        Stream<Scenario> scenarios = Stream.empty();
        for (LocalDate now = start; !now.isAfter(end); now = now.plusDays(1)) {
            scenarios = Stream.concat(scenarios, //
                    Stream.of( //
                            new Scenario(now, null, expected), //
                            new Scenario(now, getTimeZone("UTC"), expected), //
                            new Scenario(now, getTimeZone("Etc/UTC"), expected), //
                            new Scenario(now, getTimeZone("GMT"), expected), //
                            new Scenario(now, getTimeZone("Etc/GMT"), expected), //
                            new Scenario(now, getTimeZone("Europe/Berlin"), expected) //
                    ));
        }
        return scenarios;
    }

    @ParameterizedTest
    @MethodSource
    public void shouldFindTrashCollection(Scenario scenario) {
        try {
            TimeZone.setDefault(scenario.system);
            var calendar = calendar(scenario.now);

            var nextCollection = calendar.findNextTrashCollectionAfter(scenario.now);

            assertEquals(scenario.expected, nextCollection);

        } finally {
            TimeZone.setDefault(null);
        }
    }

    @ValueSource(strings = { "2023-12-20", "2023-12-21", "2023-12-22", "2024-01-01" })
    @ParameterizedTest
    public void oldCalendarShouldFallbackToLastTrashCollection(String after) {
        try {
            TimeZone.setDefault(getTimeZone("Europe/Berlin"));
            var calendar = calendar("2023-01-01");

            var nextCollection = calendar.findNextTrashCollectionAfter(LocalDate.parse(after));

            assertEquals(trashCollection("2023-12-21/RO"), nextCollection);
        } finally {
            TimeZone.setDefault(null);
        }
    }

    @ValueSource(strings = { "2023-12-21", "2023-12-31", "2024-01-01", })
    @ParameterizedTest
    public void updateYearShouldFindFirstCollection(String updateDate) throws Exception {
        try {
            TimeZone.setDefault(getTimeZone("Europe/Berlin"));
            var calendar = calendar("2023-01-01");
            var last2023 = calendar.findNextTrashCollectionAfter(LocalDate.parse("2023-12-20"));

            mockedClock.mockDate(updateDate);
            calendar.update();
            var next = calendar.findNextTrashCollectionAfter(last2023.date());

            assertEquals(trashCollection("2024-01-05/RO"), next);

        } finally {
            TimeZone.setDefault(null);
        }
    }

    @AfterEach
    @BeforeEach
    public void deleteCalenderFile() throws IOException {
        deleteIfExists(CALENDAR_FILE);
    }

    private InMemoryTrashCollectionCalendar calendar(String now) {
        return calendar(LocalDate.parse(now));
    }

    private InMemoryTrashCollectionCalendar calendar(LocalDate now) {
        try {
            mockHttpCalendar();
            mockedClock.mockDate(now);

            var properties = new CalendarConfiguration.CalendarProperties(CALENDAR_FILE.toString(), URL_TEMPLATE);
            return CalendarConfiguration.calendar(mockedClock.clock, http, properties);

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private void mockHttpCalendar() {
        try {
            when(http.get(anyString())).thenAnswer(it -> {
                String url = it.getArgument(0);
                var uri = URI.create(url);
                var query = uri.getQuery().split("&");
                var year = query[0].split("=")[1];

                var file = "/" + year + ".ics";

                return new HttpResponse(200, url, false, getClass().getResourceAsStream(file));
            });
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
