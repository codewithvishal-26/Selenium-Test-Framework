package com.orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.base.BaseClass;

public class ClaimPage {

    private ActionDriver actionDriver;

    // =========================================================
    // Navigation
    // =========================================================

    private By claimTab =
            By.xpath("//span[normalize-space()='Claim']");


    // =========================================================
    // Employee Claims
    // =========================================================

    private By employeeNameInput =
            By.xpath("//input[@placeholder='Type for hints...']");

    private By searchButton =
            By.xpath("//button[contains(normalize-space(.),'Search')]");


    // =========================================================
    // Verification
    // =========================================================

    // NOT independently re-verified against live DOM - if this fails,
    // check the real heading tag/text via browser console first rather
    // than assuming the tag or exact wording.
    private By employeeClaimsHeading =
            By.xpath("//*[contains(normalize-space(text()),'Employee Claims')]");


    // =========================================================
    // Constructor
    // =========================================================

    public ClaimPage(WebDriver driver) {

        this.actionDriver = BaseClass.getActionDriver();
    }


    // =========================================================
    // Navigate to Claim
    // =========================================================

    public void clickClaimTab() {

        actionDriver.dismissAnyOpenDropdown();

        actionDriver.click(claimTab);
    }


    // =========================================================
    // Search Employee
    // =========================================================

    public void searchEmployee(String employeeName) {

        // Enter employee name
        actionDriver.enterText(
                employeeNameInput,
                employeeName
        );

        /*
         * OrangeHRM uses autocomplete here.
         *
         * After typing the employee name, select the
         * matching suggestion from the dropdown.
         */
        By employeeSuggestion = By.xpath(
                "//div[contains(@class,'oxd-autocomplete-option')]" +
                "[contains(normalize-space(.),'" +
                employeeName +
                "')]"
        );

        actionDriver.click(employeeSuggestion);

        // Click Search
        actionDriver.click(searchButton);
    }


    // =========================================================
    // Verify Employee Claims Page
    // =========================================================

    public boolean isEmployeeClaimsPageDisplayed() {

        return actionDriver.isDisplayed(
                employeeClaimsHeading
        );
    }
}