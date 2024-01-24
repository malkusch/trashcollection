package de.malkusch.ha.shared.infrastructure.telegram;

import static de.malkusch.ha.shared.infrastructure.event.EventPublisher.publish;

import java.util.Collection;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import de.malkusch.ha.shared.infrastructure.event.Event;
import lombok.RequiredArgsConstructor;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
@RequiredArgsConstructor
public class HelpService {

    private final Collection<CommandHandler<?>> handlers;

    public record HelpPrinted(List<String> commands) implements Event {

    }

    public void printHelp() {
        var commands = handlers.stream() //
                .map(it -> it.parser().help()) //
                .toList();

        publish(new HelpPrinted(commands));
    }
}
