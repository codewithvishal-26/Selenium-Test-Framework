package com.orangehrm.utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReaderUtility {

    private ExcelReaderUtility() {
        // Utility class - prevent object creation
    }

    /**
     * Reads all test data from the requested Excel sheet.
     * The first row is treated as the header and skipped.
     */
    public static List<String[]> getSheetData(
            String filePath,
            String sheetName) {

        List<String[]> data = new ArrayList<>();

        try (
                FileInputStream fis =
                        new FileInputStream(filePath);

                Workbook workbook =
                        new XSSFWorkbook(fis)
        ) {

            Sheet sheet =
                    workbook.getSheet(sheetName);

            if (sheet == null) {

                throw new IllegalArgumentException(
                        "Excel sheet does not exist: "
                        + sheetName
                );
            }

            // Skip header row
            for (int rowIndex = 1;
                 rowIndex <= sheet.getLastRowNum();
                 rowIndex++) {

                Row row =
                        sheet.getRow(rowIndex);

                if (row == null) {
                    continue;
                }

                List<String> rowData =
                        new ArrayList<>();

                for (int cellIndex = 0;
                     cellIndex < row.getLastCellNum();
                     cellIndex++) {

                    Cell cell =
                            row.getCell(cellIndex);

                    rowData.add(
                            getCellValue(cell)
                    );
                }

                data.add(
                        rowData.toArray(new String[0])
                );
            }

        } catch (IOException e) {

            throw new RuntimeException(
                    "Unable to read Excel file: "
                    + filePath,
                    e
            );
        }

        return data;
    }

    /**
     * Converts an Excel cell into String format.
     */
    private static String getCellValue(Cell cell) {

        if (cell == null) {
            return "";
        }

        DataFormatter formatter =
                new DataFormatter();

        return formatter.formatCellValue(cell);
    }
}