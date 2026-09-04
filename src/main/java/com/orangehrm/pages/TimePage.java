package com.orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.base.BaseClass;

public class TimePage {

	private ActionDriver actionDriver;

	private static final String INPUT_GROUP_ANCESTOR = "ancestor::div[contains(concat(' ',normalize-space(@class),' '),' oxd-input-group ')][1]";
	private By dateInput = By.xpath("//label[contains(text(),'Date')]/" + INPUT_GROUP_ANCESTOR + "//input");
	private By timeInput = By.xpath("//label[contains(text(),'Time')]/" + INPUT_GROUP_ANCESTOR + "//input");
	private By noteTextarea = By.xpath("//textarea");
	private By punchButton = By.xpath("//button[contains(normalize-space(.),'In') or contains(normalize-space(.),'Out')]");

	// Navigation: Attendance is a dropdown menu tab, same pattern as
	// Leave's Configure menu.
	private By attendanceTab = By.xpath("//span[contains(text(),'Attendance')]");
	private By punchInOutMenuItem = By.xpath("//a[contains(normalize-space(.),'Punch In/Out')]");

	public TimePage(WebDriver driver) {
		this.actionDriver = BaseClass.getActionDriver();
	}

	// Navigates from the Time module's default landing page to
	// Attendance > Punch In/Out.
	public void navigateToPunchInOut() {
		actionDriver.click(attendanceTab);
		actionDriver.click(punchInOutMenuItem);
		actionDriver.dismissAnyOpenDropdown();
	}

	public void enterPunchNote(String note) {
		actionDriver.enterText(noteTextarea, note);
	}

	public void clickPunchButton() {
		actionDriver.click(punchButton);
	}

	public String getPunchButtonText() {
		return actionDriver.getText(punchButton);
	}

	// ---- Timesheets: View an employee's timesheet ----
	// Reuses the proven autocomplete-suggestion pattern from Admin/PIM/Leave.
	private By timesheetEmployeeNameInput = By.xpath("//label[contains(text(),'Employee Name')]/" + INPUT_GROUP_ANCESTOR + "//input");
	private By viewButton = By.xpath("//button[contains(normalize-space(.),'View')]");

	public void viewEmployeeTimesheet(String partialName, String fullName) {
		actionDriver.enterText(timesheetEmployeeNameInput, partialName);
		By suggestion = By.xpath("//div[contains(concat(' ',normalize-space(@class),' '),' oxd-autocomplete-option ')][contains(normalize-space(.),'"
				+ fullName + "')]");
		actionDriver.click(suggestion);
		actionDriver.click(viewButton);
	}

	// ---- Attendance > My Records ----
	private By myRecordsMenuItem = By.xpath("//a[contains(normalize-space(.),'My Records')]");

	public void navigateToMyRecords() {
		actionDriver.click(attendanceTab);
		actionDriver.click(myRecordsMenuItem);
		actionDriver.dismissAnyOpenDropdown();
	}

	// URL-based verification, same reliable pattern used for PIM's
	// post-save check - a URL change is a strong, low-assumption signal
	// that navigation actually succeeded.
	public boolean isOnTimesheetPage() {
		return actionDriver.getCurrentUrl().contains("viewTimesheet");
	}

	public boolean isOnMyRecordsPage() {
		return actionDriver.getCurrentUrl().toLowerCase().contains("attendance");
	}
}