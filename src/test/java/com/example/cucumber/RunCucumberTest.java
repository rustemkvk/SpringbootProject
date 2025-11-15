package com.example.cucumber;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.core.options.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.core.options.Constants.PLUGIN_PROPERTY_NAME;


@Suite
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME,
        value = "com.example.cucumber.config, com.example.cucumber.stepdef")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME,
        value = "pretty, json:target/cucumber-report/cucumber.json, html:target/cucumber-report/index.html")
public class RunCucumberTest {
}