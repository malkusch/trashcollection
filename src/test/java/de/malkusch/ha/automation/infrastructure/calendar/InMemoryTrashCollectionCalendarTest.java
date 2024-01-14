package de.malkusch.ha.automation.infrastructure.calendar;

import static java.nio.file.Files.deleteIfExists;
import static java.util.Arrays.stream;
import static java.util.TimeZone.getTimeZone;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.TimeZone;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import de.malkusch.ha.automation.infrastructure.calendar.ical4j.DefaultMapper;
import de.malkusch.ha.automation.infrastructure.calendar.ical4j.Ical4jHttpFactory;
import de.malkusch.ha.automation.infrastructure.calendar.ical4j.Ical4jInMemoryCalendarProvider;
import de.malkusch.ha.automation.model.TrashCan;
import de.malkusch.ha.automation.model.TrashCollection;
import de.malkusch.ha.shared.infrastructure.http.HttpClient;
import de.malkusch.ha.shared.infrastructure.http.HttpResponse;

public class InMemoryTrashCollectionCalendarTest {

    private static final String URL = "ANY";
    private static final Path CALENDAR_FILE = Paths.get("/tmp/trash-calendar-test");
    private static final String CALENDAR_2023 = "2023.ics";
    private static final String CALENDAR_2024 = "2024.ics";

    private final HttpClient http = mock(HttpClient.class);

    private static record Scenario(LocalDate now, TimeZone system, TrashCollection expected) {
    }

    private static Stream<Scenario> shouldFindTrashCollection() {
        return Stream.of( //
                scenarios("2022-12-25", "2023-01-01", trashCollection("2023-01-02", "PAPER|PLASTIC")), //
                scenarios("2023-01-02", "2023-01-04", trashCollection("2023-01-05", "RESIDUAL|ORGANIC")), //
                scenarios("2023-01-05", "2023-01-18", trashCollection("2023-01-19", "RESIDUAL|ORGANIC")), //
                scenarios("2023-01-19", "2023-01-29", trashCollection("2023-01-30", "PAPER|PLASTIC")), //

                scenarios("2023-01-30", "2023-02-01", trashCollection("2023-02-02", "RESIDUAL|ORGANIC")), //
                scenarios("2023-02-02", "2023-02-15", trashCollection("2023-02-16", "RESIDUAL|ORGANIC")), //
                scenarios("2023-02-16", "2023-02-26", trashCollection("2023-02-27", "PAPER|PLASTIC")), //

                scenarios("2023-02-27", "2023-03-01", trashCollection("2023-03-02", "RESIDUAL|ORGANIC")), //
                scenarios("2023-03-02", "2023-03-15", trashCollection("2023-03-16", "RESIDUAL|ORGANIC")), //
                scenarios("2023-03-16", "2023-03-26", trashCollection("2023-03-27", "PAPER|PLASTIC")), //
                scenarios("2023-03-27", "2023-03-29", trashCollection("2023-03-30", "RESIDUAL|ORGANIC")), //

                scenarios("2023-03-30", "2023-04-13", trashCollection("2023-04-14", "RESIDUAL|ORGANIC")), //
                scenarios("2023-04-14", "2023-04-26", trashCollection("2023-04-27", "RESIDUAL|ORGANIC")), //
                scenarios("2023-04-27", "2023-04-27", trashCollection("2023-04-28", "PAPER|PLASTIC")), //

                scenarios("2023-04-28", "2023-05-10", trashCollection("2023-05-11", "RESIDUAL|ORGANIC")), //
                scenarios("2023-05-11", "2023-05-24", trashCollection("2023-05-25", "RESIDUAL|ORGANIC")), //
                scenarios("2023-05-25", "2023-05-30", trashCollection("2023-05-31", "PAPER|PLASTIC")), //

                scenarios("2023-05-31", "2023-06-08", trashCollection("2023-06-09", "RESIDUAL|ORGANIC")), //
                scenarios("2023-06-09", "2023-06-21", trashCollection("2023-06-22", "RESIDUAL|ORGANIC")), //

                scenarios("2023-06-22", "2023-07-02", trashCollection("2023-07-03", "PAPER|PLASTIC")), //
                scenarios("2023-07-03", "2023-07-05", trashCollection("2023-07-06", "RESIDUAL|ORGANIC")), //
                scenarios("2023-07-06", "2023-07-19", trashCollection("2023-07-20", "RESIDUAL|ORGANIC")), //
                scenarios("2023-07-20", "2023-07-27", trashCollection("2023-07-28", "PAPER|PLASTIC")), //

                scenarios("2023-07-28", "2023-08-02", trashCollection("2023-08-03", "RESIDUAL|ORGANIC")), //
                scenarios("2023-08-03", "2023-08-16", trashCollection("2023-08-17", "RESIDUAL|ORGANIC")), //
                scenarios("2023-08-17", "2023-08-24", trashCollection("2023-08-25", "PAPER|PLASTIC")), //
                scenarios("2023-08-25", "2023-08-30", trashCollection("2023-08-31", "RESIDUAL|ORGANIC")), //

                scenarios("2023-08-31", "2023-09-13", trashCollection("2023-09-14", "RESIDUAL|ORGANIC")), //
                scenarios("2023-09-14", "2023-09-21", trashCollection("2023-09-22", "PAPER|PLASTIC")), //
                scenarios("2023-09-22", "2023-09-27", trashCollection("2023-09-28", "RESIDUAL|ORGANIC")), //

                scenarios("2023-09-28", "2023-10-11", trashCollection("2023-10-12", "RESIDUAL|ORGANIC")), //
                scenarios("2023-10-12", "2023-10-24", trashCollection("2023-10-25", "PAPER|PLASTIC")), //
                scenarios("2023-10-25", "2023-10-25", trashCollection("2023-10-26", "RESIDUAL|ORGANIC")), //

                scenarios("2023-10-26", "2023-11-08", trashCollection("2023-11-09", "RESIDUAL|ORGANIC")), //
                scenarios("2023-11-09", "2023-11-21", trashCollection("2023-11-22", "PAPER|PLASTIC")), //
                scenarios("2023-11-22", "2023-11-22", trashCollection("2023-11-23", "RESIDUAL|ORGANIC")), //

                scenarios("2023-11-23", "2023-12-06", trashCollection("2023-12-07", "RESIDUAL|ORGANIC")), //
                scenarios("2023-12-07", "2023-12-19", trashCollection("2023-12-20", "PAPER|PLASTIC")), //
                scenarios("2023-12-20", "2023-12-20", trashCollection("2023-12-21", "RESIDUAL|ORGANIC")), //

                scenarios("2023-12-21", "2024-01-02", trashCollection("2023-12-21", "RESIDUAL|ORGANIC")) //
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
            var calendar = calendar(CALENDAR_2023);

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
            var calendar = calendar(CALENDAR_2023);

            var nextCollection = calendar.findNextTrashCollectionAfter(LocalDate.parse(after));

            assertEquals(trashCollection("2023-12-21", "RESIDUAL|ORGANIC"), nextCollection);
        } finally {
            TimeZone.setDefault(null);
        }
    }

