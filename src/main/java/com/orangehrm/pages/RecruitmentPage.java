package com.orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.base.BaseClass;

public class RecruitmentPage {

	private ActionDriver actionDriver;

	private static final String INPUT_GROUP_ANCESTOR = "ancestor::div[contains(concat(' ',normalize-space(@class),' '),' oxd-input-group ')][1]";

	// ---- Vacancies > Add Vacancy ----
	// Verified via console: Vacancy Name, Description, Hiring Manager,
	// Number of Positions all confirmed with real labels. Job Title is a
	// custom dropdown (oxd-select-text pattern, same as elsewhere in app).
	private By vacanciesTab = By.xpath("//a[contains(normalize-space(.),'Vacancies')]");
	private By addVacancyButton = By.xpath("//button[contains(normalize-space(.),'Add')]");
	private By vacancyNameInput = By.xpath("//label[contains(text(),'Vacancy Name')]/" + INPUT_GROUP_ANCESTOR + "//input");
	private By jobTitleDropdown = By.xpath("//label[contains(text(),'Job Title')]/" + INPUT_GROUP_ANCESTOR + "//div[contains(@class,'oxd-select-text')]");
	private By descriptionTextarea = By.xpath("//textarea");
	private By hiringManagerInput = By.xpath("//label[contains(text(),'Hiring Manager')]/" + INPUT_GROUP_ANCESTOR + "//input");
	private By numberOfPositionsInput = By.xpath("//label[contains(text(),'Number of Positions')]/" + INPUT_GROUP_ANCESTOR + "//input");
	private By saveButton = By.xpath("//button[contains(normalize-space(.),'Save')]");
	private By vacanciesTableBody = By.className("oxd-table-body");

	public RecruitmentPage(WebDriver driver) {
		this.actionDriver = BaseClass.getActionDriver();
	}

	public void clickVacanciesTab() {
		actionDriver.click(vacanciesTab);
	}

	public void clickAddVacancy() {
		actionDriver.click(addVacancyButton);
	}

	// NOT YET VERIFIED: whether any Job Title options exist. If this is
	// empty (no Job Titles configured), selecting one will fail the same
	// way Leave Types did before we added a prerequisite step - check this
	// first if selectJobTitle() fails.
	public void selectJobTitle(String jobTitle) {
		actionDriver.click(jobTitleDropdown);
		By option = By.xpath("//div[@role='listbox']//span[text()='" + jobTitle + "']");
		actionDriver.click(option);
	}

	public void selectHiringManager(String partialName, String fullName) {
		actionDriver.enterText(hiringManagerInput, partialName);
		By suggestion = By.xpath("//div[contains(concat(' ',normalize-space(@class),' '),' oxd-autocomplete-option ')][contains(normalize-space(.),'"
				+ fullName + "')]");
		actionDriver.click(suggestion);
	}

	public void addVacancy(String vacancyName, String jobTitle, String hiringManagerPartial, String hiringManagerFull, String numberOfPositions) {
		actionDriver.enterText(vacancyNameInput, vacancyName);
		selectJobTitle(jobTitle);
		selectHiringManager(hiringManagerPartial, hiringManagerFull);
		if (numberOfPositions != null) {
			actionDriver.enterText(numberOfPositionsInput, numberOfPositions);
		}
		actionDriver.click(saveButton);
		actionDriver.clickIfPresent(By.xpath("//button[contains(normalize-space(.),'Confirm')]"), 5);
		actionDriver.waitForInvisibility(By.className("oxd-toast-container"));
	}

	public boolean verifyVacancyExists(String vacancyName) {
		String actualNoSpaces = actionDriver.getText(vacanciesTableBody).replaceAll("\\s+", "");
		String expectedNoSpaces = vacancyName.replaceAll("\\s+", "");
		return actualNoSpaces.contains(expectedNoSpaces);
	}

	// ---- Admin > Job > Job Titles (prerequisite for Vacancy's Job Title
	// dropdown, which is empty on a fresh instance - same pattern as Leave
	// Types being a prerequisite for Assign Leave) ----
	private By adminTab = By.xpath("//span[text()='Admin']");
	private By jobTab = By.xpath("//span[contains(text(),'Job')]");
	private By jobTitlesMenuItem = By.xpath("//a[contains(normalize-space(.),'Job Titles')]");
	private By addJobTitleButton = By.xpath("//button[contains(normalize-space(.),'Add')]");
	private By jobTitleInput = By.xpath("//label[contains(text(),'Job Title')]/" + INPUT_GROUP_ANCESTOR + "//input");

	public void addJobTitle(String jobTitleName) {
		actionDriver.click(adminTab);
		actionDriver.click(jobTab);
		actionDriver.click(jobTitlesMenuItem);
		actionDriver.dismissAnyOpenDropdown();
		actionDriver.click(addJobTitleButton);
		actionDriver.enterText(jobTitleInput, jobTitleName);
		actionDriver.click(saveButton);
		actionDriver.clickIfPresent(By.xpath("//button[contains(normalize-space(.),'Confirm')]"), 5);
		actionDriver.waitForInvisibility(By.className("oxd-toast-container"));
	}

	// ---- Candidates > Add Candidate ----
	private By candidatesTab = By.xpath("//a[contains(normalize-space(.),'Candidates')]");
	private By addCandidateButton = By.xpath("//button[contains(normalize-space(.),'Add')]");
	private By candidateFirstNameInput = By.name("firstName");
	private By candidateMiddleNameInput = By.name("middleName");
	private By candidateLastNameInput = By.name("lastName");
	private By candidateVacancyDropdown = By.xpath("//label[contains(text(),'Vacancy')]/" + INPUT_GROUP_ANCESTOR + "//div[contains(@class,'oxd-select-text')]");
	private By candidateEmailInput = By.xpath("//label[contains(text(),'Email')]/" + INPUT_GROUP_ANCESTOR + "//input");
	private By candidateContactNumberInput = By.xpath("//label[contains(text(),'Contact Number')]/" + INPUT_GROUP_ANCESTOR + "//input");
	private By candidatesTableBody = By.className("oxd-table-body");

	public void clickCandidatesTab() {
		actionDriver.click(candidatesTab);
	}

	public void clickAddCandidate() {
		actionDriver.click(addCandidateButton);
	}

	public void selectVacancyForCandidate(String vacancyName) {
		actionDriver.click(candidateVacancyDropdown);
		By option = By.xpath("//div[@role='listbox']//span[text()='" + vacancyName + "']");
		actionDriver.click(option);
	}

	public void addCandidate(String firstName, String lastName, String vacancyName, String email, String contactNumber) {
		actionDriver.enterText(candidateFirstNameInput, firstName);
		actionDriver.enterText(candidateLastNameInput, lastName);
		selectVacancyForCandidate(vacancyName);
		actionDriver.enterText(candidateEmailInput, email);
		actionDriver.enterText(candidateContactNumberInput, contactNumber);
		actionDriver.click(saveButton);
		actionDriver.clickIfPresent(By.xpath("//button[contains(normalize-space(.),'Confirm')]"), 5);
		actionDriver.waitForInvisibility(By.className("oxd-toast-container"));
	}

	public boolean verifyCandidateExists(String firstName, String lastName) {
		String actualNoSpaces = actionDriver.getText(candidatesTableBody).replaceAll("\\s+", "");
		String expectedNoSpaces = (firstName + lastName).replaceAll("\\s+", "");
		return actualNoSpaces.contains(expectedNoSpaces);
	}
}