package de.malkusch.ha.automation.presentation;

import static de.malkusch.ha.shared.infrastructure.telegram.CommandHandler.Parser.noArgumentCommand;

import org.springframework.stereotype.Service;

import de.malkusch.ha.automation.application.PrintNextCollectionApplicationService;
import de.malkusch.ha.automation.presentation.Next.PrintNext;
import de.malkusch.ha.shared.infrastructure.telegram.Command;
import de.malkusch.ha.shared.infrastructure.telegram.CommandHandler;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public final class Next extends CommandHandler<PrintNext> {

    private final PrintNextCollectionApplicationService service;

    public record PrintNext() implements Command {
    }

    @Override
    public Parser<PrintNext> parser() {
        return noArgumentCommand("next", it -> new PrintNext());
    }

    @Override
    public void onCommand(PrintNext command) {
        service.printNext();
    }
}
