package de.malkusch.ha.automation.presentation;

import static de.malkusch.ha.shared.infrastructure.telegram.CommandParser.noArgumentCommand;

import org.springframework.stereotype.Service;

import de.malkusch.ha.automation.application.ListNextCollectionsApplicationService;
import de.malkusch.ha.automation.presentation.List.ListNextCollections;
import de.malkusch.ha.shared.infrastructure.telegram.Command;
import de.malkusch.ha.shared.infrastructure.telegram.CommandHandler;
import de.malkusch.ha.shared.infrastructure.telegram.CommandParser;
import de.malkusch.ha.shared.infrastructure.telegram.CommandParser.CommandHelp;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public final class List extends CommandHandler<ListNextCollections> {

    private final ListNextCollectionsApplicationService service;

    public record ListNextCollections() implements Command {
    }

    @Override
    public CommandParser<ListNextCollections> parser() {
        return noArgumentCommand(new CommandHelp("list", "Zeigt die nächsten Müllabfuhren"), it -> new ListNextCollections());
    }

    @Override
    public void handle(ListNextCollections command) {
        service.listNext();
    }
}
