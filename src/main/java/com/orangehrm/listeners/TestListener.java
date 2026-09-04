package com.orangehrm.listeners;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.testng.IAnnotationTransformer;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.annotations.ITestAnnotation;

import com.orangehrm.base.BaseClass;
import com.orangehrm.utilities.ExtentManager;
import com.orangehrm.utilities.RetryAnalyzer;

public class TestListener implements ITestListener, IAnnotationTransformer {

    @Override
    public void transform(
            ITestAnnotation annotation,
            Class testClass,
            Constructor testConstructor,
            Method testMethod) {

        annotation.setRetryAnalyzer(RetryAnalyzer.class);
    }

    @Override
    public void onTestStart(ITestResult result) {

        String testName = result.getMethod().getMethodName();

        ExtentManager.startTest(testName);
        ExtentManager.logStep("Test Started: " + testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {

        String testName = result.getMethod().getMethodName();

        if (isApiTest(result)) {

            ExtentManager.logStepValidationForAPI(
                    "Test End: " + testName + " - ✔ Test Passed"
            );

        } else {

            ExtentManager.logStepWithScreenshot(
                    BaseClass.getDriver(),
                    "Test Passed Successfully!",
                    "Test End: " + testName + " - ✔ Test Passed"
            );
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {

        String testName = result.getMethod().getMethodName();

        String failureMessage = result.getThrowable() != null
                ? result.getThrowable().getMessage()
                : "Unknown test failure";

        ExtentManager.logStep(
                "Failure Reason: " + failureMessage
        );

        if (isApiTest(result)) {

            ExtentManager.logFailureAPI(
                    "Test End: " + testName + " - ❌ Test Failed"
            );

        } else {

            if (BaseClass.getDriver() != null) {

                ExtentManager.logFailure(
                        BaseClass.getDriver(),
                        "Test Failed!",
                        "Test End: " + testName + " - ❌ Test Failed"
                );

            } else {

                ExtentManager.logStep(
                        "WebDriver is not available for failure screenshot."
                );
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {

        String testName = result.getMethod().getMethodName();

        ExtentManager.logSkip(
                "Test Skipped: " + testName
        );
    }

    @Override
    public void onStart(ITestContext context) {

        ExtentManager.getReporter();
    }

    @Override
    public void onFinish(ITestContext context) {

        ExtentManager.endTest();
    }

    /**
     * Determines whether the current test belongs to an API test class.
     */
    private boolean isApiTest(ITestResult result) {

        return result.getTestClass()
                .getName()
                .toLowerCase()
                .contains("api");
    }
}