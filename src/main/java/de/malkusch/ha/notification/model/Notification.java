package de.malkusch.ha.notification.model;

public record Notification(String message) {

    @Override
    public String toString() {
        return message;
    }
}