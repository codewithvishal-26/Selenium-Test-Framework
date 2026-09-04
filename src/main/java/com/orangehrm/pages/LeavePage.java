package com.orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.base.BaseClass;

public class LeavePage {

	private ActionDriver actionDriver;

	// Verified via browser console (see project history): Start Month /
	// Start Date use the same oxd-select-text custom-dropdown pattern
	// already proven on the Admin page, and dropdown option text
	// (month names) is plain, unformatted text.
	private static final String INPUT_GROUP_ANCESTOR = "ancestor::div[contains(concat(' ',normalize-space(@class),' '),' oxd-input-group ')][1]";
	private By startMonthDropdown = By.xpath("//label[contains(text(),'Start Month')]/" + INPUT_GROUP_ANCESTOR + "//div[contains(@class,'oxd-select-text')]");
	private By saveButton = By.xpath("//button[contains(normalize-space(.),'Save')]");

	// NOT YET VERIFIED - built by inference from the working Admin dropdown
	// pattern, not confirmed against this page's live DOM. Check first in
	// the batch-verification pass if selectStartMonth() fails.
	private By monthOption(String month) {
		return By.xpath("//div[@role='listbox']//span[text()='" + month + "']");
	}

	public LeavePage(WebDriver driver) {
		this.actionDriver = BaseClass.getActionDriver();
	}

	public void selectStartMonth(String month) {
		actionDriver.click(startMonthDropdown);
		actionDriver.click(monthOption(month));
	}

	public void clickSave() {
		actionDriver.click(saveButton);
	}

	// Reads back the currently-displayed Start Month value (used to verify
	// a save actually persisted, by reloading the page and re-checking this
	// rather than depending on a guessed toast/notification locator).
	public String getStartMonthValue() {
		return actionDriver.getText(startMonthDropdown);
	}

	// ---- Configure > Leave Period ----
	private By leavePeriodMenuItem = By.xpath("//a[contains(normalize-space(.),'Leave Period')]");

	public void navigateToLeavePeriodConfig() {
		actionDriver.click(configureTab);
		actionDriver.click(leavePeriodMenuItem);
		actionDriver.dismissAnyOpenDropdown();
	}

	// ---- Configure > Leave Types ----
	// Configure is a dropdown menu tab; "Leave Types" is one of its items.
	private By configureTab = By.xpath("//span[contains(text(),'Configure')]");
	private By leaveTypesMenuItem = By.xpath("//a[contains(normalize-space(.),'Leave Types')]");
	private By addLeaveTypeButton = By.xpath("//button[contains(normalize-space(.),'Add')]");
	private By leaveTypeNameInput = By.xpath("//label[contains(text(),'Name')]/" + INPUT_GROUP_ANCESTOR + "//input");
	// Reuses the same .oxd-table-body list-verification pattern already
	// confirmed working on the Admin, PIM, and Leave Period pages (same
	// reusable Vue table component across the site).
	private By leaveTypesTableBody = By.className("oxd-table-body");

	public void navigateToLeaveTypesConfig() {
		actionDriver.click(configureTab);
		actionDriver.click(leaveTypesMenuItem);
		actionDriver.dismissAnyOpenDropdown();
	}

	public void clickAddLeaveType() {
		actionDriver.click(addLeaveTypeButton);
	}

	// Fills the Name field and saves, leaving "Is Entitlement Situational?"
	// at its default ("No") since we don't yet have a reason to test the
	// "Yes" path.
	public void addLeaveType(String name) {
		actionDriver.enterText(leaveTypeNameInput, name);
		actionDriver.click(saveButton);
		actionDriver.waitForInvisibility(By.className("oxd-toast-container"));
	}

	private By leaveListTab = By.xpath("//a[contains(normalize-space(.),'Leave List')]");
	private By resetButton = By.xpath("//button[contains(normalize-space(.),'Reset')]");

	private By leaveListSearchButton = By.xpath("//button[contains(normalize-space(.),'Search')]");

	// Navigates specifically to Leave List (clicking the Leave sidebar tab
	// alone lands on a different default tab) and clears any lingering
	// status filter, since Leave List defaults to filtering by "Pending
	// Approval" - a leave record with a different status could otherwise
	// be invisible even though it saved correctly. Reset alone only clears
	// the filter fields - it doesn't re-run the search - so Search must be
	// clicked afterward to actually reload the (now unfiltered) list.
	// NOT YET VERIFIED via console (time-constrained) - best-effort attempt
	// to remove the "Pending Approval" status filter chip, which the
	// generic Reset button does NOT clear (confirmed via screenshot: the
	// chip remains visible even after Reset). If leave assigned by an
	// Admin gets a different status than "Pending Approval", this leftover
	// filter would hide it from every search. Uses clickIfPresent so it's
	// safe even if this specific locator turns out wrong.
	private By pendingApprovalChipRemove = By.xpath("//*[contains(text(),'Pending Approval')]/following-sibling::i | //*[contains(text(),'Pending Approval')]//i");

