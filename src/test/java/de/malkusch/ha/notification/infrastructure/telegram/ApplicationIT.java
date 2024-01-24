package de.malkusch.ha.notification.infrastructure.telegram;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import de.malkusch.ha.Application;
import de.malkusch.ha.test.DisabledIfPR;

@SpringBootTest(classes = Application.class)
@ActiveProfiles({ "test", "telegram" })
@DisabledIfPR
public class ApplicationIT {

    @Test
    void contextLoads() {
    }
}