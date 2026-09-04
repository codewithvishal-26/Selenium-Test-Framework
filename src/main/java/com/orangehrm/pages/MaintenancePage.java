package com.orangehrm.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.base.BaseClass;

public class MaintenancePage {

    private ActionDriver actionDriver;
    private WebDriver driver;
    private WebDriverWait wait;


    // =========================================================
    // Maintenance Navigation
    // =========================================================

    private By maintenanceTab =
            By.xpath("//span[normalize-space()='Maintenance']");


    // =========================================================
    // Administrator Access
    // =========================================================

    private By passwordField =
            By.name("password");

    private By confirmButton =
            By.xpath("//button[normalize-space()='Confirm']");


    // =========================================================
    // Maintenance Page
    // =========================================================

    private By purgeEmployeeRecordsHeading =
            By.xpath("//*[contains(normalize-space(.),'Purge Employee Records')]");


    // =========================================================
    // Constructor
    // =========================================================

    public MaintenancePage(WebDriver driver) {

        this.driver = driver;

        this.actionDriver = BaseClass.getActionDriver();

        this.wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(15)
        );
    }


    // =========================================================
    // Navigate to Maintenance
    // =========================================================

    public void clickMaintenanceTab() {

        actionDriver.dismissAnyOpenDropdown();

        wait.until(
                ExpectedConditions.elementToBeClickable(maintenanceTab)
        ).click();
    }


    // =========================================================
    // Administrator Access
    // =========================================================

    public void enterAdministratorPassword(String password) {

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(passwordField)
        ).sendKeys(password);


        wait.until(
                ExpectedConditions.elementToBeClickable(confirmButton)
        ).click();
    }


    // =========================================================
    // Wait for Maintenance Page
    // =========================================================

    public void waitForMaintenancePage() {

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        purgeEmployeeRecordsHeading
                )
        );
    }


    // =========================================================
    // Verify Maintenance Page
    // =========================================================

    public boolean isMaintenancePageDisplayed() {

        try {

            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            purgeEmployeeRecordsHeading
                    )
            ).isDisplayed();

        } catch (Exception e) {

            return false;
        }
    }

}