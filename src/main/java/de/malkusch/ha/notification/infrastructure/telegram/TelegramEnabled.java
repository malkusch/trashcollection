package de.malkusch.ha.notification.infrastructure.telegram;

import de.malkusch.ha.shared.infrastructure.telegram.TelegramConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@ConditionalOnBean(TelegramConfiguration.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface TelegramEnabled {
}
