package com.orangehrm.utilities;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentManager {

    private static ExtentReports extent;

    private static final ThreadLocal<ExtentTest> test =
            new ThreadLocal<>();

    private ExtentManager() {
        // Utility class - prevent object creation
    }

    /**
     * Initializes and returns the ExtentReports instance.
     */
    public synchronized static ExtentReports getReporter() {

        if (extent == null) {

            String reportPath =
                    System.getProperty("user.dir")
                    + "/src/test/resources/ExtentReport/ExtentReport.html";

            ExtentSparkReporter spark =
                    new ExtentSparkReporter(reportPath);

            spark.config().setReportName("OrangeHRM Automation Test Report");
            spark.config().setDocumentTitle("OrangeHRM Automation Report");
            spark.config().setTheme(Theme.DARK);

            extent = new ExtentReports();

            extent.attachReporter(spark);

            extent.setSystemInfo(
                    "Operating System",
                    System.getProperty("os.name")
            );

            extent.setSystemInfo(
                    "Java Version",
                    System.getProperty("java.version")
            );

            extent.setSystemInfo(
                    "User Name",
                    System.getProperty("user.name")
            );
        }

        return extent;
    }

    /**
     * Starts a new Extent test for the current thread.
     */
    public synchronized static ExtentTest startTest(String testName) {

        ExtentTest extentTest =
                getReporter().createTest(testName);

        test.set(extentTest);

        return extentTest;
    }

    /**
     * Returns the Extent test associated with the current thread.
     */
    public static ExtentTest getTest() {
        return test.get();
    }

    /**
     * Returns the current test name.
     */
    public static String getTestName() {

        ExtentTest currentTest = getTest();

        if (currentTest != null) {
            return currentTest.getModel().getName();
        }

        return "UnknownTest";
    }

    /**
     * Logs an informational step.
     */
    public static void logStep(String logMessage) {

        if (getTest() != null) {
            getTest().info(logMessage);
        }
    }

    /**
     * Logs a passed step and attaches a screenshot.
     */
    public static void logStepWithScreenshot(
            WebDriver driver,
            String logMessage,
            String screenShotMessage) {

        if (getTest() == null) {
            return;
        }

        getTest().pass(logMessage);

        if (driver != null) {
            attachScreenshot(driver, screenShotMessage);
        }
    }

    /**
     * Logs a passed API validation.
     */
    public static void logStepValidationForAPI(String logMessage) {

        if (getTest() != null) {
            getTest().pass(logMessage);
        }
    }

    /**
     * Logs a failed UI test and attaches a screenshot.
     */
    public static void logFailure(
            WebDriver driver,
            String logMessage,
            String screenShotMessage) {

        if (getTest() == null) {
            return;
        }

        getTest().fail(logMessage);

        if (driver != null) {
            attachScreenshot(driver, screenShotMessage);
        }
    }

    /**
     * Logs an API test failure.
     */
    public static void logFailureAPI(String logMessage) {

        if (getTest() != null) {
            getTest().fail(logMessage);
        }
    }

    /**
     * Logs a skipped test.
     */
    public static void logSkip(String logMessage) {

        if (getTest() != null) {
            getTest().skip(logMessage);
        }
    }

    /**
     * Takes a screenshot and saves it to the screenshots directory.
     */
    public synchronized static String takeScreenshot(
            WebDriver driver,
            String screenshotName) {

        if (driver == null) {
            return null;
        }

        TakesScreenshot ts =
                (TakesScreenshot) driver;

        File source =
                ts.getScreenshotAs(OutputType.FILE);

        String timeStamp =
                new SimpleDateFormat(
                        "yyyy-MM-dd_HH-mm-ss"
                ).format(new Date());

        String destinationPath =
                System.getProperty("user.dir")
                + "/src/test/resources/screenshots/"
                + screenshotName
                + "_"
                + timeStamp
                + ".png";

        File destination =
                new File(destinationPath);

        try {

            FileUtils.copyFile(source, destination);

        } catch (IOException e) {

            if (getTest() != null) {
                getTest().warning(
                        "Unable to save screenshot: "
                        + e.getMessage()
                );
            }
        }

        return convertToBase64(source);
    }

    /**
     * Converts screenshot file to Base64.
     */
    public static String convertToBase64(File screenshotFile) {

        if (screenshotFile == null) {
            return null;
        }

        try {

            byte[] fileContent =
                    FileUtils.readFileToByteArray(
                            screenshotFile
                    );

            return java.util.Base64
                    .getEncoder()
                    .encodeToString(fileContent);

        } catch (IOException e) {

            return null;
        }
    }

    /**
     * Attaches screenshot to Extent Report.
     */
    public synchronized static void attachScreenshot(
            WebDriver driver,
            String message) {

        if (driver == null || getTest() == null) {
            return;
        }

        try {

            String screenshotBase64 =
                    takeScreenshot(
                            driver,
                            getTestName()
                    );

            if (screenshotBase64 != null) {

                getTest().info(
                        message,
                        MediaEntityBuilder
                                .createScreenCaptureFromBase64String(
                                        screenshotBase64
                                )
                                .build()
                );
            }

        } catch (Exception e) {

            getTest().warning(
                    "Failed to attach screenshot: "
                    + e.getMessage()
            );
        }
    }

    /**
     * Flushes the Extent Report.
     */
    public synchronized static void endTest() {

        if (extent != null) {
            extent.flush();
        }

        test.remove();
    }
}