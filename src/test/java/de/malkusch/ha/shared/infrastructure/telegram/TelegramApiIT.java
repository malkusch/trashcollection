package de.malkusch.ha.shared.infrastructure.telegram;

import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.StringUtils.isNoneBlank;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.GetChat;
import com.pengrad.telegrambot.request.PinChatMessage;
import com.pengrad.telegrambot.request.UnpinChatMessage;

import de.malkusch.ha.test.DisabledIfPR;

@DisabledIfPR
public class TelegramApiIT {

    private final String chatId = System.getenv("TELEGRAM_CHAT_ID");
    private final String token = System.getenv("TELEGRAM_TOKEN");
    private TelegramApi api;

    @BeforeEach
    public void setup() {
        assertTrue(isNoneBlank(token, chatId));

        var timeout = Duration.ofSeconds(10);
        api = new TelegramApi(chatId, token, timeout);
    }

    @Test
    public void shouldSendMessage() {
        var message = String.format("Test %s %s", LocalDateTime.now(), randomUUID());

        api.send(message);

        var lastMessage = api.lastMessage();
        assertEquals(message, lastMessage.text());
        assertEquals(message, fetchMessage(lastMessage.messageId()));
        delete(lastMessage);
    }

    private String fetchMessage(int messageId) {
        api.execute(new PinChatMessage(chatId, messageId));
        try {
            var request = new GetChat(chatId);
            var response = api.execute(request);
            if (response.chat().pinnedMessage() == null) {
                return null;
            }
            var lastMessage = response.chat().pinnedMessage().text();
            return lastMessage;

        } finally {
            api.execute(new UnpinChatMessage(chatId).messageId(messageId));
        }
    }

    private void delete(Message message) {
        var request = new DeleteMessage(chatId, message.messageId());
        api.execute(request);
    }
}
