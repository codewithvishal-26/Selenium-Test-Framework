package com.orangehrm.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.base.BaseClass;

public class DashboardPage {

    private ActionDriver actionDriver;
    private WebDriver driver;
    private WebDriverWait wait;


    // =========================================================
    // Dashboard
    // =========================================================

    private By dashboardHeading =
            By.xpath("//*[contains(normalize-space(.),'Dashboard')]");


    // =========================================================
    // Attendance Card
    // =========================================================

    private By attendanceCardAction =
            By.cssSelector("button.orangehrm-attendance-card-action");


    // =========================================================
    // Quick Launch
    // =========================================================

    private By quickLaunchButtons =
            By.cssSelector("button.orangehrm-quick-launch-icon");


    // =========================================================
    // Constructor
    // =========================================================

    public DashboardPage(WebDriver driver) {

        this.driver = driver;

        this.actionDriver = BaseClass.getActionDriver();

        this.wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(15)
        );
    }


    // =========================================================
    // Verify Dashboard
    // =========================================================

    public boolean isDashboardDisplayed() {

        try {

            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            dashboardHeading
                    )
            ).isDisplayed();

        } catch (Exception e) {

            return false;
        }
    }


    // =========================================================
    // Verify Attendance Card
    // =========================================================

    public boolean isAttendanceCardDisplayed() {

        try {

            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            attendanceCardAction
                    )
            ).isDisplayed();

        } catch (Exception e) {

            return false;
        }
    }


    // =========================================================
    // Verify Quick Launch
    // =========================================================

    public boolean isQuickLaunchDisplayed() {

        try {

            return wait.until(
                    ExpectedConditions.visibilityOfAllElementsLocatedBy(
                            quickLaunchButtons
                    )
            ).size() > 0;

        } catch (Exception e) {

            return false;
        }
    }
}