package com.orangehrm.actiondriver;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.orangehrm.base.BaseClass;
import com.orangehrm.utilities.ExtentManager;

public class ActionDriver {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public static final Logger logger = BaseClass.logger;

    public ActionDriver(WebDriver driver) {

        if (driver == null) {
            throw new IllegalArgumentException(
                    "WebDriver cannot be null."
            );
        }

        this.driver = driver;

        int explicitWait = Integer.parseInt(
                BaseClass.getProp()
                        .getProperty("explicitWait", "10")
        );

        this.wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(explicitWait)
        );

        logger.info(
                "ActionDriver initialized with explicit wait: {} seconds",
                explicitWait
        );
    }

    // ============================================================
    // ELEMENT HANDLING
    // ============================================================

    private WebElement findVisibleElement(By by) {

        List<WebElement> elements =
                driver.findElements(by);

        for (WebElement element : elements) {

            try {

                if (element.isDisplayed()) {
                    return element;
                }

            } catch (Exception ignored) {
                // Ignore stale/unreadable elements
            }
        }

        return driver.findElement(by);
    }

    public String getElementDescription(By locator) {

        if (locator == null) {
            return "Locator is null.";
        }

        try {

            WebElement element =
                    findVisibleElement(locator);

            String name =
                    element.getDomProperty("name");

            String id =
                    element.getDomProperty("id");

            String text =
                    element.getText();

            String className =
                    element.getDomProperty("class");

            String placeholder =
                    element.getDomProperty("placeholder");

            if (isNotEmpty(name)) {
                return "Element with name: " + name;
            }

            if (isNotEmpty(id)) {
                return "Element with ID: " + id;
            }

            if (isNotEmpty(text)) {
                return "Element with text: "
                        + truncate(text, 50);
            }

            if (isNotEmpty(placeholder)) {
                return "Element with placeholder: "
                        + placeholder;
            }

            if (isNotEmpty(className)) {
                return "Element with class: "
                        + className;
            }

            return "Element located using: "
                    + locator;

        } catch (Exception e) {

            return "Unable to describe element: "
                    + locator;
        }
    }

    private boolean isNotEmpty(String value) {

        return value != null
                && !value.isBlank();
    }

    private String truncate(
            String value,
            int maxLength) {

        if (value == null ||
                value.length() <= maxLength) {

            return value;
        }

        return value.substring(0, maxLength)
                + "...";
    }

    // ============================================================
    // CLICK
    // ============================================================

    public void click(By by) {

        String description =
                getElementDescription(by);

        try {

            waitForElementToBeClickable(by);

            WebElement element =
                    findVisibleElement(by);

            applyBorder(by, "green");

            element.click();

            ExtentManager.logStep(
                    "Clicked element: "
                            + description
            );

            logger.info(
                    "Clicked element --> {}",
                    description
            );

        } catch (Exception e) {

            applyBorder(by, "red");

            logger.error(
                    "Unable to click element: {}",
                    description,
                    e
            );

            ExtentManager.logFailure(
                    BaseClass.getDriver(),
                    "Unable to click element",
                    description + "_click_failed"
            );

            throw new RuntimeException(
                    "Unable to click element: "
                            + description,
                    e
            );
        }
    }

    // ============================================================
    // TEXT HANDLING
    // ============================================================

    public void enterText(
            By by,
            String value) {

        String description =
                getElementDescription(by);

        try {

            waitForElementToBeVisible(by);

            WebElement element =
                    findVisibleElement(by);

            applyBorder(by, "green");

            element.click();

            element.sendKeys(
                    Keys.chord(
                            Keys.CONTROL,
                            "a"
                    )
            );

            element.sendKeys(Keys.DELETE);

            if (value != null) {
                element.sendKeys(value);
            }

            ExtentManager.logStep(
                    "Entered text into "
                            + description
            );

            logger.info(
                    "Entered text on {}",
                    description
            );

        } catch (Exception e) {

            applyBorder(by, "red");

            logger.error(
                    "Unable to enter text on {}",
                    description,
                    e
            );

            throw new RuntimeException(
                    "Unable to enter text on element: "
                            + description,
                    e
            );
        }
    }

    public String getText(By by) {

        try {

            waitForElementToBeVisible(by);

            applyBorder(by, "green");

            return findVisibleElement(by)
                    .getText();

        } catch (Exception e) {

            applyBorder(by, "red");

            logger.error(
                    "Unable to get text",
                    e
            );

            throw new RuntimeException(
                    "Unable to get text from element: "
                            + getElementDescription(by),
                    e
            );
        }
    }

    public String getInputValue(By by) {

        try {

            waitForElementToBeVisible(by);

            applyBorder(by, "green");

            return findVisibleElement(by)
                    .getAttribute("value");

        } catch (Exception e) {

            applyBorder(by, "red");

            logger.error(
                    "Unable to get input value",
                    e
            );

            throw new RuntimeException(
                    "Unable to get input value from element: "
                            + getElementDescription(by),
                    e
            );
        }
    }

    public void clearText(By by) {

        try {

            waitForElementToBeVisible(by);

            findVisibleElement(by).clear();

            logger.info(
                    "Cleared text from: {}",
                    getElementDescription(by)
            );

        } catch (Exception e) {

            logger.error(
                    "Unable to clear text",
                    e
            );

            throw new RuntimeException(
                    "Unable to clear text",
                    e
            );
        }
    }

    // ============================================================
    // VALIDATION
    // ============================================================

    public boolean compareText(
            By by,
            String expectedText) {

        try {

            String actualText =
                    getText(by);

            boolean matched =
                    expectedText.equals(actualText);

            if (matched) {

                applyBorder(by, "green");

                logger.info(
                        "Text matched. Expected: {}, Actual: {}",
                        expectedText,
                        actualText
                );

                ExtentManager.logStepWithScreenshot(
                        BaseClass.getDriver(),
                        "Compare Text",
                        "Text Verified Successfully: "
                                + actualText
                );

            } else {

                applyBorder(by, "red");

                logger.error(
                        "Text mismatch. Expected: {}, Actual: {}",
                        expectedText,
                        actualText
                );

                ExtentManager.logFailure(
                        BaseClass.getDriver(),
                        "Text Comparison Failed",
                        "Expected: "
                                + expectedText
                                + " | Actual: "
                                + actualText
                );
            }

            return matched;

        } catch (Exception e) {

            logger.error(
                    "Unable to compare text",
                    e
            );

            return false;
        }
    }

    public boolean isDisplayed(By by) {

        try {

            waitForElementToBeVisible(by);

            boolean displayed =
                    findVisibleElement(by)
                            .isDisplayed();

            applyBorder(
                    by,
                    displayed ? "green" : "red"
            );

            logger.info(
                    "Element displayed status: {}",
                    displayed
            );

            return displayed;

        } catch (Exception e) {

            applyBorder(by, "red");

            logger.error(
                    "Element is not displayed",
                    e
            );

            return false;
        }
    }

    // ============================================================
    // WAITS
    // ============================================================

    private void waitForElementToBeClickable(By by) {

        wait.until(
                ExpectedConditions
                        .elementToBeClickable(by)
        );
    }

    private void waitForElementToBeVisible(By by) {

        wait.until(
                ExpectedConditions
                        .visibilityOfElementLocated(by)
        );
    }

    public void waitUntilVisible(By by) {

        waitForElementToBeVisible(by);
    }

    public void waitUntilAnyVisible(By by) {

        wait.until(
                driver -> driver
                        .findElements(by)
                        .stream()
                        .anyMatch(
                                WebElement::isDisplayed
                        )
        );
    }

    public void waitForInvisibility(By by) {

        wait.until(
                ExpectedConditions
                        .invisibilityOfElementLocated(by)
        );
    }

    public void waitUntilUrlContains(
            String fragment) {

        wait.until(
                ExpectedConditions
                        .urlContains(fragment)
        );
    }

    public void waitForTextToChange(
            By by,
            String oldText) {

        wait.until(
                driver -> !driver
                        .findElement(by)
                        .getText()
                        .equals(oldText)
        );
    }

    public void waitForPageLoad(
            int timeOutInSec) {

        new WebDriverWait(
                driver,
                Duration.ofSeconds(timeOutInSec)
        ).until(
                webDriver ->
                        ((JavascriptExecutor) webDriver)
                                .executeScript(
                                        "return document.readyState"
                                )
                                .equals("complete")
        );

        logger.info(
                "Page loaded successfully."
        );
    }

    // ============================================================
    // DROPDOWN
    // ============================================================

    public void selectByVisibleText(
            By by,
            String value) {

        try {

            waitForElementToBeVisible(by);

            Select select =
                    new Select(
                            findVisibleElement(by)
                    );

            select.selectByVisibleText(value);

            applyBorder(by, "green");

            logger.info(
                    "Selected dropdown value: {}",
                    value
            );

        } catch (Exception e) {

            applyBorder(by, "red");

            logger.error(
                    "Unable to select dropdown value: "
                            + value,
                    e
            );

            throw new RuntimeException(
                    "Unable to select dropdown value: "
                            + value,
                    e
            );
        }
    }

    public void selectByValue(
            By by,
            String value) {

        try {

            waitForElementToBeVisible(by);

            new Select(
                    findVisibleElement(by)
            ).selectByValue(value);

            applyBorder(by, "green");

        } catch (Exception e) {

            applyBorder(by, "red");

            throw new RuntimeException(
                    "Unable to select dropdown value: "
                            + value,
                    e
            );
        }
    }

    public void selectByIndex(
            By by,
            int index) {

        try {

            waitForElementToBeVisible(by);

            new Select(
                    findVisibleElement(by)
            ).selectByIndex(index);

            applyBorder(by, "green");

        } catch (Exception e) {

            applyBorder(by, "red");

            throw new RuntimeException(
                    "Unable to select dropdown index: "
                            + index,
                    e
            );
        }
    }

    public List<String> getDropdownOptions(By by) {

        List<String> options =
                new ArrayList<>();

        try {

            waitForElementToBeVisible(by);

            Select select =
                    new Select(
                            findVisibleElement(by)
                    );

            for (WebElement option :
                    select.getOptions()) {

                options.add(
                        option.getText()
                );
            }

            return options;

        } catch (Exception e) {

            logger.error(
                    "Unable to retrieve dropdown options",
                    e
            );

            throw new RuntimeException(
                    "Unable to retrieve dropdown options",
                    e
            );
        }
    }

    // ============================================================
    // JAVASCRIPT
    // ============================================================

    public void clickUsingJS(By by) {

        try {

            WebElement element =
                    findVisibleElement(by);

            ((JavascriptExecutor) driver)
                    .executeScript(
                            "arguments[0].click();",
                            element
                    );

            applyBorder(by, "green");

            logger.info(
                    "Clicked element using JavaScript: {}",
                    getElementDescription(by)
            );

        } catch (Exception e) {

            applyBorder(by, "red");

            throw new RuntimeException(
                    "Unable to click using JavaScript",
                    e
            );
        }
    }

    public void scrollToElement(By by) {

        try {

            // scrollToElement previously called findVisibleElement()
            // directly, which has NO built-in wait - it checks once and
            // fails immediately. That's the real root cause behind every
            // "Unable to scroll to element" failure seen across the
            // framework (PIM search results, Leave Type dropdown, etc.):
            // async-rendered content (autocomplete listboxes, filtered
            // tables) just hadn't finished rendering yet when we looked.
            // Using the same explicit WebDriverWait already relied on
            // elsewhere (waitForElementToBeVisible) fixes this at the
            // source instead of needing a wait added at every call site.
            waitForElementToBeVisible(by);

            WebElement element =
                    findVisibleElement(by);

            ((JavascriptExecutor) driver)
                    .executeScript(
                            "arguments[0].scrollIntoView({block:'center'});",
                            element
                    );

            logger.info(
                    "Scrolled to element: {}",
                    getElementDescription(by)
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to scroll to element",
                    e
            );
        }
    }

    public void scrollToBottom() {

        ((JavascriptExecutor) driver)
                .executeScript(
                        "window.scrollTo(0, document.body.scrollHeight);"
                );

        logger.info(
                "Scrolled to bottom of page."
        );
    }

    public void highlightElementJS(By by) {

        try {

            WebElement element =
                    findVisibleElement(by);

            ((JavascriptExecutor) driver)
                    .executeScript(
                            "arguments[0].style.border='3px solid yellow';",
                            element
                    );

        } catch (Exception e) {

            logger.error(
                    "Unable to highlight element",
                    e
            );
        }
    }

    // ============================================================
    // WINDOW / FRAME
    // ============================================================

    public void switchToWindow(
            String windowTitle) {

        Set<String> windows =
                driver.getWindowHandles();

        for (String window : windows) {

            driver.switchTo()
                    .window(window);

            if (driver.getTitle()
                    .equals(windowTitle)) {

                logger.info(
                        "Switched to window: {}",
                        windowTitle
                );

                return;
            }
        }

        throw new RuntimeException(
                "Window not found: "
                        + windowTitle
        );
    }

    public void switchToFrame(By by) {

        waitForElementToBeVisible(by);

        driver.switchTo()
                .frame(findVisibleElement(by));

        logger.info(
                "Switched to iframe: {}",
                getElementDescription(by)
        );
    }

    public void switchToDefaultContent() {

        driver.switchTo()
                .defaultContent();

        logger.info(
                "Switched to default content."
        );
    }

    // ============================================================
    // ALERTS
    // ============================================================

    public void acceptAlert() {

        wait.until(
                ExpectedConditions
                        .alertIsPresent()
        ).accept();

        logger.info(
                "Alert accepted."
        );
    }

    public void dismissAlert() {

        wait.until(
                ExpectedConditions
                        .alertIsPresent()
        ).dismiss();

        logger.info(
                "Alert dismissed."
        );
    }

    public String getAlertText() {

        return wait.until(
                ExpectedConditions
                        .alertIsPresent()
        ).getText();
    }

    // ============================================================
    // BROWSER ACTIONS
    // ============================================================

    public void refreshPage() {

        driver.navigate().refresh();

        logger.info(
                "Page refreshed successfully."
        );
    }

    public String getCurrentURL() {

        return driver.getCurrentUrl();
    }

    public void maximizeWindow() {

        driver.manage()
                .window()
                .maximize();

        logger.info(
                "Browser window maximized."
        );
    }

    public String getCurrentUrl() {

        return driver.getCurrentUrl();
    }

    // ============================================================
    // ADVANCED ACTIONS
    // ============================================================

    public void moveToElement(By by) {

        Actions actions =
                new Actions(driver);

        actions.moveToElement(
                findVisibleElement(by)
        ).perform();

        logger.info(
                "Moved to element: {}",
                getElementDescription(by)
        );
    }

    public void dragAndDrop(
            By source,
            By target) {

        Actions actions =
                new Actions(driver);

        actions.dragAndDrop(
                findVisibleElement(source),
                findVisibleElement(target)
        ).perform();

        logger.info(
                "Dragged {} to {}",
                getElementDescription(source),
                getElementDescription(target)
        );
    }

    public void doubleClick(By by) {

        Actions actions =
                new Actions(driver);

        actions.doubleClick(
                findVisibleElement(by)
        ).perform();

        logger.info(
                "Double-clicked: {}",
                getElementDescription(by)
        );
    }

    public void rightClick(By by) {

        Actions actions =
                new Actions(driver);

        actions.contextClick(
                findVisibleElement(by)
        ).perform();

        logger.info(
                "Right-clicked: {}",
                getElementDescription(by)
        );
    }

    public void sendKeysWithActions(
            By by,
            String value) {

        Actions actions =
                new Actions(driver);

        actions.sendKeys(
                findVisibleElement(by),
                value
        ).perform();

        logger.info(
                "Sent keys to: {}",
                getElementDescription(by)
        );
    }

    // ============================================================
    // OPTIONAL ELEMENTS
    // ============================================================

    public void clickIfPresent(
            By by,
            int timeoutSeconds) {

        try {

            WebDriverWait shortWait =
                    new WebDriverWait(
                            driver,
                            Duration.ofSeconds(
                                    timeoutSeconds
                            )
                    );

            shortWait.until(
                    ExpectedConditions
                            .elementToBeClickable(by)
            ).click();

            logger.info(
                    "Optional element clicked: {}",
                    getElementDescription(by)
            );

        } catch (Exception e) {

            logger.info(
                    "Optional element not present: {}",
                    by
            );
        }
    }

    public void dismissAnyOpenDropdown() {

        try {

            new Actions(driver)
                    .sendKeys(Keys.ESCAPE)
                    .perform();

        } catch (Exception e) {

            logger.info(
                    "Unable to dismiss dropdown.",
                    e
            );
        }
    }

    // ============================================================
    // FILE UPLOAD
    // ============================================================

    public void uploadFile(
            By by,
            String filePath) {

        try {

            waitForElementToBeVisible(by);

            findVisibleElement(by)
                    .sendKeys(filePath);

            applyBorder(by, "green");

            logger.info(
                    "File uploaded: {}",
                    filePath
            );

        } catch (Exception e) {

            applyBorder(by, "red");

            throw new RuntimeException(
                    "Unable to upload file: "
                            + filePath,
                    e
            );
        }
    }

    // ============================================================
    // BORDER / DEBUGGING
    // ============================================================

    public void applyBorder(
            By by,
            String color) {

        try {

            WebElement element =
                    findVisibleElement(by);

            String script =
                    "arguments[0].style.border='3px solid "
                            + color
                            + "';";

            ((JavascriptExecutor) driver)
                    .executeScript(
                            script,
                            element
                    );

        } catch (Exception e) {

            logger.warn(
                    "Unable to apply border to: {}",
                    by
            );
        }
    }
}