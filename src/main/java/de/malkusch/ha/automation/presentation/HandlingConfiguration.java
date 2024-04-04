package de.malkusch.ha.automation.presentation;

import java.util.Arrays;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;

import de.malkusch.ha.shared.infrastructure.telegram.TelegramConfiguration;
import de.malkusch.telgrambot.Handler;
import de.malkusch.telgrambot.TelegramApi;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@ConditionalOnBean(TelegramConfiguration.class)
public class HandlingConfiguration {

    private final List list;
    private final Next next;
    private final Done done;
    private final Debug.Changed debugChanged;
    private final Debug.Trashday debugTrashday;
    private final TelegramApi telegram;

    @PostConstruct
    public void setup() {
        telegram.startDispatcher(Arrays.asList( //
                new Handler.TextHandler(List.COMMAND, list), //
                new Handler.TextHandler(Next.COMMAND, next), //
                new Handler.TextHandler(Debug.Changed.COMMAND, debugChanged), //
                new Handler.TextHandler(Debug.Trashday.COMMAND, debugTrashday), //
                new Handler.CallbackHandler(Done.COMMAND, done) //
        ));
    }

}
