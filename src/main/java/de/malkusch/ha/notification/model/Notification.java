package de.malkusch.ha.notification.model;

import de.malkusch.ha.notification.model.Notification.CallbackNotification.Callback;

public sealed interface Notification {

    record TextNotification(String message) implements Notification {

        @Override
        public String toString() {
            return message;
        }
    }

    record SilentNotification(String message) implements Notification {

        @Override
        public String toString() {
            return message;
        }
    }

    record CallbackNotification(String message, Callback callback) implements Notification {
        public record Callback(String name, String payload) {
        }
    }
}
