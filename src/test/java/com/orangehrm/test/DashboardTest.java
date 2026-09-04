package com.orangehrm.test;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.DashboardPage;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.utilities.ExtentManager;

public class DashboardTest extends BaseClass {

    private LoginPage loginPage;
    private HomePage homePage;
    private DashboardPage dashboardPage;


    // =========================================================
    // Page Setup
    // =========================================================

    @BeforeMethod
    public void setupPages() {

        loginPage = new LoginPage(getDriver());

        homePage = new HomePage(getDriver());

        dashboardPage = new DashboardPage(getDriver());
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
    // Dashboard Test
    // =========================================================

    @Test
    public void verifyDashboardTest() {

        // -----------------------------------------------------
        // STEP 1: Login
        // -----------------------------------------------------

        loginAsAdmin();


        // -----------------------------------------------------
        // STEP 2: Verify Dashboard
        // -----------------------------------------------------

        ExtentManager.logStep(
                "Verifying Dashboard page"
        );

        Assert.assertTrue(
                dashboardPage.isDashboardDisplayed(),
                "Dashboard heading is not displayed"
        );


        // -----------------------------------------------------
        // STEP 3: Verify Attendance Card
        // -----------------------------------------------------

        ExtentManager.logStep(
                "Verifying Attendance card"
        );

        Assert.assertTrue(
                dashboardPage.isAttendanceCardDisplayed(),
                "Attendance card is not displayed"
        );


        // -----------------------------------------------------
        // STEP 4: Verify Quick Launch
        // -----------------------------------------------------

        ExtentManager.logStep(
                "Verifying Quick Launch section"
        );

        Assert.assertTrue(
                dashboardPage.isQuickLaunchDisplayed(),
                "Quick Launch buttons are not displayed"
        );


        // -----------------------------------------------------
        // STEP 5: Success
        // -----------------------------------------------------

        ExtentManager.logStep(
                "Dashboard verification completed successfully"
        );
    }
}