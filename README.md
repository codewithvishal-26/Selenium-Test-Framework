# OrangeHRM Selenium Test Automation Framework

An end-to-end **UI + API + Database** test automation framework built for [OrangeHRM](https://www.orangehrm.com/), an open-source HR Management System. Built with **Java, Selenium WebDriver, and TestNG**, following the **Page Object Model**.

**Latest full run: 28/28 tests passed ✅**

## Tech Stack

| Component | Technology |
|---|---|
| Language / Build | Java 17, Maven |
| Automation | Selenium WebDriver 4.27.0 |
| Test Framework | TestNG 7.10.2 (parallel execution, retry analyzer) |
| Design Pattern | Page Object Model + reusable `ActionDriver` |
| Reporting | ExtentReports 5.1.2, TestNG HTML/XML, screenshots on failure |
| Logging | Log4j2 |
| Data-Driven Testing | Apache POI (Excel) |
| API Testing | RestAssured 5.5.0 |
| DB Verification | MySQL (JDBC) |
| Environment | Docker Compose |
| CI/CD | Jenkinsfile |

## Modules Covered

Login, Home/Dashboard, Admin User Management, PIM, Leave, Time & Attendance, Recruitment, My Info, Performance (KPI), Directory, Maintenance, Claim, Buzz, Database Verification, and REST API checks — **16 test classes** in total.

## Project Structure

```
src/main/java/com/orangehrm/
  actiondriver/   -> reusable Selenium action wrapper
  base/           -> WebDriver setup/teardown (BaseClass)
  listeners/      -> TestNG listener (screenshots, reporting hooks)
  pages/          -> Page Object classes, one per module
  utilities/      -> Excel reader, DB connection, API utility, logging, retry logic
src/test/java/com/orangehrm/test/   -> test classes
src/test/resources/                 -> testng.xml, test data (Excel)
docker/                             -> local OrangeHRM + MySQL environment
Jenkinsfile                         -> CI pipeline definition
```

## Running the Suite

**Prerequisites:** Java 17, Maven, Chrome browser (or the Docker environment below).

```bash
# Run against configured environment
mvn clean test

# Or spin up a local OrangeHRM + MySQL instance first
docker compose -f docker/docker-compose-orangehrm-local.yml up -d
mvn clean test
```

Configuration (base URL, browser, DB credentials) lives in `src/main/resources/config.properties`.

## Reports

After a run, find results at:
- `test-output/` — TestNG HTML/XML report
- `src/test/resources/ExtentReport/ExtentReport.html` — Extent report with screenshots

## CI/CD

The included `Jenkinsfile` runs the suite as a Jenkins pipeline job and can be adapted to publish the reports above as build artifacts.

## Author

Built as a personal QA automation project to practice framework design, data-driven and API testing, database verification, containerized test environments, and CI/CD integration.
