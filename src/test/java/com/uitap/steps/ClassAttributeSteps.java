package com.uitap.steps;

import com.uitap.context.TestContext;
import com.uitap.pages.ClassAttributePage;
import com.uitap.pages.DialogDetails;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

public class ClassAttributeSteps {

    private static final String LAST_DIALOG = "classAttribute.lastDialog";
    private static final Pattern PRIMARY_CLASS_TOKEN = Pattern.compile("(^|\\s)btn-primary(\\s|$)");

    private final TestContext context;

    public ClassAttributeSteps(TestContext context) {
        this.context = context;
    }

    private ClassAttributePage page() {
        return context.classAttributePage();
    }

    @Given("the user is on the Class Attribute page")
    public void theUserIsOnTheClassAttributePage() {
        page().open();
        assertThat(page().playgroundButtons()).hasCount(3);
    }

    @Then("exactly one primary button is shown")
    public void exactlyOnePrimaryButtonIsShown() {
        assertThat(page().primaryButton()).hasCount(1);
        assertThat(page().primaryButton()).isVisible();
        assertThat(page().primaryButton()).hasClass(PRIMARY_CLASS_TOKEN);
    }

    @When("the user clicks the primary button and accepts the alert")
    public void theUserClicksThePrimaryButtonAndAcceptsTheAlert() {
        context.put(LAST_DIALOG, page().clickPrimaryButtonAndAcceptAlert());
    }

    @Then("an alert with the message {string} was shown")
    public void anAlertWithTheMessageWasShown(String expectedMessage) {
        DialogDetails dialog = context.get(LAST_DIALOG, DialogDetails.class);
        assertThat(dialog.type()).isEqualTo("alert");
        assertThat(dialog.message()).isEqualTo(expectedMessage);
    }
}
