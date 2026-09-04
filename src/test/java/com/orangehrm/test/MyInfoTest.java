package com.orangehrm.test;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.pages.PIMPage;
import com.orangehrm.utilities.ExtentManager;

public class MyInfoTest extends BaseClass {

	private LoginPage loginPage;
	private HomePage homePage;
	private PIMPage pimPage;

	@BeforeMethod
	public void setupPages() {
		loginPage = new LoginPage(getDriver());
		homePage = new HomePage(getDriver());
		pimPage = new PIMPage(getDriver());
	}

	private void loginAsAdmin() {
		String adminUsername = getProp().getProperty("username");
		String adminPassword = getProp().getProperty("password");
		loginPage.login(adminUsername, adminPassword);
		Assert.assertTrue(homePage.isAdminTabVisible(), "Login failed - Admin tab not visible");
	}

	// Updates the logged-in user's own Driver's License Number (My Info
	// routes into the same Personal Details page used by PIM, scoped to
	// self) and verifies it persisted by reloading the page - same
	// reload-based verification pattern used for Leave Period.
	@Test
	public void updateDriversLicenseNumberTest() {
		loginAsAdmin();
		homePage.clickOnMyInfoTab();
		ExtentManager.logStep("Navigated to My Info > Personal Details");

		String uniqueLicenseNumber = "DL" + System.currentTimeMillis();
		pimPage.updateDriversLicenseNumber(uniqueLicenseNumber);
		ExtentManager.logStep("Updated Driver's License Number to: " + uniqueLicenseNumber);

		getActionDriver().refreshPage();

		Assert.assertEquals(pimPage.getDriversLicenseNumber(), uniqueLicenseNumber,
				"Driver's License Number did not persist after reload - save may not have worked");
	}
}
