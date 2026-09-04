package com.orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.base.BaseClass;
import com.orangehrm.utilities.ExtentManager;

public class AdminUserManagementPage {

	private ActionDriver actionDriver;

	// ===================== Navigation Locators =====================
	private By adminTab = By.xpath("//span[text()='Admin']");
	private By addButton = By.xpath("//button[normalize-space()='Add']");

	// ===================== Add/Edit User Form Locators =====================
	// Note: User Role and Status are custom OrangeHRM dropdowns (not native
	// <select>), so they are opened via click() and the option is clicked
	// separately - selectByVisibleText() from ActionDriver won't work on these.
	// Note: These use an ancestor-based lookup - find the nearest enclosing
	// field container (div.oxd-input-group) starting from the label, then
	// search *within* that container for the input/dropdown. This is
	// deliberately more forgiving about exactly how deep the label and
	// input are nested relative to each other than a rigid
	// parent/following-sibling assumption, which was pointing at the wrong
	// element (or nothing) on this form.
	// Note: exact text() matching (e.g. text()='Employee Name*') was
	// verified via browser console to NEVER match - the '*' required-marker
	// is rendered as a separate nested element inside the label, not as
	// part of its direct text node, so text()='Label*' can structurally
	// never be true. Using contains(text(), 'Label') instead, which matches
	// regardless of how the '*' is nested.
	// Note: the ancestor class check uses exact token matching via
	// concat(' ', normalize-space(@class), ' ') rather than a plain
	// contains(@class, 'oxd-input-group'). This was verified necessary via
	// browser console: the label's immediate parent wrapper is
	// "oxd-input-group__label-wrapper", which also CONTAINS the substring
	// "oxd-input-group" - so a loose contains() match grabbed that
	// label-only wrapper (the nearest ancestor) instead of the real field
	// container ("oxd-input-group oxd-input-field-bottom-space") one level
	// further up, which is where the input actually lives.
	private static final String INPUT_GROUP_ANCESTOR = "ancestor::div[contains(concat(' ',normalize-space(@class),' '),' oxd-input-group ')][1]";
	private By userRoleDropdown = By.xpath("//label[contains(text(),'User Role')]/" + INPUT_GROUP_ANCESTOR + "//div[contains(@class,'oxd-select-text')]");
	private By statusDropdown = By.xpath("//label[contains(text(),'Status')]/" + INPUT_GROUP_ANCESTOR + "//div[contains(@class,'oxd-select-text')]");
	private By employeeNameInput = By.xpath("//label[contains(text(),'Employee Name')]/" + INPUT_GROUP_ANCESTOR + "//input");
	private By usernameInput = By.xpath("//label[contains(text(),'Username')]/" + INPUT_GROUP_ANCESTOR + "//input");
	// Password vs Confirm Password both contain the word "Password", so
	// Password's locator explicitly excludes labels that also contain
	// "Confirm" to avoid matching the wrong field.
	private By passwordInput = By.xpath("(//label[contains(text(),'Password') and not(contains(text(),'Confirm'))]/" + INPUT_GROUP_ANCESTOR + "//input)[1]");
	private By confirmPasswordInput = By.xpath("//label[contains(text(),'Confirm Password')]/" + INPUT_GROUP_ANCESTOR + "//input");
	private By saveButton = By.xpath("//button[@type='submit']");
	private By cancelButton = By.xpath("//button[normalize-space()='Cancel']");
	private By employeeNameInvalidText = By.xpath("//label[contains(text(),'Employee Name')]/" + INPUT_GROUP_ANCESTOR + "//span[text()='Invalid']");


	// ===================== System Users List / Search Locators ===============
	private By usernameFilterInput = By.xpath("//label[text()='Username']/parent::div/following-sibling::div/input");
	private By searchButton = By.xpath("//button[@type='submit']");
	private By resetButton = By.xpath("//button[normalize-space()='Reset']");
	private By recordsFoundText = By.xpath("//span[contains(@class,'oxd-text--span')][contains(text(),'Records Found')]");
	private By noRecordsFoundText = By.xpath("//span[text()='No Records Found']");

	public AdminUserManagementPage(WebDriver driver) {
		this.actionDriver = BaseClass.getActionDriver();
	}

	// ===================== Navigation Methods =====================

	// Method to navigate from Home Page to Admin > User Management (Admin tab
	// lands directly on User Management by default in OrangeHRM)
	public void navigateToUserManagement() {
		actionDriver.click(adminTab);
	}

	// Method to click the Add button to open the Add User form
	public void clickAddUser() {
		actionDriver.click(addButton);
		// Genuinely block until Employee Name (the slowest-to-hydrate field
		// - a React/Vue autocomplete widget) is visible, so callers never
		// touch the form before it's really ready.
		try {
			actionDriver.waitUntilAnyVisible(employeeNameInput);
		} catch (RuntimeException e) {
			// Capture a screenshot at the EXACT moment of timeout - not
			// before, not after - so we can see what's actually on screen
			// when this happens instead of inferring it from later state.
			ExtentManager.logFailure(BaseClass.getDriver(), "Employee Name field not visible after clicking Add:",
					"clickAddUser_timeout_moment");
			throw e;
		}
	}

