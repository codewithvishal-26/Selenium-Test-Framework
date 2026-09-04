
package com.orangehrm.test;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.DirectoryPage;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.utilities.ExtentManager;

public class DirectoryTest extends BaseClass {

    private LoginPage loginPage;
    private HomePage homePage;
    private DirectoryPage directoryPage;


    // ==================== Page Setup ====================

    @BeforeMethod
    public void setupPages() {

        loginPage = new LoginPage(getDriver());

        homePage = new HomePage(getDriver());

        directoryPage = new DirectoryPage(getDriver());
    }


    // ==================== Login ====================

    private void loginAsAdmin() {

        String adminUsername =
                getProp().getProperty("username");

        String adminPassword =
                getProp().getProperty("password");


        ExtentManager.logStep(
                "Logging into OrangeHRM"
        );

        loginPage.login(
                adminUsername,
                adminPassword
        );


        Assert.assertTrue(
                homePage.isAdminTabVisible(),
                "Login failed - Admin tab is not visible"
        );
    }


    // ==================== Directory Search Test ====================

    @Test
    public void searchEmployeeInDirectoryTest() {

        // -----------------------------------------
        // STEP 1: Login
        // -----------------------------------------

        loginAsAdmin();


        // -----------------------------------------
        // STEP 2: Navigate to Directory
        // -----------------------------------------

        ExtentManager.logStep(
                "Navigating to Directory page"
        );

        directoryPage.clickDirectoryTab();


        // -----------------------------------------
        // STEP 3: Search Employee
        // -----------------------------------------

        ExtentManager.logStep(
                "Searching for employee: Vishal Chandrabanshi"
        );

        directoryPage.searchEmployeeByName(
                "Vishal",
                "Vishal Chandrabanshi"
        );


        // -----------------------------------------
        // STEP 4: Verify Employee
        // -----------------------------------------

        ExtentManager.logStep(
                "Verifying employee search result"
        );

        Assert.assertTrue(
                directoryPage.verifyEmployeeDisplayed(
                        "Vishal Chandrabanshi"
                ),
                "Vishal Chandrabanshi was not found in Directory search results"
        );


        // -----------------------------------------
        // STEP 5: Success
        // -----------------------------------------

        ExtentManager.logStep(
                "Employee search successful: Vishal Chandrabanshi"
        );
    }


    // ==================== Reset Search Test ====================

    @Test
    public void resetDirectorySearchTest() {

        // Login
        loginAsAdmin();


        // Navigate to Directory
        ExtentManager.logStep(
                "Navigating to Directory page"
        );

        directoryPage.clickDirectoryTab();


        // Search employee
        ExtentManager.logStep(
                "Searching for employee: Vishal Chandrabanshi"
        );

        directoryPage.searchEmployeeByName(
                "Vishal",
                "Vishal Chandrabanshi"
        );


        // Reset
        ExtentManager.logStep(
                "Resetting Directory search"
        );

        directoryPage.resetSearch();


        ExtentManager.logStep(
                "Directory search reset successfully"
        );
    }
}
