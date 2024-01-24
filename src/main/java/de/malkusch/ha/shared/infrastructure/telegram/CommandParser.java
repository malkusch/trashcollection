package de.malkusch.ha.shared.infrastructure.telegram;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import de.malkusch.ha.shared.infrastructure.telegram.CommandParser.ReactionMessage.Reaction;

public interface CommandParser<T extends Command> {

    public interface Message {
    }

    public record TextMessage(String message) implements Message {
    }

    public record ReactionMessage(int messageId, List<Reaction> reactions) implements Message {

        public boolean contains(Reaction reaction) {
            return reactions.contains(reaction);
        }

        public static enum Reaction {
            THUMBS_UP, IGNORED
        }

    }

    public record UnknownMessage() implements Message {

    }

    abstract public T parse(Message message);

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

    public static class Builder<T extends Command> {

        private CommandHelp help;

        public Builder<T> help(CommandHelp help) {
            this.help = help;
            return this;
        }
        
        public Builder<T> help(String command, String description) {
            return help(new CommandHelp(command, description));
        }

        @FunctionalInterface
        public interface PartialParser<T extends Command, M extends Message> {
            T parse(M message);
        }

        private final List<PartialParser<T, Message>> parsers = new ArrayList<>();

        public Builder<T> and(PartialParser<T, Message> parser) {
            parsers.add(parser);
            return this;
        }

        public Builder<T> andTextParser(PartialParser<T, TextMessage> textParser) {
            PartialParser<T, Message> messageParser = it -> {
                if (it instanceof TextMessage message) {
                    return textParser.parse(message);
                }
                return null;
            };
            return and(messageParser);
        }

        public Builder<T> andTextMatchesHelp(Function<String, T> factory) {
            return andTextParser(t -> {
                if (help.command.equals(t.message)) {
                    return factory.apply(t.message);
                }
                return null;
            });
        }

        public Builder<T> andReactionParser(PartialParser<T, ReactionMessage> reactionParser) {
            PartialParser<T, Message> messageParser = it -> {
                if (it instanceof ReactionMessage reaction) {
                    return reactionParser.parse(reaction);
                }
                return null;
            };
            return and(messageParser);
        }

        public Builder<T> andReactionParser(Reaction reaction, Function<ReactionMessage, T> factory) {
            return andReactionParser(m -> {
                if (m.contains(reaction)) {
                    return factory.apply(m);
                }
                return null;
            });
        }

        public CommandParser<T> build() {
            return new CommandParser<T>() {

                @Override
                public T parse(Message message) {
                    return parsers.stream() //
                            .map(it -> it.parse(message)) //
                            .filter(it -> it != null) //
                            .findAny() //
                            .orElse(null);
                }

                @Override
                public CommandHelp help() {
                    return help;
                }
            };
        }
    }

    public static <T extends Command> CommandParser<T> noArgumentCommand(CommandHelp help,
            Function<String, T> factory) {

        var parser = new Builder<T>() //
                .help(help) //
                .andTextMatchesHelp(factory) //
                .build();

        return parser;
    }

}