	public void navigateToLeaveList() {
		actionDriver.click(leaveListTab);
		actionDriver.click(resetButton);
		actionDriver.clickIfPresent(pendingApprovalChipRemove, 3);
		actionDriver.click(leaveListSearchButton);
	}

	public boolean verifyLeaveTypeExists(String name) {
		String actualNoSpaces = actionDriver.getText(leaveTypesTableBody).replaceAll("\\s+", "");
		String expectedNoSpaces = name.replaceAll("\\s+", "");
		return actualNoSpaces.contains(expectedNoSpaces);
	}

	// ---- Entitlements > Add Entitlement ----
	private By entitlementsTab = By.xpath("//span[contains(text(),'Entitlements')]");
	private By addEntitlementMenuItem = By.xpath("//a[contains(normalize-space(.),'Add Entitlement')]");
	private By entitlementEmployeeNameInput = By.xpath("//label[contains(text(),'Employee Name')]/" + INPUT_GROUP_ANCESTOR + "//input");
	private By entitlementLeaveTypeDropdown = By.xpath("//label[contains(text(),'Leave Type')]/" + INPUT_GROUP_ANCESTOR + "//div[contains(@class,'oxd-select-text')]");
	private By entitlementLeavePeriodDropdown = By.xpath("//label[contains(text(),'Leave Period')]/" + INPUT_GROUP_ANCESTOR + "//div[contains(@class,'oxd-select-text')]");
	private By entitlementDaysInput = By.xpath("//label[contains(text(),'Entitlement')]/" + INPUT_GROUP_ANCESTOR + "//input");

	public void navigateToAddEntitlement() {
		actionDriver.click(entitlementsTab);
		actionDriver.click(addEntitlementMenuItem);
		actionDriver.dismissAnyOpenDropdown();
	}

	// Assigns a leave balance (entitlement) to an employee for a given
	// leave type, for the currently-listed Leave Period (first/only option
	// - selected positionally since we don't yet have a specific date to
	// match against). This is a prerequisite for Apply/Assign Leave to
	// actually work, not just for the leave type to exist.
	public void addEntitlement(String partialEmployeeName, String fullEmployeeName, String leaveTypeName, String days) {
		actionDriver.enterText(entitlementEmployeeNameInput, partialEmployeeName);
		By employeeSuggestion = By.xpath("//div[contains(concat(' ',normalize-space(@class),' '),' oxd-autocomplete-option ')][contains(normalize-space(.),'"
				+ fullEmployeeName + "')]");
		actionDriver.click(employeeSuggestion);

		actionDriver.click(entitlementLeaveTypeDropdown);
		By leaveTypeOption = By.xpath("//div[@role='listbox']//span[text()='" + leaveTypeName + "']");
		// Every test run creates a brand-new, uniquely-named leave type,
		// so this dropdown's option list only grows over repeated runs -
		// the matching option can end up scrolled out of the listbox's
		// visible area, where a plain click() (no scroll-into-view) can
		// time out even though the element exists in the DOM.
		actionDriver.scrollToElement(leaveTypeOption);
		actionDriver.click(leaveTypeOption);

		actionDriver.click(entitlementLeavePeriodDropdown);
		// Selects whichever leave period option appears first/only -
		// there's typically just one active period.
		By anyPeriodOption = By.xpath("(//div[@role='listbox']//span)[1]");
		actionDriver.click(anyPeriodOption);

		actionDriver.enterText(entitlementDaysInput, days);
		actionDriver.click(saveButton);
		// Verified via screenshot: saving an entitlement can trigger an
		// "Updating Entitlement" confirmation modal with a full-page
		// overlay - this was the actual root cause of every subsequent
		// click (e.g. back to the Leave sidebar tab) getting intercepted.
		// Only appears conditionally, so this is a soft/optional click.
		By confirmButton = By.xpath("//button[contains(normalize-space(.),'Confirm')]");
		actionDriver.clickIfPresent(confirmButton, 5);
		actionDriver.waitForInvisibility(By.className("oxd-toast-container"));
	}

