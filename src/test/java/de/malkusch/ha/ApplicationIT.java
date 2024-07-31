package de.malkusch.ha;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class ApplicationIT {

    @Test
    void contextLoads() {
    }
}
