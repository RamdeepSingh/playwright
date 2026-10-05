package com.uitap.runners;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

/**
 * Entry point for Surefire. Plugins, parallelism and tag filters are configured in
 * {@code junit-platform.properties} and may be overridden with {@code -D} on the command line,
 * e.g. {@code -Dcucumber.filter.tags=@smoke}.
 */
@Suite
@IncludeEngines("cucumber")
@SelectPackages("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.uitap")
public class TestRunner {
}
