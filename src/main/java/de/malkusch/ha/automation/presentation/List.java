package de.malkusch.ha.automation.presentation;

import static de.malkusch.ha.shared.infrastructure.telegram.CommandHandler.Parser.noArgumentCommand;

import org.springframework.stereotype.Service;

import de.malkusch.ha.automation.application.ListNextCollectionsApplicationService;
import de.malkusch.ha.automation.presentation.List.ListNextCollections;
import de.malkusch.ha.shared.infrastructure.telegram.Command;
import de.malkusch.ha.shared.infrastructure.telegram.CommandHandler;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public final class List extends CommandHandler<ListNextCollections> {

    private final ListNextCollectionsApplicationService service;

    public record ListNextCollections() implements Command {
    }

    @Override
    public Parser<ListNextCollections> parser() {
        return noArgumentCommand("list", it -> new ListNextCollections());
    }

    @Override
    public void onCommand(ListNextCollections command) {
        service.listNext();
    }
}
