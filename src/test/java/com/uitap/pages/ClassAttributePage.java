package com.uitap.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * http://uitestingplayground.com/classattr - three buttons share the text "Button" and their
 * order on the page, as well as the order of classes within each class attribute, is
 * shuffled on every load. The CSS class selector {@code .btn-primary} matches a whole class
 * token regardless of its position, so it is robust to that shuffling (equivalent to the XPath
 * {@code contains(concat(' ', normalize-space(@class), ' '), ' btn-primary ')}).
 */
public class ClassAttributePage extends BasePage {

    private final Locator playgroundButtons;
    private final Locator primaryButton;

    public ClassAttributePage(Page page) {
        super(page);
        this.playgroundButtons = page.locator("section button.btn");
        this.primaryButton = page.locator("section button.btn-primary");
    }

    @Override
    protected String path() {
        return "/classattr";
    }

    public Locator playgroundButtons() {
        return playgroundButtons;
    }

    public Locator primaryButton() {
        return primaryButton;
    }

    /** Clicks the primary (blue) button and accepts the resulting alert. */
    public DialogDetails clickPrimaryButtonAndAcceptAlert() {
        return acceptDialogTriggeredBy(primaryButton::click);
    }
}
