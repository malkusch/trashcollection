package de.malkusch.ha.shared.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
class JsonConfiguration {

    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper() //
                .registerModule(new JavaTimeModule()) //
                .registerModule(new Jdk8Module());
    }
}
