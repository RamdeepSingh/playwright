@regression @class-attribute
Feature: Class Attribute
  As a test automation engineer
  I want to locate elements by a single CSS class among many
  So that my tests are robust to the order and position of classes

  Background:
    Given the user is on the Class Attribute page

  Scenario: The primary button is identified by its class regardless of class order
    Then exactly one primary button is shown

  @smoke
  Scenario: Clicking the primary button shows a confirmation alert
    When the user clicks the primary button and accepts the alert
    Then an alert with the message "Primary button pressed" was shown
