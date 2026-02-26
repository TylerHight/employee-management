package com.fscc.automation.core.reporting;

import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.*;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener, ConcurrentEventListener {

    @Override
    public void onStart(ITestContext context) {
        ReportManager.initReports();
    }

    @Override
    public void onFinish(ITestContext context) {
        ReportManager.flushReports();
    }

    // Default implementations for ITestListener
    @Override
    public void onTestStart(ITestResult result) {
    }

    @Override
    public void onTestSuccess(ITestResult result) {
    }

    @Override
    public void onTestFailure(ITestResult result) {
    }

    @Override
    public void onTestSkipped(ITestResult result) {
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    }

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        // Register for Cucumber events
        publisher.registerHandlerFor(TestCaseStarted.class, this::handleTestCaseStarted);
        publisher.registerHandlerFor(TestStepFinished.class, this::handleTestStepFinished);
        publisher.registerHandlerFor(TestCaseFinished.class, this::handleTestCaseFinished);
    }

    private void handleTestCaseStarted(TestCaseStarted event) {
        ReportManager.createTest(
                event.getTestCase().getName(),
                event.getTestCase().getUri().toString(),
                event.getTestCase().getTags());
    }

    private void handleTestStepFinished(TestStepFinished event) {
        if (event.getTestStep() instanceof PickleStepTestStep) {
            PickleStepTestStep step = (PickleStepTestStep) event.getTestStep();
            String stepText = step.getStep().getText();
            String status = event.getResult().getStatus().name();
            ReportManager.logStep(stepText, status);
        }
    }

    private void handleTestCaseFinished(TestCaseFinished event) {
        // Handle test case completion
    }
}
