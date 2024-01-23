package de.malkusch.ha.test;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

import java.net.URI;
import java.time.LocalDate;
import java.util.Map;
import java.util.function.Function;

@FunctionalInterface
public interface UriMapper {

    public TestCalendar calendar(URI uri);

    public static UriMapper fromQuery(Function<UriMapper.Query, TestCalendar> mapper) {
        return uri -> mapper.apply(query(uri));
    }

    public static UriMapper forCalendar(TestCalendar calendar) {
        return uri -> calendar;
    }

    public static UriMapper forDate(LocalDate date) {
        return forYear(date.getYear());
    }

    public static UriMapper forYear(int year) {
        return forCalendar(new TestCalendar(year));
    }

    public static record Query(Map<String, String> query) {
    }

    private static UriMapper.Query query(URI uri) {
        var query = stream(uri.getQuery().split("&")) //
                .map(it -> it.split("=")) //
                .collect(toMap(it -> it[0], it -> it[1]));
        return new Query(query);
    }
}