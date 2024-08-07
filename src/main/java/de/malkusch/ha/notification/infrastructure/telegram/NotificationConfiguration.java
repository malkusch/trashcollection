package de.malkusch.ha.notification.infrastructure.telegram;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.malkusch.ha.notification.model.NotificationService;
import de.malkusch.telgrambot.TelegramApi;

@Configuration
class NotificationConfiguration {

    @Bean
    NotificationService notificationService(Optional<TelegramApi> telegram) {
        Optional<NotificationService> service = telegram.map(TelegramNotificationService::new);
        return service.orElse(new LoggingNotificationService());
    }
}
