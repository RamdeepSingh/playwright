package com.uitap.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

/**
 * http://uitestingplayground.com/dynamicid - the button's id is regenerated on every load,
 * so it is located by its accessible role and name only.
 */
public class DynamicIdPage extends BasePage {

    /**
     * The page gives no visible feedback on click, so before clicking we attach a one-shot
     * listener that stamps this attribute on the button when it actually receives the click
     * event. This works in every engine (WebKit does not focus buttons on mouse click).
     */
    public static final String CLICK_RECEIVED_ATTRIBUTE = "data-test-click-received";

    private final Locator heading;
    private final Locator dynamicIdButton;

    public DynamicIdPage(Page page) {
        super(page);
        this.heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Dynamic ID"));
        this.dynamicIdButton = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Button with Dynamic ID").setExact(true));
    }

    @Override
    protected String path() {
        return "/dynamicid";
    }

    public Locator heading() {
        return heading;
    }

    public Locator dynamicIdButton() {
        return dynamicIdButton;
    }

    public void clickDynamicIdButton() {
        dynamicIdButton.evaluate(
                "(el, attr) => el.addEventListener('click', () => el.setAttribute(attr, 'true'), { once: true })",
                CLICK_RECEIVED_ATTRIBUTE);
        dynamicIdButton.click();
    }

    public String dynamicIdButtonId() {
        return dynamicIdButton.getAttribute("id");
    }
}
