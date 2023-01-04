package de.malkusch.ha.notification.infrastructure.telegram;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.TelegramBot;

import de.malkusch.ha.notification.model.NotificationService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
class TelegramConfiguration {

    @ConfigurationProperties("notification.telegram")
    @Component
    @Data
    static class TelegramProperties {
        private String token;
        private String chatId;
    }

    @Bean
    NotificationService notificationService(TelegramProperties properties) {
        if (properties.chatId == null || properties.token == null) {
            log.warn("Telegram chatId or token are empty, falling back to logging notifications");
            return new LoggingNotificationService();
        }
        var api = new TelegramBot(properties.token);
        return new TelegramNotificationService(api, properties.chatId);
    }
}
