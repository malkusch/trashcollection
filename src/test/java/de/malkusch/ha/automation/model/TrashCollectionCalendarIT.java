package de.malkusch.ha.automation.model;

import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import de.malkusch.ha.Application;

@SpringBootTest(classes = Application.class)
@ActiveProfiles(profiles = "test")
public class TrashCollectionCalendarIT {

    @Autowired
    private TrashCollectionCalendar calendar;

    @Test
    void allCansShouldBeMapped() {
        var trashCans = calendar.findAll() //
                .flatMap(it -> it.trashCans().stream()) //
                .distinct().collect(toSet());

        var expected = Set.of(TrashCan.values());
        assertEquals(expected, trashCans);
    }
}