	// ---- Assign Leave ----
	private By assignLeaveTab = By.xpath("//a[contains(normalize-space(.),'Assign Leave')]");
	private By assignEmployeeNameInput = By.xpath("//label[contains(text(),'Employee Name')]/" + INPUT_GROUP_ANCESTOR + "//input");
	private By assignLeaveTypeDropdown = By.xpath("//label[contains(text(),'Leave Type')]/" + INPUT_GROUP_ANCESTOR + "//div[contains(@class,'oxd-select-text')]");
	private By fromDateInput = By.xpath("//label[contains(text(),'From Date')]/" + INPUT_GROUP_ANCESTOR + "//input");
	private By toDateInput = By.xpath("//label[contains(text(),'To Date')]/" + INPUT_GROUP_ANCESTOR + "//input");
	private By commentsTextarea = By.xpath("//textarea");
	private By assignButton = By.xpath("//button[contains(normalize-space(.),'Assign')]");

	private By assignLeaveBalanceText = By.xpath("//p[contains(concat(' ',normalize-space(@class),' '),' orangehrm-leave-balance-text ')]");

	// Reads the displayed Leave Balance (e.g. "10.00 Day(s)") after
	// selecting an employee and leave type on the Assign Leave page.
	public String getLeaveBalanceText() {
		return actionDriver.getText(assignLeaveBalanceText);
	}

	public void clickAssignLeaveTab() {
		actionDriver.click(assignLeaveTab);
	}

	// Reuses the same oxd-autocomplete-option suggestion pattern verified
	// and proven working on the Admin and PIM pages (same Vue component).
	public void selectEmployeeForLeave(String partialName, String fullNameToSelect) {
		actionDriver.enterText(assignEmployeeNameInput, partialName);
		By suggestionOption = By.xpath("//div[contains(concat(' ',normalize-space(@class),' '),' oxd-autocomplete-option ')][contains(normalize-space(.),'"
				+ fullNameToSelect + "')]");
		actionDriver.click(suggestionOption);
	}

	// Reuses the oxd-select-text dropdown + listbox option pattern already
	// verified for Leave Period's Start Month dropdown.
	public void selectLeaveType(String leaveTypeName) {
		actionDriver.click(assignLeaveTypeDropdown);
		By option = By.xpath("//div[@role='listbox']//span[text()='" + leaveTypeName + "']");
		// Same fix as addEntitlement's leave type selection: scroll the
		// matching option into view first, since the list keeps growing
		// with every run and the newest entry can end up below the
		// listbox's visible scroll area.
		actionDriver.scrollToElement(option);
		actionDriver.click(option);
	}

	// NOT YET VERIFIED: this is the first date-type input this framework
	// has interacted with. Assuming plain sendKeys works since the field
	// showed as a normal text input with a "yyyy-mm-dd" placeholder (not a
	// native HTML5 date picker) - if this fails, check whether it actually
	// requires the calendar icon/picker UI instead of direct typing.
	// NOTE: OrangeHRM auto-fills To Date to match From Date as soon as From
	// Date is set. Re-typing into To Date when it's already correct caused
	// the typed value to get appended onto the auto-filled one instead of
	// replacing it (e.g. "2026-09-012026-09-01") - the field's clear()
	// doesn't fully reset this Vue-controlled input. Skipping the retype
	// when the dates already match avoids the bug entirely for same-day
	// leave (our current use case).
	public void enterLeaveDates(String fromDate, String toDate) {
		actionDriver.enterText(fromDateInput, fromDate);
		if (!toDate.equals(fromDate)) {
			actionDriver.enterText(toDateInput, toDate);
		}
	}

	public void enterLeaveComments(String comments) {
		actionDriver.enterText(commentsTextarea, comments);
	}

	public void clickAssign() {
		actionDriver.click(assignButton);
		// Same conditional confirmation-modal pattern confirmed on the
		// entitlement save - applying it here too as a precaution.
		By confirmButton = By.xpath("//button[contains(normalize-space(.),'Confirm')]");
		actionDriver.clickIfPresent(confirmButton, 5);
		actionDriver.waitForInvisibility(By.className("oxd-toast-container"));
	}

	// ---- Apply for Leave ----
	private By applyTab = By.xpath("//a[contains(normalize-space(.),'Apply')]");
	// Apply's Leave Type dropdown and date/comment fields reuse the exact
	// same locators as Assign Leave - both pages share this form layout,
	// minus the Employee Name field (Apply is self-service).
	private By applyButton = By.xpath("//button[contains(normalize-space(.),'Apply')]");

	public void clickApplyTab() {
		actionDriver.click(applyTab);
	}

	public void clickApply() {
		actionDriver.click(applyButton);
		By confirmButton = By.xpath("//button[contains(normalize-space(.),'Confirm')]");
		actionDriver.clickIfPresent(confirmButton, 5);
		actionDriver.waitForInvisibility(By.className("oxd-toast-container"));
	}
}