package de.malkusch.ha.shared.infrastructure;

import de.malkusch.ha.automation.model.TrashCollection;
import de.malkusch.ha.test.TrashCollectionTests;
import de.malkusch.telgrambot.Callback;
import de.malkusch.telgrambot.Command;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrashCollectionFormatterTest {

    private final TrashCollectionFormatter formatter = new TrashCollectionFormatter(new JsonConfiguration().objectMapper());

    private static Stream<TrashCollection> trashCollections() {
        return Stream.of( //
                "2023-01-02/PP", "2023-01-02/RO", "2023-01-02/PPRO", //
                "2023-11-12/PP", "2023-11-12/RO", "2023-11-12/PPRO" //
        ).map(TrashCollectionTests::trashCollection);
    }

    @ParameterizedTest
    @MethodSource("trashCollections")
    public void testJson(TrashCollection trashCollection) {
        var json = formatter.json(trashCollection);
        var parsed = formatter.parseJson(json);

        assertEquals(trashCollection, parsed);
    }

    @ParameterizedTest
    @MethodSource("trashCollections")
    public void sendShouldBuildValidCallback(TrashCollection trashCollection) {
        var done = new Command("done");
        var payload = formatter.json(trashCollection);

        assertDoesNotThrow(() -> new Callback(done, payload));
    }
}
