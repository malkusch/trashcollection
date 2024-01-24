package de.malkusch.ha.shared.infrastructure.telegram;

import de.malkusch.ha.shared.infrastructure.telegram.CommandParser.Message;

public abstract class CommandHandler<T extends Command> {

    abstract public CommandParser<T> parser();

    abstract public void handle(T command);

    final boolean parseAndHandle(Message command) {
        var parsed = parser().parse(command);
        if (parsed == null) {
            return false;
        }
        handle(parsed);
        return true;
    }
}