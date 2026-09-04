package com.orangehrm.test;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.BuzzPage;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.utilities.ExtentManager;

public class BuzzTest extends BaseClass {

    private LoginPage loginPage;
    private HomePage homePage;
    private BuzzPage buzzPage;


    // =========================================================
    // Page Setup
    // =========================================================

    @BeforeMethod
    public void setupPages() {

        loginPage = new LoginPage(getDriver());

        homePage = new HomePage(getDriver());

        buzzPage = new BuzzPage(getDriver());
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
    // Create Buzz Post
    // =========================================================

    @Test
    public void createBuzzPostTest() {

        // -----------------------------------------------------
        // STEP 1: Login
        // -----------------------------------------------------

        loginAsAdmin();


        // -----------------------------------------------------
        // STEP 2: Navigate to Buzz
        // -----------------------------------------------------

        ExtentManager.logStep(
                "Navigating to Buzz module"
        );

        buzzPage.clickBuzzTab();


        // -----------------------------------------------------
        // STEP 3: Verify Buzz Page
        // -----------------------------------------------------

        Assert.assertTrue(
                buzzPage.isBuzzPageDisplayed(),
                "Buzz page is not displayed"
        );


        // -----------------------------------------------------
        // STEP 4: Create Post
        // -----------------------------------------------------

        String postText =
                "Automation framework testing - " +
                System.currentTimeMillis();


        ExtentManager.logStep(
                "Creating a new Buzz post"
        );

        buzzPage.createPost(
                postText
        );


        // -----------------------------------------------------
        // STEP 5: Verify Post
        // -----------------------------------------------------

        ExtentManager.logStep(
                "Verifying created Buzz post"
        );

        Assert.assertTrue(
                buzzPage.isPostDisplayed(postText),
                "Created Buzz post is not displayed"
        );


        // -----------------------------------------------------
        // STEP 6: Success
        // -----------------------------------------------------

        ExtentManager.logStep(
                "Buzz post created successfully"
        );
    }
}