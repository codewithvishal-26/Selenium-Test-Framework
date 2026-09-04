package com.orangehrm.utilities;

import java.util.List;

import org.testng.annotations.DataProvider;

public final class DataProviders {

    private static final String FILE_PATH =
            System.getProperty("user.dir")
                    + "/src/test/resources/testdata/TestData.xlsx";

    private DataProviders() {
        // Utility class - prevent object creation
    }

    @DataProvider(name = "validLoginData")
    public static Object[][] validLoginData() {
        return getSheetData("validLoginData");
    }

    @DataProvider(name = "inValidLoginData")
    public static Object[][] invalidLoginData() {
        return getSheetData("inValidLoginData");
    }

    @DataProvider(name = "emplVerification")
    public static Object[][] employeeVerification() {
        return getSheetData("emplVerfication");
    }

    private static Object[][] getSheetData(String sheetName) {

        List<String[]> sheetData =
                ExcelReaderUtility.getSheetData(
                        FILE_PATH,
                        sheetName
                );

        if (sheetData == null || sheetData.isEmpty()) {
            throw new IllegalArgumentException(
                    "No test data found for sheet: " + sheetName
            );
        }

        Object[][] data =
                new Object[sheetData.size()][];

        for (int i = 0; i < sheetData.size(); i++) {

            data[i] = sheetData.get(i);
        }

        return data;
    }
}