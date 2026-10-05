package com.uitap.driver;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import com.uitap.config.ConfigReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Owns the Playwright and Browser instances. Playwright objects are not thread-safe, so each
 * worker thread gets its own {@link Playwright} + {@link Browser} pair, reused across the
 * scenarios that thread executes. Every scenario gets a fresh, isolated {@link BrowserContext}.
 */
public final class PlaywrightFactory {

    private static final Logger LOG = LoggerFactory.getLogger(PlaywrightFactory.class);

    private static final ThreadLocal<Playwright> PLAYWRIGHT = new ThreadLocal<>();
    private static final ThreadLocal<Browser> BROWSER = new ThreadLocal<>();
    private static final Queue<Playwright> ALL_INSTANCES = new ConcurrentLinkedQueue<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(PlaywrightFactory::closeAll, "playwright-shutdown"));
    }

    private PlaywrightFactory() {
    }

    public static BrowserContext newContext() {
        ConfigReader config = ConfigReader.get();
        Browser.NewContextOptions options = new Browser.NewContextOptions()
                .setViewportSize(config.viewportWidth(), config.viewportHeight());
        if (config.videoEnabled()) {
            options.setRecordVideoDir(Paths.get("target", "videos"));
        }
        BrowserContext context = browser().newContext(options);
        context.setDefaultTimeout(config.defaultTimeoutMillis());
        context.setDefaultNavigationTimeout(config.navigationTimeoutMillis());
        return context;
    }

    private static Browser browser() {
        Browser browser = BROWSER.get();
        if (browser == null || !browser.isConnected()) {
            browser = launch(playwright(), ConfigReader.get());
            BROWSER.set(browser);
        }
        return browser;
    }

    private static Playwright playwright() {
        Playwright playwright = PLAYWRIGHT.get();
        if (playwright == null) {
            playwright = Playwright.create();
            PLAYWRIGHT.set(playwright);
            ALL_INSTANCES.add(playwright);
        }
        return playwright;
    }

    private static Browser launch(Playwright playwright, ConfigReader config) {
        BrowserType type = switch (config.browser()) {
            case "chromium", "chrome" -> playwright.chromium();
            case "firefox" -> playwright.firefox();
            case "webkit", "safari" -> playwright.webkit();
            default -> throw new IllegalArgumentException("Unsupported browser: " + config.browser());
        };
        LOG.info("Launching {} (headless={}) on thread {}",
                type.name(), config.headless(), Thread.currentThread().getName());
        return type.launch(new BrowserType.LaunchOptions()
                .setHeadless(config.headless())
                .setSlowMo(config.slowMoMillis()));
    }

    private static void closeAll() {
        Playwright playwright;
        while ((playwright = ALL_INSTANCES.poll()) != null) {
            try {
                playwright.close();
            } catch (RuntimeException e) {
                LOG.debug("Ignoring error while closing Playwright on shutdown", e);
            }
        }
    }
}
