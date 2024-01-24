package de.malkusch.ha.shared.infrastructure.telegram;

import java.util.function.Function;

public abstract class CommandHandler<T extends Command> {

    public interface Parser<T extends Command> {
        abstract public T parse(String command);

        abstract public String help();

        public static <T extends Command> Parser<T> noArgumentCommand(String command, Function<String, T> factory) {
            class SingleArgumentParser implements Parser<T> {

                @Override
                public T parse(String commandString) {
                    if (command.equals(commandString)) {
                        return factory.apply(commandString);
                    }
                    return null;
                }

                @Override
                public String help() {
                    return command;
                }
            }

            return new SingleArgumentParser();
        }

    }

    abstract public Parser<T> parser();

    abstract public void onCommand(T command);

    final boolean parseAndHandle(String command) {
        var parsed = parser().parse(command);
        if (parsed == null) {
            return false;
        }
        onCommand(parsed);
        return true;
    }
}