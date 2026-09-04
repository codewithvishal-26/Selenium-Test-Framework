package com.orangehrm.test;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.pages.PIMPage;
import com.orangehrm.utilities.DBConnection;
import com.orangehrm.utilities.DataProviders;
import com.orangehrm.utilities.ExtentManager;

public class DBVerificationTest extends BaseClass {
	
	private LoginPage loginPage;
	private HomePage homePage;
	private PIMPage pimPage;
	
	@BeforeMethod
	public void setupPages() {
		loginPage = new LoginPage(getDriver());
		homePage  = new HomePage(getDriver());
		pimPage   = new PIMPage(getDriver());
	}
	
	@Test(dataProvider="emplVerification", dataProviderClass = DataProviders.class)
	public void verifyEmployeeNameVerificationFromDB(String emplID, String empName) {
		
		SoftAssert softAssert = getSoftAssert();
		
		ExtentManager.logStep("Logging with Admin Credentails");
		loginPage.login(prop.getProperty("username"), prop.getProperty("password"));
		
		ExtentManager.logStep("click on PIM tab");
		homePage.clickOnPIMTab();
		
		ExtentManager.logStep("Search for Employee");
		// Reuses PIMPage.searchEmployeeByName - the same verified
		// oxd-autocomplete selection already proven working by PIMTest,
		// instead of the separate (and stale) locators that used to live
		// in HomePage for this.
		pimPage.searchEmployeeByName(empName, empName);
		staticWait(1);
		
		ExtentManager.logStep("Get the Employee Name from DB");
		String employee_id=emplID;
		
		//Fetch the data into a map
		
		Map<String,String> employeeDetails = DBConnection.getEmployeeDetails(employee_id);
		
		String emplFirstName = employeeDetails.get("firstName");
		String emplMiddleName = employeeDetails.get("middleName");
		String emplLastName = employeeDetails.get("lastName");
		
		String emplFullName = (emplFirstName + " " + emplMiddleName + " " + emplLastName)
				.replaceAll("\\s+", " ").trim();
		
		// Combined into a single full-name check against the results
		// table's raw text content (PIMPage strips all whitespace before
		// comparing, since adjacent cells concatenate with no space in
		// the raw DOM text) - more robust than indexing individual table
		// cells, which don't have a verified, stable position on this
		// page.
		ExtentManager.logStep("Verify the employee name from DB matches the search result: " + emplFullName);
		softAssert.assertTrue(pimPage.verifySearchResultContainsEmployee(emplFullName),
				"Employee name from DB (" + emplFullName + ") was not found in the PIM search result");
		
		ExtentManager.logStep("DB Validation Completed");
		
		softAssert.assertAll();

	}

}