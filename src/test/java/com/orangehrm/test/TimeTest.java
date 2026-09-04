package com.orangehrm.test;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.pages.TimePage;
import com.orangehrm.utilities.ExtentManager;

public class TimeTest extends BaseClass {

	private LoginPage loginPage;
	private HomePage homePage;
	private TimePage timePage;

	@BeforeMethod
	public void setupPages() {
		loginPage = new LoginPage(getDriver());
		homePage = new HomePage(getDriver());
		timePage = new TimePage(getDriver());
	}

	private void loginAsAdmin() {
		String adminUsername = getProp().getProperty("username");
		String adminPassword = getProp().getProperty("password");
		loginPage.login(adminUsername, adminPassword);
		Assert.assertTrue(homePage.isAdminTabVisible(), "Login failed - Admin tab not visible");
	}

	// Punches in (or out, whichever the button currently shows) and
	// verifies the button's own text changed - the clearest, simplest
	// success signal on this page ("In" becomes "Out" or vice versa).
	@Test
	public void punchInOutTest() {
		loginAsAdmin();
		homePage.clickOnTimeTab();
		timePage.navigateToPunchInOut();
		ExtentManager.logStep("Navigated to Time > Attendance > Punch In/Out");

		String buttonTextBefore = timePage.getPunchButtonText();
		ExtentManager.logStep("Punch button text before: " + buttonTextBefore);

		timePage.enterPunchNote("Automated test - punch " + buttonTextBefore);
		timePage.clickPunchButton();
		ExtentManager.logStep("Clicked punch button");

		// Wait for the text to actually change rather than reading
		// immediately - there's a brief server round-trip before the
		// button visually updates.
		getActionDriver().waitForTextToChange(By.xpath("//button[contains(normalize-space(.),'In') or contains(normalize-space(.),'Out')]"), buttonTextBefore);

		String buttonTextAfter = timePage.getPunchButtonText();
		ExtentManager.logStep("Punch button text after: " + buttonTextAfter);

		Assert.assertNotEquals(buttonTextAfter, buttonTextBefore,
				"Punch button text did not change - punch action may not have persisted");
	}

	// Views a specific employee's timesheet via the Timesheets tab's
	// default search/select page.
	@Test
	public void viewEmployeeTimesheetTest() {
		loginAsAdmin();
		homePage.clickOnTimeTab();
		ExtentManager.logStep("Navigated to Time > Timesheets");

		timePage.viewEmployeeTimesheet("Vis", "Vishal Chandrabanshi");
		ExtentManager.logStep("Viewed timesheet for Vishal Chandrabanshi");

		Assert.assertTrue(timePage.isOnTimesheetPage(),
				"Did not navigate to the employee's timesheet page");
	}

	// Navigates to Attendance > My Records and confirms the page loads.
	@Test
	public void myAttendanceRecordsTest() {
		loginAsAdmin();
		homePage.clickOnTimeTab();
		timePage.navigateToMyRecords();
		ExtentManager.logStep("Navigated to Time > Attendance > My Records");

		Assert.assertTrue(timePage.isOnMyRecordsPage(),
				"Did not navigate to the My Records page");
	}
}