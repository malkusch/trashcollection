package de.malkusch.ha.shared.infrastructure.telegram;

import java.util.Collection;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import de.malkusch.ha.automation.presentation.Help;
import de.malkusch.ha.shared.infrastructure.http.HttpConfiguration.HttpProperties;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty({ "notification.telegram.token", "notification.telegram.chatId" })
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
    TelegramApi telegram() {
        return new TelegramApi(properties.chatId, properties.token, httpProperties.getTimeout());
    }

    @Bean
    CommandDispatcher dispatcher(Collection<CommandHandler<?>> handlers, Help help) {
        return new CommandDispatcher(telegram(), handlers, help);
    }
}
