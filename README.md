# UI Testing Playground - Java BDD UI Automation

BDD UI test automation for [UI Testing Playground](http://uitestingplayground.com) built with
Java 17, Playwright for Java, Cucumber 7 (JUnit 5 Platform), PicoContainer, AssertJ, SLF4J/Logback
and Allure.

Covered scenarios:

| Feature | Page | What it proves |
|---|---|---|
| `dynamic_id.feature` | `/dynamicid` | The button is located by role + accessible name, never by its regenerated `id`; it is visible, enabled and receives the click; its `id` differs across page loads. |
| `class_attribute.feature` | `/classattr` | The blue button is located with the CSS class selector `.btn-primary`, which is robust to class order/position; clicking it raises an alert `"Primary button pressed"` that is accepted and asserted. |

## Prerequisites

- JDK 17+
- Maven 3.8+
- Internet access (the site and the one-time browser download)

Install the browser once (Playwright also auto-downloads browsers on first run):

```bash
mvn test-compile exec:java -Dexec.args="install chromium"
# all browsers + Linux OS deps (CI):  mvn test-compile exec:java -Dexec.args="install --with-deps"
```

## Running

```bash
mvn test                                         # all scenarios (excluding @wip), headless chromium
mvn test -Dcucumber.filter.tags=@smoke           # by tag
mvn test -Dcucumber.filter.tags="@class-attribute and not @wip"
mvn test -Dbrowser=firefox                       # chromium | firefox | webkit
mvn test -Dheadless=false -Dslow.mo.ms=300       # headed, slowed down
mvn test -Denv=staging                           # loads config/staging.properties
mvn test -Dcucumber.execution.parallel.config.fixed.parallelism=4 \
         -Dcucumber.execution.parallel.config.fixed.max-pool-size=4
mvn test -Dvideo.enabled=true                    # videos in target/videos
```

### Configuration

Values resolve in this order: `-Dkey=value` system property, then environment variable (`base.url` becomes
`BASE_URL`), then `src/test/resources/config/<env>.properties` (`env` defaults to `qa`, also settable
via the `TEST_ENV` env var).

| Key | Default | Purpose |
|---|---|---|
| `base.url` | `http://uitestingplayground.com` | Site under test |
| `browser` | `chromium` | `chromium`, `firefox`, `webkit` |
| `headless` | `true` | Headless/headed |
| `slow.mo.ms` | `0` | Slow motion for debugging |
| `timeout.default.ms` / `timeout.navigation.ms` | `15000` / `30000` | Playwright timeouts |
| `viewport.width` / `viewport.height` | `1366` / `768` | Viewport |
| `trace.on.failure` | `true` | Save Playwright trace for failed scenarios |
| `video.enabled` | `false` | Record video |

Secrets (if ever needed) must come from environment variables, never from committed files.

## Reports and artifacts

| Artifact | Location |
|---|---|
| Cucumber HTML | `target/cucumber-reports/cucumber.html` |
| Cucumber JSON | `target/cucumber-reports/cucumber.json` |
| Allure results | `target/allure-results` (view with `allure serve target/allure-results`) |
| Failure screenshots | Attached to the Cucumber and Allure reports |
| Playwright traces (failures) | `target/traces/<scenario>_L<line>.zip` (open with `npx playwright show-trace <zip>` or https://trace.playwright.dev) |
| Surefire | `target/surefire-reports` |

## Project layout

```
src/test/java/com/uitap/
  runners/TestRunner.java        JUnit Platform @Suite running the Cucumber engine
  hooks/Hooks.java               fresh BrowserContext per scenario, screenshot + trace on failure
  context/TestContext.java       PicoContainer-injected per-scenario state and page objects
  driver/PlaywrightFactory.java  ThreadLocal Playwright/Browser per worker thread
  config/ConfigReader.java       env properties + -D / env var overrides
  pages/                         BasePage, DynamicIdPage, ClassAttributePage, DialogDetails
  steps/                         DynamicIdSteps, ClassAttributeSteps (no selectors)
src/test/resources/
  features/playground/           dynamic_id.feature, class_attribute.feature
  config/                        qa.properties, staging.properties
  junit-platform.properties      glue, plugins, tag filter, parallelism
  logback-test.xml, allure.properties
.github/workflows/ui-tests.yml   CI: install browser, run tests, upload reports
```

## Adding a new feature

1. Write a declarative `.feature` file under `src/test/resources/features/<area>/` and tag it
   (`@smoke`, `@regression`, `@wip` for in-progress work, which is excluded by default).
2. Create `pages/<Name>Page.java` extending `BasePage`: locators as `Locator` fields (prefer
   `getByRole` > `getByLabel` > `getByPlaceholder` > `getByText` > `getByTestId` > CSS) and
   intent-revealing action methods. No assertions in page objects.
3. Expose it lazily from `TestContext`.
4. Create `steps/<Name>Steps.java` taking `TestContext` in its constructor; call page objects and
   assert with `PlaywrightAssertions.assertThat(locator)` (web-first) or AssertJ. No selectors and
   no sleeps in steps.
5. Run `mvn test -Dcucumber.filter.tags=@your-tag`.
