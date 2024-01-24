package de.malkusch.ha.automation.presentation;

import static de.malkusch.ha.shared.infrastructure.telegram.CommandParser.noArgumentCommand;

import org.springframework.stereotype.Service;

import de.malkusch.ha.automation.presentation.Help.PrintHelp;
import de.malkusch.ha.shared.infrastructure.telegram.Command;
import de.malkusch.ha.shared.infrastructure.telegram.CommandHandler;
import de.malkusch.ha.shared.infrastructure.telegram.CommandParser;
import de.malkusch.ha.shared.infrastructure.telegram.CommandParser.CommandHelp;
import de.malkusch.ha.shared.infrastructure.telegram.HelpService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public final class Help extends CommandHandler<PrintHelp> {

    private final HelpService help;

    public record PrintHelp() implements Command {
    }

    @Override
    public CommandParser<PrintHelp> parser() {
        return noArgumentCommand(new CommandHelp("help", "Listed alle Befehle auf"), it -> new PrintHelp());
    }

    @Override
    public void handle(PrintHelp command) {
        help.printHelp();
    }
}
