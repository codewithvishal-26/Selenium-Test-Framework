package com.orangehrm.test;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.pages.RecruitmentPage;
import com.orangehrm.utilities.ExtentManager;

public class RecruitmentTest extends BaseClass {

	private LoginPage loginPage;
	private HomePage homePage;
	private RecruitmentPage recruitmentPage;

	@BeforeMethod
	public void setupPages() {
		loginPage = new LoginPage(getDriver());
		homePage = new HomePage(getDriver());
		recruitmentPage = new RecruitmentPage(getDriver());
	}

	private void loginAsAdmin() {
		String adminUsername = getProp().getProperty("username");
		String adminPassword = getProp().getProperty("password");
		loginPage.login(adminUsername, adminPassword);
		Assert.assertTrue(homePage.isAdminTabVisible(), "Login failed - Admin tab not visible");
	}

	@Test
	public void addVacancyTest() {
		loginAsAdmin();

		String uniqueJobTitle = "Test Job Title " + System.currentTimeMillis();
		recruitmentPage.addJobTitle(uniqueJobTitle);
		ExtentManager.logStep("Created Job Title: " + uniqueJobTitle);

		homePage.clickOnRecruitmentTab();
		recruitmentPage.clickVacanciesTab();
		recruitmentPage.clickAddVacancy();
		ExtentManager.logStep("Navigated to Recruitment > Vacancies > Add Vacancy");

		String uniqueVacancyName = "Test Vacancy " + System.currentTimeMillis();
		recruitmentPage.addVacancy(uniqueVacancyName, uniqueJobTitle, "Vis", "Vishal Chandrabanshi", "2");
		ExtentManager.logStep("Added vacancy: " + uniqueVacancyName);

		homePage.clickOnRecruitmentTab();
		recruitmentPage.clickVacanciesTab();
		Assert.assertTrue(recruitmentPage.verifyVacancyExists(uniqueVacancyName),
				"New vacancy did not appear in the Vacancies list");
	}

	// Self-contained: creates its own Job Title + Vacancy first, then adds
	// a Candidate against that exact vacancy - same pattern used for
	// Leave's Assign Leave test (create the prerequisite, then use it).
	@Test
	public void addCandidateTest() {
		loginAsAdmin();

		String uniqueJobTitle = "Test Job Title " + System.currentTimeMillis();
		recruitmentPage.addJobTitle(uniqueJobTitle);
		ExtentManager.logStep("Created Job Title: " + uniqueJobTitle);

		homePage.clickOnRecruitmentTab();
		recruitmentPage.clickVacanciesTab();
		recruitmentPage.clickAddVacancy();
		String uniqueVacancyName = "Test Vacancy " + System.currentTimeMillis();
		recruitmentPage.addVacancy(uniqueVacancyName, uniqueJobTitle, "Vis", "Vishal Chandrabanshi", "2");
		ExtentManager.logStep("Created Vacancy: " + uniqueVacancyName);

		homePage.clickOnRecruitmentTab();
		recruitmentPage.clickCandidatesTab();
		recruitmentPage.clickAddCandidate();
		ExtentManager.logStep("Navigated to Recruitment > Candidates > Add Candidate");

		String uniqueLastName = "Candidate" + System.currentTimeMillis();
		recruitmentPage.addCandidate("Test", uniqueLastName, uniqueVacancyName,
				"test." + System.currentTimeMillis() + "@example.com", "9876543210");
		ExtentManager.logStep("Added candidate: Test " + uniqueLastName);

		homePage.clickOnRecruitmentTab();
		recruitmentPage.clickCandidatesTab();
		Assert.assertTrue(recruitmentPage.verifyCandidateExists("Test", uniqueLastName),
				"New candidate did not appear in the Candidates list");
	}
}
