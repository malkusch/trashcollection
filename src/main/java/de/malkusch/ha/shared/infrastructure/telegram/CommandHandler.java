package de.malkusch.ha.shared.infrastructure.telegram;

import java.util.function.Function;

public abstract class CommandHandler<T extends Command> {

    public interface Parser<T extends Command> {
        abstract public T parse(String command);

        public record CommandHelp(String command, String description) {

            @Override
            public String toString() {
                if (description == null) {
                    return command;
                }
                return String.format("%s - %s", command, description);
            }
        }

        abstract public CommandHelp help();

        public static <T extends Command> Parser<T> noArgumentCommand(CommandHelp help, Function<String, T> factory) {
            var command = help.command;
            class SingleArgumentParser implements Parser<T> {

                @Override
                public T parse(String commandString) {
                    if (command.equals(commandString)) {
                        return factory.apply(commandString);
                    }
                    return null;
                }

                @Override
                public CommandHelp help() {
                    return help;
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