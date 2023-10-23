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
    private static final String CALENDAR_2017 = "2017.ics";
    private static final String CALENDAR_2023 = "2023.ics";

    private final HttpClient http = mock(HttpClient.class);

    private static record Scenario(LocalDate now, TimeZone system, TrashCollection expected) {
    }

    private static Stream<Scenario> shouldFindTrashCollection() {
        return Stream.of( //
                scenarios("2016-12-25", "2017-01-04", trashCollection("2017-01-05", "RESIDUAL|ORGANIC")), //
                scenarios("2017-01-05", "2017-01-10", trashCollection("2017-01-11", "PAPER|PLASTIC")), //
                scenarios("2017-01-11", "2017-01-18", trashCollection("2017-01-19", "RESIDUAL|ORGANIC")), //
                scenarios("2017-01-19", "2017-02-01", trashCollection("2017-02-02", "RESIDUAL|ORGANIC")), //
                scenarios("2017-02-02", "2017-02-06", trashCollection("2017-02-07", "PAPER|PLASTIC")), //
                scenarios("2017-02-07", "2017-02-15", trashCollection("2017-02-16", "RESIDUAL|ORGANIC")), //
                scenarios("2017-02-16", "2017-03-01", trashCollection("2017-03-02", "RESIDUAL|ORGANIC")), //
                scenarios("2017-03-02", "2017-03-06", trashCollection("2017-03-07", "PAPER|PLASTIC")), //
                scenarios("2017-03-07", "2017-03-15", trashCollection("2017-03-16", "RESIDUAL|ORGANIC")), //
                scenarios("2017-03-16", "2017-03-29", trashCollection("2017-03-30", "RESIDUAL|ORGANIC")), //
                scenarios("2017-03-30", "2017-04-02", trashCollection("2017-04-03", "PAPER|PLASTIC")), //
                scenarios("2017-04-03", "2017-04-12", trashCollection("2017-04-13", "RESIDUAL|ORGANIC")), //
                scenarios("2017-04-13", "2017-04-26", trashCollection("2017-04-27", "RESIDUAL|ORGANIC")), //
                scenarios("2017-04-27", "2017-05-03", trashCollection("2017-05-04", "PAPER|PLASTIC")), //
                scenarios("2017-05-04", "2017-05-10", trashCollection("2017-05-11", "RESIDUAL|ORGANIC")), //
                scenarios("2017-05-11", "2017-05-25", trashCollection("2017-05-26", "RESIDUAL|ORGANIC")), //
                scenarios("2017-05-26", "2017-05-31", trashCollection("2017-06-01", "PAPER|PLASTIC")), //
                scenarios("2017-06-01", "2017-06-08", trashCollection("2017-06-09", "RESIDUAL|ORGANIC")), //
                scenarios("2017-06-09", "2017-06-21", trashCollection("2017-06-22", "RESIDUAL|ORGANIC")), //
                scenarios("2017-06-22", "2017-07-04", trashCollection("2017-07-05", "PAPER|PLASTIC")), //
                scenarios("2017-07-05", "2017-07-05", trashCollection("2017-07-06", "RESIDUAL|ORGANIC")), //
                scenarios("2017-07-06", "2017-07-19", trashCollection("2017-07-20", "RESIDUAL|ORGANIC")), //
                scenarios("2017-07-20", "2017-08-01", trashCollection("2017-08-02", "PAPER|PLASTIC")), //
                scenarios("2017-08-02", "2017-08-02", trashCollection("2017-08-03", "RESIDUAL|ORGANIC")), //
                scenarios("2017-08-03", "2017-08-16", trashCollection("2017-08-17", "RESIDUAL|ORGANIC")), //
                scenarios("2017-08-17", "2017-08-30", trashCollection("2017-08-31", "RESIDUAL|ORGANIC")), //
                scenarios("2017-08-31", "2017-09-04", trashCollection("2017-09-05", "PAPER|PLASTIC")), //
                scenarios("2017-09-05", "2017-09-13", trashCollection("2017-09-14", "RESIDUAL|ORGANIC")), //
                scenarios("2017-09-14", "2017-09-27", trashCollection("2017-09-28", "RESIDUAL|ORGANIC")), //
                scenarios("2017-09-28", "2017-09-28", trashCollection("2017-09-29", "PAPER|PLASTIC")), //
                scenarios("2017-09-29", "2017-10-11", trashCollection("2017-10-12", "RESIDUAL|ORGANIC")), //
                scenarios("2017-10-12", "2017-10-25", trashCollection("2017-10-26", "RESIDUAL|ORGANIC")), //
                scenarios("2017-10-26", "2017-10-26", trashCollection("2017-10-27", "PAPER|PLASTIC")), //
                scenarios("2017-10-27", "2017-11-08", trashCollection("2017-11-09", "RESIDUAL|ORGANIC")), //
                scenarios("2017-11-09", "2017-11-22", trashCollection("2017-11-23", "RESIDUAL|ORGANIC")), //
                scenarios("2017-11-23", "2017-11-28", trashCollection("2017-11-29", "PAPER|PLASTIC")), //
                scenarios("2017-11-29", "2017-12-06", trashCollection("2017-12-07", "RESIDUAL|ORGANIC")), //
                scenarios("2017-12-07", "2017-12-20", trashCollection("2017-12-21", "RESIDUAL|ORGANIC")), //
                scenarios("2017-12-21", "2017-12-27", trashCollection("2017-12-28", "PAPER|PLASTIC")) //
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
            var calendar = calendar(CALENDAR_2017);

            var nextCollection = calendar.findNextTrashCollectionAfter(scenario.now);

            assertEquals(scenario.expected, nextCollection);

        } finally {
            TimeZone.setDefault(null);
        }
    }

    @ValueSource(strings = { "2017-12-27", "2017-12-28", "2018-01-01" })
    @ParameterizedTest
    public void oldCalendarShouldFallbackToLastTrashCollection(String after) {
        try {
            TimeZone.setDefault(getTimeZone("Europe/Berlin"));
            var calendar = calendar(CALENDAR_2017);

            var nextCollection = calendar.findNextTrashCollectionAfter(LocalDate.parse(after));

            assertEquals(trashCollection("2017-12-28", "PAPER|PLASTIC"), nextCollection);
        } finally {
            TimeZone.setDefault(null);
        }
    }

    @Test
    public void updateYearShouldFindFirstCollection() throws Exception {
        try {
            TimeZone.setDefault(getTimeZone("Europe/Berlin"));
            var calendar = calendar(CALENDAR_2017);
            var last2017 = calendar.findNextTrashCollectionAfter(LocalDate.parse("2017-12-27"));

            mockHttpCalendar(CALENDAR_2023);
            calendar.update();
            var next = calendar.findNextTrashCollectionAfter(last2017.date());

            assertEquals(trashCollection("2023-01-02", "PAPER|PLASTIC"), next);

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
