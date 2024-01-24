package de.malkusch.ha.notification.infrastructure.telegram;

import static org.apache.commons.lang3.StringUtils.isAnyBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import de.malkusch.ha.notification.model.NotificationService;
import de.malkusch.ha.shared.infrastructure.http.HttpConfiguration.HttpProperties;
import de.malkusch.ha.shared.infrastructure.telegram.TelegramApi;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
class NotificationConfiguration {

    @Bean
    NotificationService notificationService(TelegramApi telegram) {
        return new TelegramNotificationService(telegram);
    }
}
