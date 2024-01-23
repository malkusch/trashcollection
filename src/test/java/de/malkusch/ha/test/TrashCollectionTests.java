package de.malkusch.ha.test;

import static de.malkusch.ha.automation.model.TrashCan.ORGANIC;
import static de.malkusch.ha.automation.model.TrashCan.PAPER;
import static de.malkusch.ha.automation.model.TrashCan.PLASTIC;
import static de.malkusch.ha.automation.model.TrashCan.RESIDUAL;

import java.io.IOException;
import java.time.LocalDate;

import de.malkusch.ha.automation.infrastructure.calendar.InMemoryCalendarCache;
import de.malkusch.ha.automation.infrastructure.calendar.TrashCollections;
import de.malkusch.ha.automation.model.TrashCan;
import de.malkusch.ha.automation.model.TrashCollection;

public final class TrashCollectionTests {

    public static TrashCollection trashCollection(String collection) {
        return TrashCollectionScenario.parse(collection).trashCollection();
    }

    private record TrashCollectionScenario(String date, String cans) {

        private TrashCollection trashCollection() {
            return new TrashCollection(LocalDate.parse(date), trashCans(cans));
        }

        private static final TrashCan[] PP = { PAPER, PLASTIC };
        private static final TrashCan[] RO = { RESIDUAL, ORGANIC };

        private TrashCan[] trashCans(String can) {
            return switch (can) {
            case "PP" -> PP;
            case "RO" -> RO;
            default -> throw new IllegalArgumentException(can);
            };
        }

        @Override
        public String toString() {
            return date + "/" + cans;
        }

        public static TrashCollectionScenario parse(String scenario) {
            String[] parsed = scenario.split("/");
            return new TrashCollectionScenario(parsed[0], parsed[1]);
        }
    }

    public static TrashCollections fromJson(LocalDate fetchDate) {
        var calendar = new TestCalendar(fetchDate.getYear());
        return fromJson(calendar);
    }

    private static TrashCollections fromJson(TestCalendar calendar) {
        try {
            return new InMemoryCalendarCache(calendar.json().path()).load();

        } catch (IOException e) {
            throw new IllegalStateException(calendar.toString(), e);
        }
    }
}
