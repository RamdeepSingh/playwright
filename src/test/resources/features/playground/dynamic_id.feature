@regression @dynamic-id
Feature: Dynamic ID
  As a test automation engineer
  I want to identify elements without relying on generated IDs
  So that my tests keep working when IDs change between page loads

  Background:
    Given the user is on the Dynamic ID page

  @smoke
  Scenario: Click the button whose ID is regenerated on every load
    Then the button with the dynamic ID is visible and enabled
    When the user clicks the button with the dynamic ID
    Then the button with the dynamic ID has received the click

  Scenario: The button ID changes between page loads
    When the user notes the ID of the button
    And the user reloads the page
    Then the button has a different ID than before
