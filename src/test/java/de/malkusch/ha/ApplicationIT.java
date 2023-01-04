package de.malkusch.ha;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles(profiles = "test")
@ExtendWith(MockitoExtension.class)
public class ApplicationIT {

    @Test
    void contextLoads() {
    }
}
