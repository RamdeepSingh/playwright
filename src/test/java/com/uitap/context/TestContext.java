package com.uitap.context;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.uitap.pages.ClassAttributePage;
import com.uitap.pages.DynamicIdPage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Per-scenario state, created and injected by PicoContainer into hooks and step classes.
 * A new instance exists for every scenario, so nothing leaks between scenarios.
 */
public class TestContext {

    private BrowserContext browserContext;
    private Page page;
    private final Map<String, Object> scenarioData = new HashMap<>();

    private DynamicIdPage dynamicIdPage;
    private ClassAttributePage classAttributePage;

    public void init(BrowserContext browserContext, Page page) {
        this.browserContext = browserContext;
        this.page = page;
    }

    public BrowserContext browserContext() {
        return Objects.requireNonNull(browserContext, "BrowserContext not initialised; did the @Before hook run?");
    }

    public Page page() {
        return Objects.requireNonNull(page, "Page not initialised; did the @Before hook run?");
    }

    public DynamicIdPage dynamicIdPage() {
        if (dynamicIdPage == null) {
            dynamicIdPage = new DynamicIdPage(page());
        }
        return dynamicIdPage;
    }

    public ClassAttributePage classAttributePage() {
        if (classAttributePage == null) {
            classAttributePage = new ClassAttributePage(page());
        }
        return classAttributePage;
    }

    public void put(String key, Object value) {
        scenarioData.put(key, value);
    }

    public <T> T get(String key, Class<T> type) {
        Object value = scenarioData.get(key);
        if (value == null) {
            throw new IllegalStateException("No scenario data stored under key '" + key + "'");
        }
        return type.cast(value);
    }
}
