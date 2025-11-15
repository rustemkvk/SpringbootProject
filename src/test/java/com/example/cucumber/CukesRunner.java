package com.example.cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {
                "pretty",
                "json:target/cucumber-report.json",
                "html:target/cucumber.html"},
        features = "src/test/resources/features",
        glue = {"com.example.cucumber.config", "com.example.cucumber.stepdef"},
        publish = true
)

public class CukesRunner {
}
