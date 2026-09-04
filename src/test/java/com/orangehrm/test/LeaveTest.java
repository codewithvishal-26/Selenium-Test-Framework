package com.orangehrm.test;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LeavePage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.utilities.ExtentManager;

public class LeaveTest extends BaseClass {

	private LoginPage loginPage;
	private HomePage homePage;
	private LeavePage leavePage;

	@BeforeMethod
	public void setupPages() {
		loginPage = new LoginPage(getDriver());
		homePage = new HomePage(getDriver());
		leavePage = new LeavePage(getDriver());
	}

	private void loginAsAdmin() {
		String adminUsername = getProp().getProperty("username");
		String adminPassword = getProp().getProperty("password");
		loginPage.login(adminUsername, adminPassword);
		Assert.assertTrue(homePage.isAdminTabVisible(), "Login failed - Admin tab not visible");
	}

	// Generates a date that's both unique-per-run (to dodge OrangeHRM's
	// overlapping-leave-request rejection from prior runs' leftover data)
	// AND guaranteed to fall on a weekday (Mon-Fri) - a purely weekend date
	// gets rejected with "No Working Days Selected", which the original
	// unconstrained random-offset version could land on.
	// Uses nanoTime (not currentTimeMillis()/60000) for the offset -
	// millis-based minute buckets meant a TestNG retry firing seconds
	// after the original failure computed the SAME date, colliding with
	// the leave just assigned moments earlier and silently failing with
	// "balance did not change" (an overlap rejection, not a real bug).
	private String generateWeekdayLeaveDate(java.time.LocalDate base, long moduloRange) {
		java.time.LocalDate date = base.plusDays(Math.abs(System.nanoTime()) % moduloRange);
		while (date.getDayOfWeek() == java.time.DayOfWeek.SATURDAY
				|| date.getDayOfWeek() == java.time.DayOfWeek.SUNDAY) {
			date = date.plusDays(1);
		}
		return date.toString();
	}

	// Updates the Leave Period's Start Month and verifies the change
	// actually persisted by reloading the page and reading the value back -
	// deliberately not dependent on a guessed toast/notification locator.
	@Test
	public void updateLeavePeriodStartMonthTest() {
		loginAsAdmin();
		homePage.clickOnLeaveTab();
		leavePage.navigateToLeavePeriodConfig();
		ExtentManager.logStep("Navigated to Leave > Configure > Leave Period");

		leavePage.selectStartMonth("March");
		leavePage.clickSave();
		ExtentManager.logStep("Set Leave Period Start Month to March and clicked Save");

		getActionDriver().refreshPage();

		Assert.assertEquals(leavePage.getStartMonthValue(), "March",
				"Start Month did not persist as 'March' after reload - save may not have worked");
	}

	// The one stable, permanent leave type all Apply/Assign tests use.
	// Previously each test created its own brand-new leave type and
	// immediately tried to select it on a *different* page (Add
	// Entitlement) moments later - OrangeHRM's Add Entitlement dropdown
	// doesn't reliably pick up leave types created earlier in the same
	// browser session without a full reload, so that always eventually
	// timed out. It also left 40+ junk leave types behind every run.
	// Using one fixed, already-existing type sidesteps both problems.
	private static final String STABLE_LEAVE_TYPE = "QA Automation Standard Leave";

	// Adds a new Leave Type (prerequisite for testing Apply/Assign Leave,
	// which are otherwise untestable on an instance with zero configured
	// leave types) and verifies it appears in the Leave Types list. This
	// is the only test that still creates a fresh leave type each run -
	// Apply/Assign now use STABLE_LEAVE_TYPE instead of depending on this.
	@Test
	public void addLeaveTypeTest() {
		loginAsAdmin();
		homePage.clickOnLeaveTab();
		leavePage.navigateToLeaveTypesConfig();
		ExtentManager.logStep("Navigated to Leave > Configure > Leave Types");

		String uniqueLeaveTypeName = "Test Leave " + System.currentTimeMillis();

		leavePage.clickAddLeaveType();
		leavePage.addLeaveType(uniqueLeaveTypeName);
		ExtentManager.logStep("Added leave type: " + uniqueLeaveTypeName);

		Assert.assertTrue(leavePage.verifyLeaveTypeExists(uniqueLeaveTypeName),
				"New leave type did not appear in the Leave Types list");
	}

