package com.uitap.pages;

/** Snapshot of a JavaScript dialog (alert/confirm/prompt) captured before it was dismissed. */
public record DialogDetails(String type, String message) {
}
