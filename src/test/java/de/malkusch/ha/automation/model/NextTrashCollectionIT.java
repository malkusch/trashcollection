package de.malkusch.ha.automation.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import de.malkusch.ha.Application;

@SpringBootTest(classes = Application.class)
@ActiveProfiles(profiles = "test")
public class NextTrashCollectionIT {

    @Autowired
    private NextTrashCollection next;

    @Test
    void nextCollectionShouldBeWithinAMonth() {
        var now = LocalDate.now();

        var next = this.next.nextTrashCollection();

        assertThat(next.date()).isBetween(now, now.plusWeeks(4));
    }
}
