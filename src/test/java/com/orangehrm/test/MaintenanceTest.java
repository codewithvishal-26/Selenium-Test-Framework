
package com.orangehrm.test;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.pages.MaintenancePage;
import com.orangehrm.utilities.ExtentManager;

public class MaintenanceTest extends BaseClass {

    private LoginPage loginPage;
    private HomePage homePage;
    private MaintenancePage maintenancePage;


    // =========================================================
    // Page Setup
    // =========================================================

    @BeforeMethod
    public void setupPages() {

        loginPage = new LoginPage(getDriver());

        homePage = new HomePage(getDriver());

        maintenancePage = new MaintenancePage(getDriver());
    }


    // =========================================================
    // Login
    // =========================================================

    private void loginAsAdmin() {

        String username =
                getProp().getProperty("username");

        String password =
                getProp().getProperty("password");


        ExtentManager.logStep(
                "Logging into OrangeHRM"
        );

        loginPage.login(
                username,
                password
        );


        Assert.assertTrue(
                homePage.isAdminTabVisible(),
                "Login failed - Admin tab is not visible"
        );
    }


    // =========================================================
    // Maintenance Test
    // =========================================================

    @Test
    public void accessMaintenancePageTest() {

        // -----------------------------------------------------
        // STEP 1: Login
        // -----------------------------------------------------

        loginAsAdmin();


        // -----------------------------------------------------
        // STEP 2: Navigate to Maintenance
        // -----------------------------------------------------

        ExtentManager.logStep(
                "Navigating to Maintenance page"
        );

        maintenancePage.clickMaintenanceTab();


        // -----------------------------------------------------
        // STEP 3: Get Existing Password
        // -----------------------------------------------------

        String password =
                getProp().getProperty("password");


        // -----------------------------------------------------
        // STEP 4: Enter Password
        // -----------------------------------------------------

        ExtentManager.logStep(
                "Entering administrator password"
        );

        maintenancePage.enterAdministratorPassword(
                password
        );


        // -----------------------------------------------------
        // STEP 5: Wait for Maintenance Page
        // -----------------------------------------------------

        ExtentManager.logStep(
                "Waiting for Maintenance page to load"
        );

        maintenancePage.waitForMaintenancePage();


        // -----------------------------------------------------
        // STEP 6: Verify Maintenance Page
        // -----------------------------------------------------

        ExtentManager.logStep(
                "Verifying Purge Employee Records section"
        );

        Assert.assertTrue(
                maintenancePage.isMaintenancePageDisplayed(),
                "Purge Employee Records heading is not displayed"
        );


        // -----------------------------------------------------
        // STEP 7: Success
        // -----------------------------------------------------

        ExtentManager.logStep(
                "Maintenance page opened successfully"
        );
    }
}
