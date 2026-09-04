package com.orangehrm.base;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;
import org.testng.asserts.SoftAssert;

import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.utilities.LoggerManager;

public class BaseClass {

    protected static Properties prop;

    private static final ThreadLocal<WebDriver> driver =
            new ThreadLocal<>();

    private static final ThreadLocal<ActionDriver> actionDriver =
            new ThreadLocal<>();

    public static final Logger logger =
            LoggerManager.getLogger(BaseClass.class);

    private final ThreadLocal<SoftAssert> softAssert =
            ThreadLocal.withInitial(SoftAssert::new);

    private static final int DEFAULT_WINDOW_WIDTH = 1920;
    private static final int DEFAULT_WINDOW_HEIGHT = 1080;

    /**
     * Returns the SoftAssert instance for the current thread.
     */
    public SoftAssert getSoftAssert() {
        return softAssert.get();
    }

    /**
     * Loads framework configuration before the test suite starts.
     */
    @BeforeSuite
    public void loadConfig() throws IOException {

        prop = new Properties();

        String configPath =
                System.getProperty("user.dir")
                + "/src/main/resources/config.properties";

        try (FileInputStream fis =
                     new FileInputStream(configPath)) {

            prop.load(fis);
        }

        logger.info("config.properties loaded successfully.");
    }

    /**
     * Initializes browser and ActionDriver before every test method.
     */
    @BeforeMethod
    @Parameters("browser")
    public void setup(String browser) throws IOException {

        logger.info(
                "Setting up WebDriver for: {}",
                this.getClass().getSimpleName()
        );

        launchBrowser(browser);

        configureBrowser();

        actionDriver.set(
                new ActionDriver(getDriver())
        );

        logger.info(
                "ActionDriver initialized for thread: {}",
                Thread.currentThread().getId()
        );
    }

    /**
     * Creates either a local WebDriver or RemoteWebDriver
     * depending on the Selenium Grid configuration.
     */
    private void launchBrowser(String browser) {

        if (browser == null || browser.isBlank()) {

            throw new IllegalArgumentException(
                    "Browser parameter cannot be null or empty."
            );
        }

        boolean seleniumGrid =
                Boolean.parseBoolean(
                        prop.getProperty(
                                "seleniumGrid",
                                "false"
                        )
                );

        if (seleniumGrid) {

            launchRemoteBrowser(browser);

        } else {

            launchLocalBrowser(browser);
        }
    }

    /**
     * Launches browser through Selenium Grid.
     */
    private void launchRemoteBrowser(String browser) {

        String gridURL = prop.getProperty("gridURL");

        if (gridURL == null || gridURL.isBlank()) {

            throw new IllegalArgumentException(
                    "gridURL is not configured in config.properties."
            );
        }

        try {

            switch (browser.toLowerCase()) {

                case "chrome":

                    ChromeOptions chromeOptions =
                            new ChromeOptions();

                    chromeOptions.addArguments(
                            "--headless=new"
                    );

                    driver.set(
                            new RemoteWebDriver(
                                    new URL(gridURL),
                                    chromeOptions
                            )
                    );

                    break;

                case "firefox":

                    FirefoxOptions firefoxOptions =
                            new FirefoxOptions();

                    firefoxOptions.addArguments(
                            "-headless"
                    );

                    driver.set(
                            new RemoteWebDriver(
                                    new URL(gridURL),
                                    firefoxOptions
                            )
                    );

                    break;

                case "edge":

                    EdgeOptions edgeOptions =
                            new EdgeOptions();

                    edgeOptions.addArguments(
                            "--headless=new",
                            "--disable-gpu",
                            "--no-sandbox",
                            "--disable-dev-shm-usage",
                            "--window-size=1920,1080"
                    );

                    driver.set(
                            new RemoteWebDriver(
                                    new URL(gridURL),
                                    edgeOptions
                            )
                    );

                    break;

                default:

                    throw new IllegalArgumentException(
                            "Browser not supported: " + browser
                    );
            }

            logger.info(
                    "RemoteWebDriver created successfully for {}.",
                    browser
            );

        } catch (MalformedURLException e) {

            throw new RuntimeException(
                    "Invalid Selenium Grid URL: " + gridURL,
                    e
            );
        }
    }

