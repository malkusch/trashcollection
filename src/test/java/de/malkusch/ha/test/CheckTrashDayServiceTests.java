package de.malkusch.ha.test;

import static de.malkusch.ha.test.TrashCollectionTests.trashCollection;

import de.malkusch.ha.automation.model.CheckTrashDayService.TomorrowsTrashDayNoticed;
import de.malkusch.ha.automation.model.CheckTrashDayService.TomorrowsTrashDayReminded;

public final class CheckTrashDayServiceTests {

    public static TomorrowsTrashDayNoticed tomorrowsTrashDayNoticed(String trashCollection) {
        return new TomorrowsTrashDayNoticed(trashCollection(trashCollection));
    }

    public static TomorrowsTrashDayReminded tomorrowsTrashDayReminded(String trashCollection) {
        return new TomorrowsTrashDayReminded(trashCollection(trashCollection));
    }
}
