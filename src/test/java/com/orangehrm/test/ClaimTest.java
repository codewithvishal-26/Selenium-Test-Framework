package com.orangehrm.test;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.ClaimPage;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.utilities.ExtentManager;

public class ClaimTest extends BaseClass {

    private LoginPage loginPage;
    private HomePage homePage;
    private ClaimPage claimPage;


    // =========================================================
    // Page Setup
    // =========================================================

    @BeforeMethod
    public void setupPages() {

        loginPage = new LoginPage(getDriver());

        homePage = new HomePage(getDriver());

        claimPage = new ClaimPage(getDriver());
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
    // Employee Claims Search Test
    // =========================================================

    @Test
    public void searchEmployeeClaimTest() {

        // -----------------------------------------------------
        // STEP 1: Login
        // -----------------------------------------------------

        loginAsAdmin();


        // -----------------------------------------------------
        // STEP 2: Navigate to Claim
        // -----------------------------------------------------

        ExtentManager.logStep(
                "Navigating to Claim module"
        );

        claimPage.clickClaimTab();


        // -----------------------------------------------------
        // STEP 3: Verify Employee Claims page
        // -----------------------------------------------------

        Assert.assertTrue(
                claimPage.isEmployeeClaimsPageDisplayed(),
                "Employee Claims page is not displayed"
        );


        // -----------------------------------------------------
        // STEP 4: Search Employee
        // -----------------------------------------------------

        ExtentManager.logStep(
                "Searching Employee Claims for Vishal"
        );

        claimPage.searchEmployee(
                "Vishal"
        );


        // -----------------------------------------------------
        // STEP 5: Verify page after search
        // -----------------------------------------------------

        Assert.assertTrue(
                claimPage.isEmployeeClaimsPageDisplayed(),
                "Employee Claims page is not displayed after search"
        );


        // -----------------------------------------------------
        // STEP 6: Success
        // -----------------------------------------------------

        ExtentManager.logStep(
                "Employee Claims search completed successfully"
        );
    }
}