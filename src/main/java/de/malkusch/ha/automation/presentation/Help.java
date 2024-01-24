package de.malkusch.ha.automation.presentation;

import static de.malkusch.ha.shared.infrastructure.telegram.CommandHandler.Parser.noArgumentCommand;

import org.springframework.stereotype.Service;

import de.malkusch.ha.automation.presentation.Help.PrintHelp;
import de.malkusch.ha.shared.infrastructure.telegram.Command;
import de.malkusch.ha.shared.infrastructure.telegram.CommandHandler;
import de.malkusch.ha.shared.infrastructure.telegram.HelpService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public final class Help extends CommandHandler<PrintHelp> {

    private final HelpService help;

    public record PrintHelp() implements Command {
    }

    @Override
    public Parser<PrintHelp> parser() {
        return noArgumentCommand("help", it -> new PrintHelp());
    }

    @Override
    public void onCommand(PrintHelp command) {
        help.printHelp();
    }
}
