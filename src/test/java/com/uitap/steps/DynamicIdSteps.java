package com.uitap.steps;

import com.uitap.context.TestContext;
import com.uitap.pages.DynamicIdPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

public class DynamicIdSteps {

    private static final String FIRST_LOAD_ID = "dynamicId.firstLoadId";

    private final TestContext context;

    public DynamicIdSteps(TestContext context) {
        this.context = context;
    }

    private DynamicIdPage page() {
        return context.dynamicIdPage();
    }

    @Given("the user is on the Dynamic ID page")
    public void theUserIsOnTheDynamicIdPage() {
        page().open();
        assertThat(page().heading()).isVisible();
    }

    @When("the user clicks the button with the dynamic ID")
    public void theUserClicksTheButtonWithTheDynamicId() {
        page().clickDynamicIdButton();
    }

    @Then("the button with the dynamic ID is visible and enabled")
    public void theButtonWithTheDynamicIdIsVisibleAndEnabled() {
        assertThat(page().dynamicIdButton()).isVisible();
        assertThat(page().dynamicIdButton()).isEnabled();
    }

    @Then("the button with the dynamic ID has received the click")
    public void theButtonWithTheDynamicIdHasReceivedTheClick() {
        assertThat(page().dynamicIdButton()).hasAttribute(DynamicIdPage.CLICK_RECEIVED_ATTRIBUTE, "true");
    }

    @When("the user notes the ID of the button")
    public void theUserNotesTheIdOfTheButton() {
        String id = page().dynamicIdButtonId();
        assertThat(id).as("button id on first load").isNotBlank();
        context.put(FIRST_LOAD_ID, id);
    }

    @When("the user reloads the page")
    public void theUserReloadsThePage() {
        page().reload();
        assertThat(page().dynamicIdButton()).isVisible();
    }

    @Then("the button has a different ID than before")
    public void theButtonHasADifferentIdThanBefore() {
        String firstId = context.get(FIRST_LOAD_ID, String.class);
        String secondId = page().dynamicIdButtonId();
        assertThat(secondId)
                .as("button id after reload")
                .isNotBlank()
                .isNotEqualTo(firstId);
    }
}
