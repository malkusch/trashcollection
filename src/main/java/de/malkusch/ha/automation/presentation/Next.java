package de.malkusch.ha.automation.presentation;

import static de.malkusch.ha.shared.infrastructure.telegram.CommandParser.noArgumentCommand;

import org.springframework.stereotype.Service;

import de.malkusch.ha.automation.application.PrintNextCollectionApplicationService;
import de.malkusch.ha.automation.presentation.Next.PrintNext;
import de.malkusch.ha.shared.infrastructure.telegram.Command;
import de.malkusch.ha.shared.infrastructure.telegram.CommandHandler;
import de.malkusch.ha.shared.infrastructure.telegram.CommandParser;
import de.malkusch.ha.shared.infrastructure.telegram.CommandParser.CommandHelp;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public final class Next extends CommandHandler<PrintNext> {

    private final PrintNextCollectionApplicationService service;

    public record PrintNext() implements Command {
    }

    @Override
    public CommandParser<PrintNext> parser() {
        return noArgumentCommand(new CommandHelp("next", "Zeigt die nächste Müllabfuhr"), it -> new PrintNext());
    }

    @Override
    public void handle(PrintNext command) {
        service.printNext();
    }
}
