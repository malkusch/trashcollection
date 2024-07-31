package de.malkusch.ha.shared.infrastructure.telegram;

import java.time.Duration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import de.malkusch.ha.shared.infrastructure.http.HttpConfiguration.HttpProperties;
import de.malkusch.telgrambot.TelegramApi;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty({ "notification.telegram.token", "notification.telegram.chatId" })
public class TelegramConfiguration {

    @Data
    @Component
    @ConfigurationProperties("notification.telegram")
    static class TelegramProperties {
        private String token;
        private String chatId;
        private Duration polling;
    }

    private final TelegramProperties properties;
    private final HttpProperties httpProperties;

    @Bean
    TelegramApi telegram() {
        var timeouts = new TelegramApi.Timeouts(httpProperties.getTimeout(), properties.polling);
        return new TelegramApi(properties.chatId, properties.token, timeouts);
    }
}
