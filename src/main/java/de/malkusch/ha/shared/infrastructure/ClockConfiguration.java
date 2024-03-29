package de.malkusch.ha.shared.infrastructure;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ClockConfiguration {

    @Bean
    Clock clock() {
        return Clock.systemDefaultZone();
    }
}
