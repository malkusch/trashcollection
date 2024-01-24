package de.malkusch.ha.notification.infrastructure.telegram;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import de.malkusch.ha.notification.model.NotificationService;
import de.malkusch.ha.shared.infrastructure.http.HttpConfiguration.HttpProperties;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
class TelegramConfiguration {

    @Data
    @Component
    @ConfigurationProperties("notification.telegram")
    static class TelegramProperties {
        private String token;
        private String chatId;
    }

    private final TelegramProperties properties;
    private final HttpProperties httpProperties;

    @Bean
    NotificationService notificationService() {
        if (properties.chatId == null || properties.token == null) {
            log.warn("Telegram chatId or token are empty, falling back to logging notifications");
            return new LoggingNotificationService();
        }
        return new TelegramNotificationService(properties.chatId, properties.token, httpProperties.getTimeout());
    }
}
