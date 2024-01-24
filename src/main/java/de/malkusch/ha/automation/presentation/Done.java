package de.malkusch.ha.automation.presentation;

import static de.malkusch.ha.shared.infrastructure.telegram.CommandParser.ReactionMessage.Reaction.THUMBS_UP;

import org.springframework.stereotype.Service;

import de.malkusch.ha.automation.application.DoneNextCollectionApplicationService;
import de.malkusch.ha.automation.presentation.Done.CheckNext;
import de.malkusch.ha.shared.infrastructure.telegram.Command;
import de.malkusch.ha.shared.infrastructure.telegram.CommandHandler;
import de.malkusch.ha.shared.infrastructure.telegram.CommandParser;
import de.malkusch.ha.shared.infrastructure.telegram.CommandParser.CommandHelp;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public final class Done extends CommandHandler<CheckNext> {

    private final DoneNextCollectionApplicationService service;

    public record CheckNext() implements Command {
    }

    @Override
    public CommandParser<CheckNext> parser() {
        return new CommandParser.Builder<CheckNext>() //
                .help(new CommandHelp("done", "Markiert die nächste Müllabfuhr als erledigt")) //
                .andTextMatchesHelp(it -> new CheckNext()) //
                .andReactionParser(THUMBS_UP, m -> new CheckNext()) //
                .build();
    }

    @Override
    public void handle(CheckNext command) {
        service.done();
    }
}
