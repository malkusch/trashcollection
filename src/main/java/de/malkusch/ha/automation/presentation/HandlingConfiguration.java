package de.malkusch.ha.automation.presentation;

import de.malkusch.ha.notification.infrastructure.telegram.TelegramEnabled;
import de.malkusch.telgrambot.TelegramApi;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import static de.malkusch.telgrambot.UpdateReceiver.onCallback;
import static de.malkusch.telgrambot.UpdateReceiver.onCommand;

@Configuration
@RequiredArgsConstructor
@TelegramEnabled
public class HandlingConfiguration {

    private final List list;
    private final Next next;
    private final Done done;
    private final Debug.Changed debugChanged;
    private final Debug.Trashday debugTrashday;
    private final TelegramApi telegram;

    @PostConstruct
    public void setup() {
        telegram.receiveUpdates( //
                onCommand(List.COMMAND, list), //
                onCommand(Next.COMMAND, next), //
                onCommand(Debug.Changed.COMMAND, debugChanged), //
                onCommand(Debug.Trashday.COMMAND, debugTrashday), //
                onCallback(Done.COMMAND, done) //
        );
    }

}
