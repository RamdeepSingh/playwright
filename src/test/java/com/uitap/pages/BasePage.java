package com.uitap.pages;

import com.microsoft.playwright.Page;
import com.uitap.config.ConfigReader;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Shared page-object behaviour. Page objects expose locators and intent-revealing actions;
 * they never assert.
 */
public abstract class BasePage {

    protected final Page page;

    protected BasePage(Page page) {
        this.page = page;
    }

    /** Relative path of this page from the configured base URL, e.g. {@code /dynamicid}. */
    protected abstract String path();

    public void open() {
        page.navigate(ConfigReader.get().baseUrl() + path());
    }

    public void reload() {
        page.reload();
    }

    /**
     * Performs {@code action}, accepts the JavaScript dialog it opens and returns the dialog's
     * type and message. Waits (event-driven, no sleeps) until the dialog has actually appeared.
     */
    protected DialogDetails acceptDialogTriggeredBy(Runnable action) {
        AtomicReference<DialogDetails> captured = new AtomicReference<>();
        page.onceDialog(dialog -> {
            captured.set(new DialogDetails(dialog.type(), dialog.message()));
            dialog.accept();
        });
        action.run();
        page.waitForCondition(() -> captured.get() != null);
        return captured.get();
    }
}
