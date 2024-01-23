package de.malkusch.ha.automation.infrastructure.calendar;

import static de.malkusch.ha.test.TrashCollectionTests.trashCollection;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.LocalDate;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.stubbing.Answer;

import de.malkusch.ha.test.TrashCollectionTests;

public class RolloverCalendarProviderTest {

    private static final String FIRST_2023 = "2023-01-02/PP";
    private static final String LAST_2023 = "2023-12-21/RO";

    @ParameterizedTest
    @CsvSource({ //
            "2023-01-01, " + FIRST_2023 + ", " + LAST_2023, //
            "2023-12-20," + FIRST_2023 + ", " + LAST_2023, //

            "2024-01-01, 2024-01-05/RO, 2024-12-19/RO", //
            "2024-12-18, 2024-01-05/RO, 2024-12-19/RO", //
    })
    public void shouldNotRolloverWhenFetchDateIsBeforeLastCollection(String fetchDateString,
            String expectedFirstCollection, String expectedLastCollection) throws Exception {

        var fetchDate = LocalDate.parse(fetchDateString);
        var provider = provider();

        var collections = provider.fetch(fetchDate);

        verify(upstream, only()).fetch(fetchDate);
        assertEquals(trashCollection(expectedFirstCollection), collections.first());
        assertEquals(trashCollection(expectedLastCollection), collections.last());
    }

    @ParameterizedTest
    @CsvSource({ //
            "2023-12-21, 2024-01-01, " + FIRST_2023 + ", 2024-12-19/RO", //
            "2023-12-31, 2024-01-01, " + FIRST_2023 + ", 2024-12-19/RO", //
    })
    public void shouldRolloverWhenEndOfYear(String fetchDateString, String expectedRolloverFetchDate,
            String expectedFirstCollection, String expectedLastCollection) throws Exception {

        var fetchDate = LocalDate.parse(fetchDateString);
        var provider = provider();

        var collections = provider.fetch(fetchDate);

        verify(upstream).fetch(fetchDate);
        verify(upstream).fetch(LocalDate.parse(expectedRolloverFetchDate));
        assertEquals(trashCollection(expectedFirstCollection), collections.first());
        assertEquals(trashCollection(expectedLastCollection), collections.last());
    }

    @Test
    public void shouldFallBackOnRolloverError() throws Exception {
        var fetchDate = LocalDate.parse("2023-12-31");
        var provider = provider(answer(), new IOException());

        var collections = provider.fetch(fetchDate);

        verify(upstream).fetch(fetchDate);
        verify(upstream).fetch(LocalDate.parse("2024-01-01"));
        assertEquals(trashCollection(FIRST_2023), collections.first());
        assertEquals(trashCollection(LAST_2023), collections.last());
    }

    private final CalendarProvider upstream = mock(CalendarProvider.class);

    private CalendarProvider provider() throws Exception {
        return provider(answer());
    }

    private CalendarProvider provider(Object... stubbings) throws Exception {
        var upstreamStubbing = when(upstream.fetch(any()));
        for (Object stubbing : stubbings) {
            if (stubbing instanceof Answer a) {
                upstreamStubbing = upstreamStubbing.thenAnswer(a);
            } else if (stubbing instanceof Exception e) {
                upstreamStubbing = upstreamStubbing.thenThrow(e);
            } else if (stubbing instanceof TrashCollections t) {
                upstreamStubbing = upstreamStubbing.thenReturn(t);
            } else {
                throw new IllegalArgumentException("Unexpected value: " + stubbing);
            }
        }
        var provider = new RolloverCalendarProvider(upstream);
        return provider;
    }

    private Answer<TrashCollections> answer() {
        return answer(TrashCollectionTests::fromJson);
    }

    private Answer<TrashCollections> answer(Function<LocalDate, TrashCollections> answer) {
        return it -> answer.apply(it.getArgument(0, LocalDate.class));
    }
}
