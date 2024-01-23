package de.malkusch.ha.notification.infrastructure.telegram;

import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.StringUtils.isNoneBlank;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.GetChat;
import com.pengrad.telegrambot.request.PinChatMessage;
import com.pengrad.telegrambot.request.UnpinChatMessage;

import de.malkusch.ha.notification.model.Notification;

public class TelegramNotificationServiceIT {

    private final String chatId = System.getenv("TELEGRAM_CHAT_ID");
    private final String token = System.getenv("TELEGRAM_TOKEN");
    private TelegramNotificationService service;

    @BeforeEach
    public void setup() {
        assertTrue(isNoneBlank(token, chatId));

        var api = new TelegramBot(token);
        service = new TelegramNotificationService(api, chatId);
    }

    @Test
    public void shouldSendMessage() {
        var message = String.format("Test %s %s", LocalDateTime.now(), randomUUID());

        service.send(new Notification(message));

        var lastMessage = service.lastMessage();
        assertEquals(message, lastMessage.text());
        assertEquals(message, fetchLastMessage(lastMessage.messageId()));
        delete(lastMessage);
    }

    private String fetchLastMessage(int messageId) {
        service.execute(new PinChatMessage(chatId, messageId));
        try {
            var request = new GetChat(chatId);
            var response = service.execute(request);
            if (response.chat().pinnedMessage() == null) {
                return null;
            }
            var lastMessage = response.chat().pinnedMessage().text();
            return lastMessage;

        } finally {
            service.execute(new UnpinChatMessage(chatId).messageId(messageId));
        }
    }

    private void delete(Message message) {
        var request = new DeleteMessage(chatId, message.messageId());
        service.execute(request);
    }
}
