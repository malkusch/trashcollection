package de.malkusch.ha.automation.infrastructure.calendar.ical4j;

import static de.malkusch.ha.test.HttpTests.http;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class Ical4jHttpFactoryTest {

    @ParameterizedTest
    @CsvSource({ //
            "2022-12-31, http://example.org?year={year}, http://example.org?year=2022", //
            "2023-01-01, http://example.org?year={year}, http://example.org?year=2023", //
            "2023-12-31, http://example.org?year={year}, http://example.org?year=2023", //
            "2024-01-01, http://example.org?year={year}, http://example.org?year=2024", //

            "2023-01-01, http://example.org?foo&year={year}, http://example.org?foo&year=2023", //

            "2023-01-01, http://example.org/{year}, http://example.org/2023", //

            "2023-01-01, http://example.org, http://example.org", //

            "2023-01-01, http://example.org?year=2024, http://example.org?year=2024", //
    })
    public void shouldExpandUriTemplate(String fetchDateString, String urltemplate, String expectedUrl)
            throws Exception {

        var fetchDate = LocalDate.parse(fetchDateString);
        var http = http();
        var factory = new Ical4jHttpFactory(http, urltemplate);

        factory.download(fetchDate);

        verify(http).get(expectedUrl);
    }
}
