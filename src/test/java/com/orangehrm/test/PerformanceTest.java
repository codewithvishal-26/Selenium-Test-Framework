package com.orangehrm.test;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.pages.PerformancePage;
import com.orangehrm.utilities.ExtentManager;

public class PerformanceTest extends BaseClass {

	private LoginPage loginPage;
	private HomePage homePage;
	private PerformancePage performancePage;

	@BeforeMethod
	public void setupPages() {
		loginPage = new LoginPage(getDriver());
		homePage = new HomePage(getDriver());
		performancePage = new PerformancePage(getDriver());
	}

	private void loginAsAdmin() {
		String adminUsername = getProp().getProperty("username");
		String adminPassword = getProp().getProperty("password");
		loginPage.login(adminUsername, adminPassword);
		Assert.assertTrue(homePage.isAdminTabVisible(), "Login failed - Admin tab not visible");
	}

	// Self-contained: creates its own Job Title first, then adds a KPI
	// against it - same prerequisite pattern used for Leave Types and
	// Recruitment Job Titles.
	@Test
	public void addKpiTest() {
		loginAsAdmin();

		String uniqueJobTitle = "Test Job Title " + System.currentTimeMillis();
		performancePage.addJobTitle(uniqueJobTitle);
		ExtentManager.logStep("Created Job Title: " + uniqueJobTitle);

		performancePage.navigateToAddKpi();
		ExtentManager.logStep("Navigated to Performance > Configure > KPIs > Add");

		String uniqueKpiName = "Test KPI " + System.currentTimeMillis();
		performancePage.addKpi(uniqueKpiName, uniqueJobTitle, "1", "5");
		ExtentManager.logStep("Added KPI: " + uniqueKpiName);

		performancePage.navigateToKpiList();
		Assert.assertTrue(performancePage.verifyKpiExists(uniqueKpiName),
				"New KPI did not appear in the KPI list");
	}
}
