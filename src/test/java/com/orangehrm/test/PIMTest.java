package com.orangehrm.test;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.pages.PIMPage;
import com.orangehrm.utilities.ExtentManager;

public class PIMTest extends BaseClass {

	private LoginPage loginPage;
	private HomePage homePage;
	private PIMPage pimPage;

	@BeforeMethod
	public void setupPages() {
		loginPage = new LoginPage(getDriver());
		homePage = new HomePage(getDriver());
		pimPage = new PIMPage(getDriver());
	}

	// See AdminUserManagementTest for why login happens here rather than in
	// @BeforeMethod (ExtentManager isn't ready yet during @BeforeMethod).
	private void loginAsAdmin() {
		String adminUsername = getProp().getProperty("username");
		String adminPassword = getProp().getProperty("password");
		loginPage.login(adminUsername, adminPassword);
		Assert.assertTrue(homePage.isAdminTabVisible(), "Login failed - Admin tab not visible");
	}

	// Positive case: add a new employee with just First Name and Last Name
	// (Middle Name and Employee Id are left at their defaults), then verify
	// the save redirected to the new employee's Personal Details page and
	// that the full name shown there matches what was entered.
	@Test
	public void addEmployeeWithValidDataTest() {
		loginAsAdmin();
		homePage.clickOnPIMTab();
		ExtentManager.logStep("Navigated to PIM > Employee List");

		pimPage.clickAddEmployeeTab();
		ExtentManager.logStep("Navigated to PIM > Add Employee");

		// Timestamp suffix keeps the name unique across repeated runs,
		// same approach used for usernames in AdminUserManagementTest.
		String uniqueLastName = "Employee_" + System.currentTimeMillis();

		pimPage.addEmployee("Test", null, uniqueLastName);
		ExtentManager.logStep("Submitted Add Employee form for: Test " + uniqueLastName);

		Assert.assertTrue(pimPage.isOnPersonalDetailsPage(),
				"Save did not redirect to the employee's Personal Details page");

		ExtentManager.logStep("Verify employee full name on Personal Details page");
		Assert.assertTrue(pimPage.verifyEmployeeNameOnDetailsPage("Test " + uniqueLastName),
				"Employee full name on details page did not match what was entered");
	}

	// Searches the Employee List for the employee created above (or any
	// known existing employee) and verifies they appear in the results.
	@Test
	public void searchEmployeeByNameTest() {
		loginAsAdmin();
		homePage.clickOnPIMTab();
		ExtentManager.logStep("Navigated to PIM > Employee List");

		pimPage.searchEmployeeByName("Vis", "Vishal Chandrabanshi");
		ExtentManager.logStep("Searched for employee: Vishal Chandrabanshi");

		Assert.assertTrue(pimPage.verifySearchResultContainsEmployee("Vishal Chandrabanshi"),
				"Search results did not contain the expected employee");
	}
}