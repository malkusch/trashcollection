package de.malkusch.ha.notification.infrastructure.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;

import de.malkusch.ha.notification.model.Notification;
import de.malkusch.ha.notification.model.NotificationService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class TelegramNotificationService implements NotificationService {

    private final TelegramBot api;
    private final String chatId;

    @Override
    public void send(Notification notification) {
        var request = new SendMessage(chatId, notification.toString());
        var response = execute(request);
        if (response.message() == null) {
            lastMessage = NO_MESSAGE;
            throw new RuntimeException("Sending to Telegram failed: empty message");
        }
        lastMessage = response.message();
    }

    private final static Message NO_MESSAGE = new Message();
    private volatile Message lastMessage = NO_MESSAGE;

    Message lastMessage() {
        return lastMessage;
    }

    <T extends BaseRequest<T, R>, R extends BaseResponse> R execute(BaseRequest<T, R> request) {
        var response = api.execute(request);
        if (!response.isOk()) {
            lastMessage = NO_MESSAGE;
            var error = String.format("Sending to Telegram failed: %d", response.errorCode());
            throw new RuntimeException(error);
        }
        return response;
    }
}
