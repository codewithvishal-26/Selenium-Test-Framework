# OrangeHRM Test Automation Framework

Selenium WebDriver + Java + TestNG + Maven framework (Page Object Model) that automates real workflows of [OrangeHRM](https://www.orangehrm.com/), an open-source HR system with a dynamic Vue.js UI (custom dropdowns, autocompletes, modals).

## Coverage
16 test classes, 30 `@Test` methods:

| Module | What is automated |
|---|---|
| Login / Home / Dashboard | valid + invalid login, page load checks |
| Admin | add system user, invalid-employee validation, search by username |
| PIM | add employee, search employee |
| Leave | leave period, leave type, entitlement, assign leave, apply leave |
| Time | punch in/out, timesheet, my records |
| Recruitment | job title -> vacancy -> candidate |
| My Info | update personal details |
| Performance | add KPI |
| Directory, Claim, Buzz, Maintenance | search / create / admin re-auth flows |
| API (REST Assured) | GET request + status/field validation |
| DB (JDBC, optional) | DB verification (excluded unless configured) |

## Architecture
```
src/main/java/com/orangehrm/
  base/         BaseClass: ThreadLocal WebDriver + ActionDriver, Chrome/Firefox/Edge, local or Selenium Grid
  actiondriver/ ActionDriver: explicit-wait click/type/verify layer (fail-fast)
  pages/        Page Objects (one per module)
  listeners/    TestListener: Extent reporting, screenshots, retry hook
  utilities/    ExtentManager, RetryAnalyzer, ApiUtility, DBConnection, Excel/DataProviders, Logger
src/test/java/.../test/   test classes
src/test/resources/testng.xml
docker/         Selenium Grid + optional local OrangeHRM compose files
Jenkinsfile     Windows-agent pipeline
```
Design points:
- **ThreadLocal driver** - each thread gets its own browser session.
- **Explicit waits only** - implicit wait is disabled to avoid compounding waits.
- **Fail-fast actions** - click/type failures throw immediately instead of being swallowed.
- **Robust locators** - `contains()`/`normalize-space()` and exact class-token matching, built from verified DOM.
- **Self-contained tests** - tests create their own prerequisite data with unique values and weekday-only dates.

## Run
Requirements: JDK 17+, Maven, Chrome, a running OrangeHRM (default `http://localhost:8081`, see `docker/docker-compose-orangehrm-local.yml`).

1. Edit `src/main/resources/config.properties` (URL, username, password).
2. Run: `mvn clean test`
3. Reports: `target/reports/ExtentReport.html`, failure screenshots in `target/screenshots/`.

Grid mode: start `docker/docker-compose.yml`, then `mvn clean test -DseleniumGrid=true`.
Grid flag priority: `-DseleniumGrid` > env `SELENIUM_GRID` > `config.properties`. Note the pom sets a default `seleniumGrid=false`, so under Maven use `-D` to turn it on.

## Known limitations (honest list)
- `parallel="tests"` in testng.xml has no effect with a single `<test>` block; the suite effectively runs sequentially. True parallelism needs independent tests (`parallel="classes"`).
- Tests create data and do not clean it up; the punch-in/out test depends on the account's current state.
- A few short `staticWait()` calls remain (Login, Admin, DB tests).
- `DBVerificationTest` needs real `db.*` values in config.properties.
- `config.properties` holds demo credentials for a local instance - use env vars/Jenkins credentials for anything shared.
- Jenkinsfile needs a Windows agent; not yet verified end to end.
- Pin the OrangeHRM image tag in the compose file before relying on it.
- Locators target one OrangeHRM version and may need updates for another.

## Planned improvements
`BasePage` with injected driver, splitting `ActionDriver` (waits vs actions), test data cleanup, parallel + cross-browser matrix, API POST/PUT/DELETE tests.
