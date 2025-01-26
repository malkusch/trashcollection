package de.malkusch.ha.test;

import static java.util.Arrays.stream;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.Year;
import java.util.function.Function;
import java.util.stream.Stream;

public record TestCalendar(int year) {

    public final static TestCalendar CALENDAR_2023 = new TestCalendar(2023);
    public final static TestCalendar CALENDAR_2024 = new TestCalendar(2024);
    public final static TestCalendar CALENDAR_2025 = new TestCalendar(2025);

    public record TestCalendars(TestCalendar... calendars) {

        public final static TestCalendars ALL = new TestCalendars(CALENDAR_2023, CALENDAR_2024, CALENDAR_2025);

        public <T> Stream<T> testCases(ScenarioMapper<T> mapper) {
            return stream(calendars) //
                    .flatMap(mapper::scenarios); //
        }

        @SafeVarargs
        public final <T> Stream<T> testCases(Function<TestCalendar, T>... mappers) {
            return testCases(ScenarioMapper.scenarios(mappers));
        }
    }

    @FunctionalInterface
    public interface ScenarioMapper<T> {

        public Stream<T> scenarios(TestCalendar calendar);

        public static <T> ScenarioMapper<T> scenarios(Function<TestCalendar, T> mapper) {
            return it -> Stream.of(mapper.apply(it));
        }

        public static <T> ScenarioMapper<T> scenarios(Function<TestCalendar, T>... mappers) {
            return it -> stream(mappers).map(mapper -> mapper.apply(it));
        }
    }

    public <T> Stream<T> scenarios(ScenarioMapper<T> mapper) {
        return mapper.scenarios(this);
    }

    public LocalDate beginOfYear() {
        return Year.of(year).atDay(1);
    }

    private static MonthDay END_OF_YEAR = MonthDay.parse("--12-31");

    public LocalDate endOfYear() {
        return Year.of(year).atMonthDay(END_OF_YEAR);
    }

    public File ics() {
        return forExtension(".ics");
    }

    public File json() {
        return forExtension(".json");
    }

    private File forExtension(String extension) {
        return new File("/" + year + extension);
    }

    public record File(String fileName) {
        public InputStream stream() {
            return getClass().getResourceAsStream(fileName);
        }

        private URI uri() {
            try {
                return getClass().getResource(fileName).toURI();

            } catch (URISyntaxException e) {
                throw new IllegalStateException(fileName, e);
            }
        }

        public Path path() {
            return Paths.get(uri());
        }
    }
}
