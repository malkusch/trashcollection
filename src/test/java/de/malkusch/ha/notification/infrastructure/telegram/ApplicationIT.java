package de.malkusch.ha.notification.infrastructure.telegram;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import de.malkusch.ha.Application;

@SpringBootTest(classes = Application.class)
@ActiveProfiles({ "test", "telegram" })
@DisabledIfEnvironmentVariable(named = "GITHUB_EVENT_NAME", matches = "pull_request")
public class ApplicationIT {

    @Test
    void contextLoads() {
    }
}