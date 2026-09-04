package com.orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.base.BaseClass;

public class DirectoryPage {

    private ActionDriver actionDriver;

    // ==================== Navigation ====================

    // Matches on the href fragment rather than visible span text -
    // OrangeHRM's sidebar can render duplicate nodes for the same menu
    // item (e.g. an expanded + a collapsed/responsive variant), and a
    // plain contains(text(),'Directory') match can non-deterministically
    // grab a hidden duplicate instead of the visible one, causing
    // intermittent timeouts. The href is unique and always present on
    // the real, clickable link regardless of which variant is visible.
    private By directoryTab =
            By.xpath("//a[contains(@href,'/directory/viewDirectory')]");


    // ==================== Directory Search ====================

    private By employeeNameInput =
            By.xpath("//input[@placeholder='Type for hints...']");

    private By searchButton =
            By.xpath("//button[normalize-space()='Search']");

    private By resetButton =
            By.xpath("//button[normalize-space()='Reset']");


    // ==================== Directory Results ====================

    /*
     * OrangeHRM Directory displays employees as cards.
     *
     * We verify the employee card directly instead of verifying
     * the "Records Found" text.
     */
    private By employeeCards =
            By.xpath("//div[contains(@class,'orangehrm-directory-card')]");


    // ==================== Constructor ====================

    public DirectoryPage(WebDriver driver) {
        this.actionDriver = BaseClass.getActionDriver();
    }


    // ==================== Navigation ====================

    public void clickDirectoryTab() {

        actionDriver.dismissAnyOpenDropdown();

        actionDriver.click(directoryTab);
    }


    // ==================== Employee Search ====================

    /*
     * Complete search flow:
     *
     * 1. Type partial employee name
     * 2. Select employee from autocomplete
     * 3. Click Search
     */
    public void searchEmployeeByName(
            String partialName,
            String fullNameToSelect) {

        // Step 1 - Type employee name
        actionDriver.enterText(
                employeeNameInput,
                partialName
        );


        // Step 2 - Select employee from autocomplete
        //
        // IMPORTANT:
        // OrangeHRM requires selecting the suggestion.
        // Simply typing "Vishal" leaves the field Invalid.
        //
        // This is the same autocomplete pattern already
        // used in your PIM/Recruitment pages.

        By suggestionOption = By.xpath(
                "//div[contains(" +
                "concat(' ',normalize-space(@class),' ')," +
                "' oxd-autocomplete-option ')" +
                "]" +
                "[contains(normalize-space(.),'" +
                fullNameToSelect +
                "')]"
        );

        actionDriver.click(suggestionOption);


        // Step 3 - Click Search
        actionDriver.click(searchButton);
    }


    // ==================== Verification ====================

    /*
     * Verify that the employee card contains the expected employee.
     */
    public boolean verifyEmployeeDisplayed(String employeeName) {

        By employeeCard = By.xpath(
                "//div[contains(@class,'orangehrm-directory-card')]" +
                "[contains(normalize-space(.),'" +
                employeeName +
                "')]"
        );

        return actionDriver.isDisplayed(employeeCard);
    }


    // ==================== Reset ====================

    public void resetSearch() {

        actionDriver.click(resetButton);
    }
}