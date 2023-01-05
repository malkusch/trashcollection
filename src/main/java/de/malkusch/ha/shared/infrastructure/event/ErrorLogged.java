package de.malkusch.ha.shared.infrastructure.event;

import java.util.UUID;

public record ErrorLogged(String reference, String message) implements Event {

    public ErrorLogged(String message) {
        this(buildReference(), message);
    }

    private static String buildReference() {
        return UUID.randomUUID().toString();
    }

}
