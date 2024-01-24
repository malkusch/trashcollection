package de.malkusch.ha.shared.infrastructure.telegram;

import static com.pengrad.telegrambot.UpdatesListener.CONFIRMED_UPDATES_ALL;
import static de.malkusch.ha.shared.infrastructure.event.EventPublisher.publish;

import java.util.Collection;
import java.util.List;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;

import de.malkusch.ha.automation.presentation.Help;
import de.malkusch.ha.shared.infrastructure.event.Event;

public final class CommandDispatcher implements AutoCloseable {

    private final TelegramApi telegram;
    private final Collection<CommandHandler<?>> handlers;
    private final Help help;

    public CommandDispatcher(TelegramApi telegram, Collection<CommandHandler<?>> handlers, Help help) {
        this.telegram = telegram;
        this.handlers = handlers;
        this.help = help;

        telegram.api.setUpdatesListener(updatesListener());
    }

    UpdatesListener updatesListener() {
        return this::onUpdate;
    }

    private int onUpdate(List<Update> updates) {
        return updates.stream().mapToInt(this::onUpdate).reduce((first, second) -> second)
                .orElse(CONFIRMED_UPDATES_ALL);
    }

    public record UnkownCommandReceived(String command, String help) implements Event {

    }

    private int onUpdate(Update update) {
        var id = update.updateId();
        if (update.message() == null) {
            return id;
        }
        if (update.message().text() == null) {
            return id;
        }
        var command = update.message().text();
        var handled = handlers.stream() //
                .anyMatch(it -> it.parseAndHandle(command));
        if (!handled) {
            publish(new UnkownCommandReceived(command, help.parser().help()));
        }
        return id;
    }

    @Override
    public void close() throws Exception {
        telegram.api.removeGetUpdatesListener();
    }
}
