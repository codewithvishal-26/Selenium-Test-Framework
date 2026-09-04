package com.orangehrm.utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.orangehrm.base.BaseClass;

public class DBConnection {

    private static final Logger logger = BaseClass.logger;

    private DBConnection() {
        // Utility class - prevent object creation
    }

    /**
     * Creates a connection to the OrangeHRM database.
     */
    public static Connection getDBConnection() {

        String dbUrl = BaseClass.getProp().getProperty("db.url");
        String dbUsername = BaseClass.getProp().getProperty("db.username");
        String dbPassword = BaseClass.getProp().getProperty("db.password");

        if (dbUrl == null || dbUrl.isBlank()) {
            throw new IllegalArgumentException(
                    "Database URL is not configured."
            );
        }

        try {

            logger.info("Starting DB Connection...");

            Connection connection =
                    DriverManager.getConnection(
                            dbUrl,
                            dbUsername,
                            dbPassword
                    );

            logger.info("DB Connection Successful");

            return connection;

        } catch (SQLException e) {

            logger.error(
                    "Error while establishing DB connection.",
                    e
            );

            throw new RuntimeException(
                    "Unable to establish database connection.",
                    e
            );
        }
    }

    /**
     * Fetches employee details from the database
     * using employee ID.
     */
    public static Map<String, String> getEmployeeDetails(
            String employeeId) {

        // Query on emp_number (the real int primary key) rather than
        // employee_id, which is stored zero-padded as a string (e.g.
        // "0001" for emp_number 1) and would never match a plain "1"
        // passed in from test data.
        String query =
                "SELECT emp_firstname, emp_middle_name, "
                + "emp_lastname "
                + "FROM hs_hr_employee "
                + "WHERE emp_number = ?";

        Map<String, String> employeeDetails =
                new HashMap<>();

        try (
                Connection connection =
                        getDBConnection();

                PreparedStatement statement =
                        connection.prepareStatement(query)
        ) {

            statement.setString(1, employeeId);

            logger.info(
                    "Executing employee query for ID: {}",
                    employeeId
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (resultSet.next()) {

                    String firstName =
                            resultSet.getString(
                                    "emp_firstname"
                            );

                    String middleName =
                            resultSet.getString(
                                    "emp_middle_name"
                            );

                    String lastName =
                            resultSet.getString(
                                    "emp_lastname"
                            );

                    employeeDetails.put(
                            "firstName",
                            firstName
                    );

                    employeeDetails.put(
                            "middleName",
                            middleName != null
                                    ? middleName
                                    : ""
                    );

                    employeeDetails.put(
                            "lastName",
                            lastName
                    );

                    logger.info(
                            "Employee data fetched successfully."
                    );

                } else {

                    logger.warn(
                            "Employee not found for ID: {}",
                            employeeId
                    );
                }
            }

        } catch (SQLException e) {

            logger.error(
                    "Error while executing employee query.",
                    e
            );

            throw new RuntimeException(
                    "Unable to fetch employee details.",
                    e
            );
        }

        return employeeDetails;
    }

    /**
     * Checks whether a leave record exists for the given employee,
     * leave type, and date. Used to verify an "Assign Leave" action
     * actually persisted - directly against the ohrm_leave table,
     * rather than relying on the UI's rendered balance text, which
     * doesn't reliably reflect a just-submitted change on the same
     * loaded page instance.
     */
    public static boolean leaveRecordExists(
            String empNumber, String leaveTypeId, String date) {

        String query =
                "SELECT COUNT(*) AS recordCount "
                + "FROM ohrm_leave "
                + "WHERE emp_number = ? "
                + "AND leave_type_id = ? "
                + "AND date = ?";

        try (
                Connection connection =
                        getDBConnection();

                PreparedStatement statement =
                        connection.prepareStatement(query)
        ) {

            statement.setString(1, empNumber);
            statement.setString(2, leaveTypeId);
            statement.setString(3, date);

            logger.info(
                    "Checking for leave record: emp_number={}, leave_type_id={}, date={}",
                    empNumber, leaveTypeId, date
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (resultSet.next()) {
                    return resultSet.getInt("recordCount") > 0;
                }
            }

        } catch (SQLException e) {

            logger.error(
                    "Error while checking leave record.",
                    e
            );

            throw new RuntimeException(
                    "Unable to check leave record.",
                    e
            );
        }

        return false;
    }
}