    @Test
    public void updateYearShouldFindFirstCollection() throws Exception {
        try {
            TimeZone.setDefault(getTimeZone("Europe/Berlin"));
            var calendar = calendar(CALENDAR_2023);
            var last2023 = calendar.findNextTrashCollectionAfter(LocalDate.parse("2023-12-20"));

            mockHttpCalendar(CALENDAR_2024);
            calendar.update();
            var next = calendar.findNextTrashCollectionAfter(last2023.date());

            assertEquals(trashCollection("2024-01-05", "RESIDUAL|ORGANIC"), next);

        } finally {
            TimeZone.setDefault(null);
        }
    }

    private static TrashCollection trashCollection(String dateString, String cansString) {
        var date = LocalDate.parse(dateString);
        var cans = stream(cansString.split("\\|")).map(TrashCan::valueOf).collect(toSet());
        return new TrashCollection(date, cans);
    }

    @AfterEach
    @BeforeEach
    public void deleteCalenderFile() throws IOException {
        deleteIfExists(CALENDAR_FILE);
    }

    private InMemoryTrashCollectionCalendar calendar(String file) {
        try {
            mockHttpCalendar(file);
            var provider = new Ical4jInMemoryCalendarProvider(new DefaultMapper(), new Ical4jHttpFactory(http, URL));
            var cache = new InMemoryCalendarCache(CALENDAR_FILE);
            return new InMemoryTrashCollectionCalendar(provider, cache);

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private void mockHttpCalendar(String file) {
        try {
            when(http.get(URL)).then(it -> new HttpResponse(200, URL, false, getClass().getResourceAsStream(file)));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
