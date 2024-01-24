package de.malkusch.ha.shared.infrastructure.event;

public final class EventPublisherTests {

    public static void ignoreEvents() {
        EventPublisher.publisher = it -> {
        };
    }

}
