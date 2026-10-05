package com.uitap.hooks;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Tracing;
import com.uitap.config.ConfigReader;
import com.uitap.context.TestContext;
import com.uitap.driver.PlaywrightFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Hooks {

    private static final Logger LOG = LoggerFactory.getLogger(Hooks.class);

    private final TestContext testContext;
    private final ConfigReader config = ConfigReader.get();

    public Hooks(TestContext testContext) {
        this.testContext = testContext;
    }

    @Before(order = 0)
    public void startBrowserContext(Scenario scenario) {
        LOG.info("Starting scenario '{}' [env={}, browser={}]", scenario.getName(), config.env(), config.browser());
        BrowserContext context = PlaywrightFactory.newContext();
        if (config.traceOnFailure()) {
            context.tracing().start(new Tracing.StartOptions()
                    .setName(fileSafe(scenario))
                    .setScreenshots(true)
                    .setSnapshots(true)
                    .setSources(true));
        }
        Page page = context.newPage();
        testContext.init(context, page);
    }

    @After(order = 0)
    public void closeBrowserContext(Scenario scenario) {
        BrowserContext context = testContext.browserContext();
        try {
            if (scenario.isFailed()) {
                captureFailureArtifacts(scenario);
            } else if (config.traceOnFailure()) {
                context.tracing().stop();
            }
        } finally {
            context.close();
            LOG.info("Finished scenario '{}' with status {}", scenario.getName(), scenario.getStatus());
        }
    }

    private void captureFailureArtifacts(Scenario scenario) {
        try {
            byte[] screenshot = testContext.page().screenshot(new Page.ScreenshotOptions().setFullPage(true));
            scenario.attach(screenshot, "image/png", "failure-screenshot");
        } catch (RuntimeException e) {
            LOG.warn("Could not capture failure screenshot", e);
        }
        if (config.traceOnFailure()) {
            Path tracePath = Paths.get("target", "traces", fileSafe(scenario) + ".zip");
            testContext.browserContext().tracing().stop(new Tracing.StopOptions().setPath(tracePath));
            LOG.warn("Scenario failed; trace saved to {} (open with: npx playwright show-trace {})",
                    tracePath.toAbsolutePath(), tracePath);
        }
    }

    private static String fileSafe(Scenario scenario) {
        String line = scenario.getLine() == null ? "" : "_L" + scenario.getLine();
        return scenario.getName().replaceAll("[^A-Za-z0-9-_]+", "_") + line;
    }
}