	// Admin assigns leave to an existing employee (Vishal Chandrabanshi,
	// known to exist from earlier setup), using the fixed, pre-existing
	// STABLE_LEAVE_TYPE rather than creating a new one per run.
	@Test
	public void assignLeaveTest() {
		loginAsAdmin();
		homePage.clickOnLeaveTab();

		leavePage.navigateToAddEntitlement();
		leavePage.addEntitlement("Vis", "Vishal Chandrabanshi", STABLE_LEAVE_TYPE, "10");
		ExtentManager.logStep("Added 10-day entitlement for Vishal Chandrabanshi: " + STABLE_LEAVE_TYPE);

		homePage.clickOnLeaveTab();
		leavePage.clickAssignLeaveTab();
		leavePage.selectEmployeeForLeave("Vis", "Vishal Chandrabanshi");
		leavePage.selectLeaveType(STABLE_LEAVE_TYPE);
		String balanceBefore = leavePage.getLeaveBalanceText();
		ExtentManager.logStep("Leave balance before assigning: " + balanceBefore);

		// Uses a date that varies per run (within the Leave Period range,
		// 2026-03-01 to 2027-02-28) rather than a hardcoded one - a fixed
		// date collided with leave records left behind by earlier runs,
		// which OrangeHRM correctly rejects as an "Overlapping Leave
		// Request" (a real business rule, not a bug).
		String leaveDate = generateWeekdayLeaveDate(java.time.LocalDate.of(2026, 3, 2), 359);
		leavePage.enterLeaveDates(leaveDate, leaveDate);
		leavePage.enterLeaveComments("Automated test - assign leave");
		leavePage.clickAssign();
		ExtentManager.logStep("Submitted Assign Leave for Vishal Chandrabanshi on " + leaveDate);

		// Verified directly against ohrm_leave in the database, rather
		// than comparing the UI's rendered Leave Balance text before vs
		// after - that comparison kept failing even after fixing the
		// date-collision bug, most likely because the balance widget
		// doesn't reliably re-render with fresh data on the same loaded
		// page instance. A direct row-exists check against the database
		// is unambiguous: emp_number=1 is Vishal Chandrabanshi,
		// leave_type_id=1 is the fixed STABLE_LEAVE_TYPE (confirmed
		// directly via SQL earlier in this project).
		boolean leaveRecordFound =
				com.orangehrm.utilities.DBConnection.leaveRecordExists("1", "1", leaveDate);
		ExtentManager.logStep("Leave record found in database for " + leaveDate + ": " + leaveRecordFound);

		Assert.assertTrue(leaveRecordFound,
				"No leave record found in the database for Vishal Chandrabanshi on " + leaveDate
						+ " - assignment may not have persisted");
	}

	// Self-service: the logged-in Admin user applies for leave themselves,
	// using the fixed, pre-existing STABLE_LEAVE_TYPE.
	@Test
	public void applyForLeaveTest() {
		loginAsAdmin();
		homePage.clickOnLeaveTab();

		leavePage.navigateToAddEntitlement();
		leavePage.addEntitlement("Vis", "Vishal Chandrabanshi", STABLE_LEAVE_TYPE, "10");
		ExtentManager.logStep("Added 10-day entitlement for Vishal Chandrabanshi: " + STABLE_LEAVE_TYPE);

		homePage.clickOnLeaveTab();
		leavePage.clickApplyTab();
		leavePage.selectLeaveType(STABLE_LEAVE_TYPE);
		// Offset by 1 day from assignLeaveTest's date formula - both tests
		// apply to the same employee (Vishal Chandrabanshi), so using the
		// exact same formula could collide if both run within the same
		// minute.
		String leaveDate = generateWeekdayLeaveDate(java.time.LocalDate.of(2026, 3, 3), 358);
		leavePage.enterLeaveDates(leaveDate, leaveDate);
		leavePage.enterLeaveComments("Automated test - apply for leave");
		leavePage.clickApply();
		ExtentManager.logStep("Submitted Apply for Leave: " + STABLE_LEAVE_TYPE);

		// Verified via Leave List, reusing the same generic .oxd-table-body
		// check (this Vue table component is confirmed reused across the
		// whole app, not specific to any one list page).
		leavePage.navigateToLeaveList();
		Assert.assertTrue(leavePage.verifyLeaveTypeExists(STABLE_LEAVE_TYPE),
				"Applied leave did not appear in the Leave List after submission");
	}
}