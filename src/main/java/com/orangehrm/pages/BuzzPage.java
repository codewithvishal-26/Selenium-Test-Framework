package com.orangehrm.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.base.BaseClass;

public class BuzzPage {

    private ActionDriver actionDriver;
    private WebDriver driver;
    private WebDriverWait wait;


    // =========================================================
    // Navigation
    // =========================================================

    private By buzzTab =
            By.xpath("//span[normalize-space()='Buzz']");


    // =========================================================
    // Buzz Page Elements
    // =========================================================

    private By postTextArea =
            By.cssSelector("textarea[placeholder=\"What's on your mind?\"]");


    private By postButton =
            By.xpath("//button[normalize-space()='Post']");


    // =========================================================
    // Constructor
    // =========================================================

    public BuzzPage(WebDriver driver) {

        this.driver = driver;

        this.actionDriver = BaseClass.getActionDriver();

        this.wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(15)
        );
    }


    // =========================================================
    // Navigate to Buzz
    // =========================================================

    public void clickBuzzTab() {

        actionDriver.dismissAnyOpenDropdown();

        wait.until(
                ExpectedConditions.elementToBeClickable(buzzTab)
        ).click();
    }


    // =========================================================
    // Verify Buzz Page
    // =========================================================

    public boolean isBuzzPageDisplayed() {

        try {

            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            postTextArea
                    )
            ).isDisplayed();

        } catch (Exception e) {

            return false;
        }
    }


    // =========================================================
    // Create Post
    // =========================================================

    public void createPost(String postText) {

        WebDriverWait wait =
                new WebDriverWait(driver, Duration.ofSeconds(15));

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        postTextArea
                )
        ).sendKeys(postText);


        wait.until(
                ExpectedConditions.elementToBeClickable(
                        postButton
                )
        ).click();
    }


    // =========================================================
    // Verify Created Post
    // =========================================================

    public boolean isPostDisplayed(String postText) {

        try {

            By createdPost = By.xpath(
                    "//*[contains(normalize-space(.),\"" +
                    postText +
                    "\")]"
            );

            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            createdPost
                    )
            ).isDisplayed();

        } catch (Exception e) {

            return false;
        }
    }
}