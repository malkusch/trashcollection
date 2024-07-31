package de.malkusch.ha.shared.infrastructure.event;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.ApplicationEventPublisher;

public final class EventPublisherTests implements BeforeEachCallback, AfterEachCallback {

    private final ApplicationEventPublisher mock = mock(ApplicationEventPublisher.class);

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        EventPublisher.publisher = mock;
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        ignoreEvents();
    }

    private static void ignoreEvents() {
        EventPublisher.publisher = it -> {
        };
    }

    public void assertNoEvent() {
        verify(mock, never()).publishEvent(any());
    }

    public void assertEvent(Event event) {
        verify(mock).publishEvent(event);
    }

    public void assertEvents(int times, Event event) {
        verify(mock, times(times)).publishEvent(event);
    }
}