    /**
     * Launches browser locally.
     */
    private void launchLocalBrowser(String browser) {

        switch (browser.toLowerCase()) {

            case "chrome":

                ChromeOptions chromeOptions =
                        new ChromeOptions();

                // HEADLESS MODE - temporarily disabled to diagnose
                // inconsistent failures at different steps each run
                // (login field, then Directory tab, etc.) that a manual
                // browser session doesn't reproduce. Watching it run
                // visibly will show whether this is a real rendering/
                // timing issue specific to headless mode, or something
                // else entirely (e.g. a popup/overlay headless can't
                // show us). Re-enable once confirmed.
                chromeOptions.addArguments(
                        "--headless=new",
                        "--disable-gpu",
                        "--window-size=1920,1080",
                        "--disable-notifications",
                        "--no-sandbox",
                        "--disable-dev-shm-usage"
                );

                driver.set(
                        new ChromeDriver(chromeOptions)
                );

                logger.info(
                        "ChromeDriver initialized in VISIBLE mode (headless temporarily disabled for diagnosis)."
                );

                break;

            case "firefox":

                FirefoxOptions firefoxOptions =
                        new FirefoxOptions();

                firefoxOptions.addArguments(
                        "--headless",
                        "--width=1920",
                        "--height=1080",
                        "--disable-notifications",
                        "--no-sandbox",
                        "--disable-dev-shm-usage"
                );

                driver.set(
                        new FirefoxDriver(firefoxOptions)
                );

                logger.info(
                        "FirefoxDriver initialized."
                );

                break;

            case "edge":

                EdgeOptions edgeOptions =
                        new EdgeOptions();

                edgeOptions.addArguments(
                        "--headless",
                        "--disable-gpu",
                        "--window-size=1920,1080",
                        "--disable-notifications",
                        "--no-sandbox",
                        "--disable-dev-shm-usage"
                );

                driver.set(
                        new EdgeDriver(edgeOptions)
                );

                logger.info(
                        "EdgeDriver initialized."
                );

                break;

            default:

                throw new IllegalArgumentException(
                        "Browser not supported: " + browser
                );
        }
    }

    /**
     * Configures browser timeout, window size and application URL.
     */
    private void configureBrowser() {

        boolean seleniumGrid =
                Boolean.parseBoolean(
                        System.getProperty(
                                "seleniumGrid",
                                prop.getProperty(
                                        "seleniumGrid",
                                        "false"
                                )
                        )
                );

        // Explicit waits are handled by ActionDriver.
        // Therefore implicit wait remains disabled.
        getDriver()
                .manage()
                .timeouts()
                .implicitlyWait(Duration.ZERO);

        getDriver()
                .manage()
                .window()
                .setSize(
                        new Dimension(
                                DEFAULT_WINDOW_WIDTH,
                                DEFAULT_WINDOW_HEIGHT
                        )
                );

        String applicationURL;

        if (seleniumGrid) {

            applicationURL =
                    prop.getProperty("url_grid");

        } else {

            applicationURL =
                    prop.getProperty("url_local");
        }

        if (applicationURL == null ||
                applicationURL.isBlank()) {

            throw new IllegalArgumentException(
                    "Application URL is not configured."
            );
        }

        logger.info(
                "Navigating to application URL: {}",
                applicationURL
        );

        getDriver().get(applicationURL);
    }

    /**
     * Closes browser and cleans thread-local resources
     * after every test method.
     */
    @AfterMethod
    public void tearDown() {

        WebDriver currentDriver = driver.get();

        if (currentDriver != null) {

            try {

                currentDriver.quit();

                logger.info(
                        "WebDriver closed successfully for thread: {}",
                        Thread.currentThread().getId()
                );

            } catch (Exception e) {

                logger.error(
                        "Unable to close WebDriver.",
                        e
                );
            }
        }

        driver.remove();
        actionDriver.remove();
        softAssert.remove();
    }

    /**
     * Returns WebDriver for the current thread.
     */
    public static WebDriver getDriver() {

        WebDriver currentDriver = driver.get();

        if (currentDriver == null) {

            throw new IllegalStateException(
                    "WebDriver is not initialized for the current thread."
            );
        }

        return currentDriver;
    }

    /**
     * Returns ActionDriver for the current thread.
     */
    public static ActionDriver getActionDriver() {

        ActionDriver currentActionDriver =
                actionDriver.get();

        if (currentActionDriver == null) {

            throw new IllegalStateException(
                    "ActionDriver is not initialized for the current thread."
            );
        }

        return currentActionDriver;
    }

    /**
     * Returns loaded framework properties.
     */
    public static Properties getProp() {

        if (prop == null) {

            throw new IllegalStateException(
                    "Configuration has not been loaded yet."
            );
        }

        return prop;
    }

    /**
     * Static wait utility.
     * Prefer explicit waits through ActionDriver for UI synchronization.
     */
    public static void staticWait(int seconds) {

        LockSupport.parkNanos(
                TimeUnit.SECONDS.toNanos(seconds)
        );
    }
}