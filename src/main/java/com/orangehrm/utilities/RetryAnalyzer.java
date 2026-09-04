package com.orangehrm.utilities;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

    private int retryCount = 0;

    private final int maxRetryCount;

    public RetryAnalyzer() {

        maxRetryCount = Integer.parseInt(
                System.getProperty("retryCount", "2")
        );
    }

    @Override
    public boolean retry(ITestResult result) {

        if (retryCount < maxRetryCount) {

            retryCount++;

            return true;
        }

        return false;
    }
}