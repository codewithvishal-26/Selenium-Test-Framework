package com.orangehrm.test;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.AdminUserManagementPage;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.utilities.ExtentManager;

public class AdminUserManagementTest extends BaseClass {

	private LoginPage loginPage;
	private HomePage homePage;
	private AdminUserManagementPage userMgmtPage;

	@BeforeMethod
	public void setupPages() {
		loginPage = new LoginPage(getDriver());
		homePage = new HomePage(getDriver());
		userMgmtPage = new AdminUserManagementPage(getDriver());
	}

	// Every test method logs in first (rather than doing it in @BeforeMethod),
	// since ExtentManager's report object isn't ready yet during @BeforeMethod -
	// calling login() there causes a NullPointerException inside ActionDriver's
	// click() when it tries to log the click step right after.
	private void loginAsAdmin() {
		String adminUsername = getProp().getProperty("username");
		String adminPassword = getProp().getProperty("password");
		loginPage.login(adminUsername, adminPassword);
		Assert.assertTrue(homePage.isAdminTabVisible(), "Login failed - Admin tab not visible");
	}

	// Positive case: Add a new user with valid data, selecting an existing
	// employee from the autocomplete dropdown, and verify it appears in the list
	@Test
	public void addUserWithValidDataTest() {
		loginAsAdmin();
		userMgmtPage.navigateToUserManagement();
		ExtentManager.logStep("Navigated to Admin > User Management");

		String uniqueUsername = "TestUser_" + System.currentTimeMillis();

		userMgmtPage.addUser(
				"Admin",                 // User Role
				"Vis",                    // partial name typed into Employee Name field
				"Vishal Chandrabanshi",   // full name to click in the autocomplete suggestion
				"Enabled",                 // Status
				uniqueUsername,            // Username
				"Str0ng@Pass1"             // Password / Confirm Password
		);
		ExtentManager.logStep("Submitted Add User form for username: " + uniqueUsername);

		staticWait(2);
		Assert.assertTrue(userMgmtPage.isUserPresentInTable(uniqueUsername),
				"Newly added user should appear in the System Users table");
		Assert.assertTrue(userMgmtPage.verifyUserEmployeeName(uniqueUsername, "Vishal Chandrabanshi"),
				"Employee Name in table should match the one selected during Add User");
		Assert.assertTrue(userMgmtPage.verifyUserStatus(uniqueUsername, "Enabled"),
				"Status in table should be Enabled");
		ExtentManager.logStep("Add User validation successful");
	}

	// Negative case: typing an Employee Name without clicking the autocomplete
	// suggestion should leave the field marked Invalid and block submission
	@Test
	public void addUserWithUnselectedEmployeeNameTest() {
		loginAsAdmin();
		userMgmtPage.navigateToUserManagement();
		userMgmtPage.clickAddUser();
		ExtentManager.logStep("Opened Add User form");

		// Directly type a full name into the field without clicking any
		// suggestion, then blur the field - this is expected to be rejected
		// as Invalid
		userMgmtPage.enterEmployeeNameWithoutSelecting("Animesh Singh");
		Assert.assertTrue(userMgmtPage.isEmployeeNameInvalid(),
				"Employee Name field should show Invalid when no autocomplete suggestion is clicked");
		ExtentManager.logStep("Validation successful - Invalid shown for unselected Employee Name");

		userMgmtPage.clickCancel();
	}

	// Search case: verify searching by username returns the correct user
	@Test
	public void searchUserByUsernameTest() {
		loginAsAdmin();
		userMgmtPage.navigateToUserManagement();
		userMgmtPage.searchByUsername("Admin");
		staticWait(1);
		ExtentManager.logStep("Searched for username: Admin");

		Assert.assertTrue(userMgmtPage.isUserPresentInTable("Admin"),
				"Search should return the Admin user in the results table");
		ExtentManager.logStep("Search validation successful");
	}

	// Search case: verify searching for a non-existent username shows no results
	@Test
	public void searchUserWithNoMatchTest() {
		loginAsAdmin();
		userMgmtPage.navigateToUserManagement();
		userMgmtPage.searchByUsername("NonExistentUser_12345");
		staticWait(1);
		ExtentManager.logStep("Searched for a username that does not exist");

		Assert.assertTrue(userMgmtPage.isNoRecordsFoundDisplayed(),
				"'No Records Found' should be displayed for a non-matching search");
		ExtentManager.logStep("No-match search validation successful");
	}
}