	// ===================== Custom Dropdown Handling =====================
	// OrangeHRM dropdowns are div-based, not native <select> elements, so we
	// open the dropdown then click the option matching the visible text.
	private void selectCustomDropdown(By dropdownLocator, String optionText) {
		actionDriver.click(dropdownLocator);
		By optionLocator = By.xpath("//div[@role='listbox']//span[text()='" + optionText + "']");
		actionDriver.click(optionLocator);
	}

	public void selectUserRole(String role) {
		selectCustomDropdown(userRoleDropdown, role);
	}

	public void selectStatus(String status) {
		selectCustomDropdown(statusDropdown, status);
	}

	// Method to select Employee Name from the autocomplete dropdown - types a
	// partial name then clicks the matching suggestion (typing the full name
	// without clicking the suggestion leaves the field marked "Invalid")
	public void selectEmployeeName(String partialName, String fullNameToSelect) {
		actionDriver.enterText(employeeNameInput, partialName);
		// Verified via browser console: the option's text can contain extra
		// internal whitespace (e.g. a double space between first/last
		// name), so normalize-space() is used to collapse whitespace before
		// comparing rather than relying on an exact/literal match.
		By suggestionOption = By.xpath("//div[contains(concat(' ',normalize-space(@class),' '),' oxd-autocomplete-option ')][contains(normalize-space(.),'"
				+ fullNameToSelect + "')]");
		actionDriver.click(suggestionOption);
	}

	// Method to check if Employee Name field shows "Invalid" (i.e. a
	// suggestion was never clicked / employee doesn't exist in PIM)
	public boolean isEmployeeNameInvalid() {
		return actionDriver.isDisplayed(employeeNameInvalidText);
	}

	// Method to type a name into Employee Name WITHOUT clicking any
	// autocomplete suggestion, then press Tab to blur the field - OrangeHRM
	// only shows the "Invalid" message after the field loses focus, not just
	// from typing. We send Tab directly to this field (not a click on a
	// different field) because the autocomplete suggestion box can visually
	// cover nearby fields, causing click-intercepted errors and endless
	// retries via RetryAnalyzer.
	public void enterEmployeeNameWithoutSelecting(String name) {
		actionDriver.enterText(employeeNameInput, name);
		actionDriver.sendKeysWithActions(employeeNameInput, org.openqa.selenium.Keys.TAB.toString());
	}

	public void enterUsername(String username) {
		actionDriver.enterText(usernameInput, username);
	}

	public void enterPassword(String password) {
		actionDriver.enterText(passwordInput, password);
	}

	public void enterConfirmPassword(String password) {
		actionDriver.enterText(confirmPasswordInput, password);
	}

	public void clickSave() {
		actionDriver.click(saveButton);
	}

	public void clickCancel() {
		actionDriver.click(cancelButton);
	}

	// Convenience method that fills the entire Add User form in one call
	public void addUser(String role, String employeePartialName, String employeeFullName, String status,
			String username, String password) {
		clickAddUser();
		selectUserRole(role);
		selectEmployeeName(employeePartialName, employeeFullName);
		selectStatus(status);
		enterUsername(username);
		enterPassword(password);
		enterConfirmPassword(password);
		clickSave();
	}

	// ===================== Search / List Verification Methods =============

	// Method to search for a user by username
	public void searchByUsername(String username) {
		actionDriver.enterText(usernameFilterInput, username);
		actionDriver.click(searchButton);
	}

	public void resetSearch() {
		actionDriver.click(resetButton);
	}

	// Method to verify a user row exists in the System Users table by username
	public boolean isUserPresentInTable(String username) {
		By userRow = By.xpath("//div[@class='oxd-table-card']//div[text()='" + username + "']");
		return actionDriver.isDisplayed(userRow);
	}

	// Method to verify a user's Employee Name value in the table matches
	public boolean verifyUserEmployeeName(String username, String expectedEmployeeName) {
		By employeeNameCell = By.xpath("//div[@class='oxd-table-card'][.//div[text()='" + username
				+ "']]//div[text()='" + expectedEmployeeName + "']");
		return actionDriver.isDisplayed(employeeNameCell);
	}

	// Method to verify a user's Status value in the table matches
	public boolean verifyUserStatus(String username, String expectedStatus) {
		By statusCell = By.xpath("//div[@class='oxd-table-card'][.//div[text()='" + username
				+ "']]//div[text()='" + expectedStatus + "']");
		return actionDriver.isDisplayed(statusCell);
	}

	public boolean isNoRecordsFoundDisplayed() {
		return actionDriver.isDisplayed(noRecordsFoundText);
	}

	// ===================== Delete User Methods =====================

	// Method to click the delete (trash) icon for a specific username row
	public void deleteUserByUsername(String username) {
		By deleteIcon = By.xpath("//div[@class='oxd-table-card'][.//div[text()='" + username
				+ "']]//i[contains(@class,'bi-trash')]/parent::button");
		actionDriver.click(deleteIcon);
		By confirmDeleteButton = By.xpath("//button[normalize-space()='Yes, Delete']");
		actionDriver.click(confirmDeleteButton);
	}
}