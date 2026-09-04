package com.orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.base.BaseClass;

public class PerformancePage {

	private ActionDriver actionDriver;

	private static final String INPUT_GROUP_ANCESTOR = "ancestor::div[contains(concat(' ',normalize-space(@class),' '),' oxd-input-group ')][1]";

	// ---- Admin > Job > Job Titles (prerequisite - same pattern used in
	// RecruitmentPage, duplicated here rather than shared given the
	// project's existing per-page convention) ----
	private By adminTab = By.xpath("//span[text()='Admin']");
	private By jobTab = By.xpath("//span[contains(text(),'Job')]");
	private By jobTitlesMenuItem = By.xpath("//a[contains(normalize-space(.),'Job Titles')]");
	private By addJobTitleButton = By.xpath("//button[contains(normalize-space(.),'Add')]");
	private By jobTitleNameInput = By.xpath("//label[contains(text(),'Job Title')]/" + INPUT_GROUP_ANCESTOR + "//input");
	private By saveButton = By.xpath("//button[contains(normalize-space(.),'Save')]");

	public PerformancePage(WebDriver driver) {
		this.actionDriver = BaseClass.getActionDriver();
	}

	public void addJobTitle(String jobTitleName) {
		actionDriver.click(adminTab);
		actionDriver.click(jobTab);
		actionDriver.click(jobTitlesMenuItem);
		actionDriver.dismissAnyOpenDropdown();
		actionDriver.click(addJobTitleButton);
		actionDriver.enterText(jobTitleNameInput, jobTitleName);
		actionDriver.click(saveButton);
		actionDriver.clickIfPresent(By.xpath("//button[contains(normalize-space(.),'Confirm')]"), 5);
		actionDriver.waitForInvisibility(By.className("oxd-toast-container"));
	}

	// ---- Performance > Configure > KPIs > Add ----
	private By performanceTab = By.xpath("//span[text()='Performance']");
	private By configureTab = By.xpath("//span[contains(text(),'Configure')]");
	private By kpisMenuItem = By.xpath("//a[contains(normalize-space(.),'KPIs')]");
	private By addKpiButton = By.xpath("//button[contains(normalize-space(.),'Add')]");
	private By kpiNameInput = By.xpath("//label[contains(text(),'Key Performance Indicator')]/" + INPUT_GROUP_ANCESTOR + "//input");
	private By kpiJobTitleDropdown = By.xpath("//label[contains(text(),'Job Title')]/" + INPUT_GROUP_ANCESTOR + "//div[contains(@class,'oxd-select-text')]");
	private By minRatingInput = By.xpath("//label[contains(text(),'Minimum Rating')]/" + INPUT_GROUP_ANCESTOR + "//input");
	private By maxRatingInput = By.xpath("//label[contains(text(),'Maximum Rating')]/" + INPUT_GROUP_ANCESTOR + "//input");
	private By kpiTableBody = By.className("oxd-table-body");

	public void navigateToKpiList() {
		actionDriver.click(performanceTab);
		actionDriver.click(configureTab);
		actionDriver.click(kpisMenuItem);
		actionDriver.dismissAnyOpenDropdown();
	}

	public void navigateToAddKpi() {
		navigateToKpiList();
		actionDriver.click(addKpiButton);
	}

	public void selectKpiJobTitle(String jobTitle) {
		actionDriver.click(kpiJobTitleDropdown);
		By option = By.xpath("//div[@role='listbox']//span[text()='" + jobTitle + "']");
		actionDriver.click(option);
	}

	public void addKpi(String kpiName, String jobTitle, String minRating, String maxRating) {
		actionDriver.enterText(kpiNameInput, kpiName);
		selectKpiJobTitle(jobTitle);
		actionDriver.enterText(minRatingInput, minRating);
		actionDriver.enterText(maxRatingInput, maxRating);
		actionDriver.click(saveButton);
		actionDriver.clickIfPresent(By.xpath("//button[contains(normalize-space(.),'Confirm')]"), 5);
		actionDriver.waitForInvisibility(By.className("oxd-toast-container"));
	}

	public boolean verifyKpiExists(String kpiName) {
		String actualNoSpaces = actionDriver.getText(kpiTableBody).replaceAll("\\s+", "");
		String expectedNoSpaces = kpiName.replaceAll("\\s+", "");
		return actualNoSpaces.contains(expectedNoSpaces);
	}
}
