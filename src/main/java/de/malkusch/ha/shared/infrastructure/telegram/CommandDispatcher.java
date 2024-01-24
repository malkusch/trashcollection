package de.malkusch.ha.shared.infrastructure.telegram;

import static com.pengrad.telegrambot.UpdatesListener.CONFIRMED_UPDATES_ALL;
import static de.malkusch.ha.shared.infrastructure.event.EventPublisher.publish;
import static de.malkusch.ha.shared.infrastructure.telegram.CommandParser.ReactionMessage.Reaction.IGNORED;
import static de.malkusch.ha.shared.infrastructure.telegram.CommandParser.ReactionMessage.Reaction.THUMBS_UP;
import static java.util.Arrays.stream;

import java.util.Collection;
import java.util.List;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.reaction.ReactionType;
import com.pengrad.telegrambot.model.reaction.ReactionTypeEmoji;
import com.pengrad.telegrambot.request.GetUpdates;

import de.malkusch.ha.automation.presentation.Help;
import de.malkusch.ha.shared.infrastructure.event.Event;
import de.malkusch.ha.shared.infrastructure.telegram.CommandParser.CommandHelp;
import de.malkusch.ha.shared.infrastructure.telegram.CommandParser.Message;
import de.malkusch.ha.shared.infrastructure.telegram.CommandParser.ReactionMessage;
import de.malkusch.ha.shared.infrastructure.telegram.CommandParser.TextMessage;
import de.malkusch.ha.shared.infrastructure.telegram.CommandParser.UnknownMessage;

public final class CommandDispatcher implements AutoCloseable {

    private final TelegramApi telegram;
    private final Collection<CommandHandler<?>> handlers;
    private final Help help;

    public CommandDispatcher(TelegramApi telegram, Collection<CommandHandler<?>> handlers, Help help) {
        this.telegram = telegram;
        this.handlers = handlers;
        this.help = help;

        var request = new GetUpdates().allowedUpdates("message", "message_reaction");
        telegram.api.setUpdatesListener(updatesListener(), request);
    }

    UpdatesListener updatesListener() {
        return this::onUpdate;
    }

    private int onUpdate(List<Update> updates) {
        return updates.stream().mapToInt(this::onUpdate).reduce((first, second) -> second)
                .orElse(CONFIRMED_UPDATES_ALL);
    }

    public record UnkownCommandReceived(Message command, CommandHelp help) implements Event {

    }

    private int onUpdate(Update update) {
        var id = update.updateId();
        var message = message(update);
        var handled = handlers.stream() //
                .anyMatch(it -> it.parseAndHandle(message));
        if (!handled) {
            publish(new UnkownCommandReceived(message, help.parser().help()));
        }
        return id;
    }

    private static Message message(Update update) {
        if (update.message() != null && update.message().text() != null) {
            return new TextMessage(update.message().text());
        }

        if (update.messageReaction() != null) {
            var messageId = update.messageReaction().messageId();
            var reactions = stream(update.messageReaction().newReaction()) //
                    .map(it -> reaction(it)) //
                    .toList();
            return new ReactionMessage(messageId, reactions);
        }

        return new UnknownMessage();
    }

    private static ReactionMessage.Reaction reaction(ReactionType reaction) {
        if (reaction instanceof ReactionTypeEmoji emoji) {
            var value = emoji.emoji();
            if (value == null) {
                return IGNORED;

            } else if (value.equals("👍")) {
                return THUMBS_UP;
            }
        }
        return IGNORED;
    }

    @Override
    public void close() throws Exception {
        telegram.api.removeGetUpdatesListener();
    }
}
