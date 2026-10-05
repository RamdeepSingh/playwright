package com.uitap.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Locale;
import java.util.Properties;

/**
 * Resolves configuration values with the following precedence (highest first):
 * <ol>
 *   <li>JVM system property, e.g. {@code -Dbrowser=firefox}</li>
 *   <li>Environment variable, upper-cased with dots replaced by underscores, e.g. {@code BASE_URL}</li>
 *   <li>{@code config/<env>.properties} on the test classpath ({@code env} defaults to {@code qa})</li>
 * </ol>
 */
public final class ConfigReader {

    private static final String DEFAULT_ENV = "qa";
    private static final ConfigReader INSTANCE = new ConfigReader();

    private final String env;
    private final Properties fileProperties;

    private ConfigReader() {
        this.env = resolveEnv();
        this.fileProperties = loadProperties("config/" + env + ".properties");
    }

    public static ConfigReader get() {
        return INSTANCE;
    }

    public String env() {
        return env;
    }

    public String baseUrl() {
        return required("base.url").replaceAll("/+$", "");
    }

    public String browser() {
        return optional("browser", "chromium").toLowerCase(Locale.ROOT);
    }

    public boolean headless() {
        return Boolean.parseBoolean(optional("headless", "true"));
    }

    public double slowMoMillis() {
        return Double.parseDouble(optional("slow.mo.ms", "0"));
    }

    public double defaultTimeoutMillis() {
        return Double.parseDouble(optional("timeout.default.ms", "15000"));
    }

    public double navigationTimeoutMillis() {
        return Double.parseDouble(optional("timeout.navigation.ms", "30000"));
    }

    public int viewportWidth() {
        return Integer.parseInt(optional("viewport.width", "1366"));
    }

    public int viewportHeight() {
        return Integer.parseInt(optional("viewport.height", "768"));
    }

    public boolean traceOnFailure() {
        return Boolean.parseBoolean(optional("trace.on.failure", "true"));
    }

    public boolean videoEnabled() {
        return Boolean.parseBoolean(optional("video.enabled", "false"));
    }

    public String optional(String key, String defaultValue) {
        String value = lookup(key);
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }

    public String required(String key) {
        String value = lookup(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Missing required config '" + key + "' for env '" + env + "'");
        }
        return value.trim();
    }

    private String lookup(String key) {
        String sys = System.getProperty(key);
        if (sys != null) {
            return sys;
        }
        String envVar = System.getenv(toEnvVarName(key));
        if (envVar != null) {
            return envVar;
        }
        return fileProperties.getProperty(key);
    }

    private static String toEnvVarName(String key) {
        return key.toUpperCase(Locale.ROOT).replace('.', '_');
    }

    private static String resolveEnv() {
        String fromSys = System.getProperty("env");
        if (fromSys != null && !fromSys.isBlank()) {
            return fromSys.trim();
        }
        String fromEnv = System.getenv("TEST_ENV");
        return fromEnv != null && !fromEnv.isBlank() ? fromEnv.trim() : DEFAULT_ENV;
    }

    private static Properties loadProperties(String resource) {
        Properties props = new Properties();
        try (InputStream in = ConfigReader.class.getClassLoader().getResourceAsStream(resource)) {
            if (in == null) {
                throw new IllegalStateException("Config file not found on classpath: " + resource);
            }
            props.load(in);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load " + resource, e);
        }
        return props;
    }
}
