// NOTE: This pipeline uses Windows `bat` steps, so it requires a Windows
// Jenkins agent with Docker Desktop. For a Linux agent, replace each `bat`
// with `sh` and the backslash path with a forward slash.
pipeline {

    agent any

    tools {
        maven 'maven-3.9.9'
    }

    environment {
        COMPOSE_FILE  = "${WORKSPACE}\\docker\\docker-compose.yml"
        SELENIUM_GRID = "true"
        RETRY_COUNT   = "2"
    }

    stages {

        stage('Checkout') {
            steps {
                echo "Checking out automation framework..."
                git(
                    branch: 'main',
                    url: 'https://github.com/codewithvishal-26/Selenium-Test-Framework.git'
                )
            }
        }

        stage('Start Selenium Grid') {
            steps {
                echo "Starting Selenium Grid..."
                bat "docker compose -p selenium-grid -f \"${COMPOSE_FILE}\" up -d"

                echo "Waiting for Grid to report ready (max 3 min)..."
                // waitUntil retries with a growing pause between tries.
                // (Windows `timeout` fails under Jenkins: "Input redirection
                // is not supported", so the retry logic lives in Groovy.)
                script {
                    timeout(time: 3, unit: 'MINUTES') {
                        waitUntil(initialRecurrencePeriod: 3000) {
                            def code = bat(
                                returnStatus: true,
                                script: '@curl -s http://localhost:4444/status | findstr /R /C:"ready.: true"'
                            )
                            return code == 0
                        }
                    }
                }
                echo "Grid is ready"
            }
        }

        stage('Build') {
            steps {
                echo "Compiling automation framework..."
                bat "mvn clean compile -DseleniumGrid=${SELENIUM_GRID}"
            }
        }

        stage('Test') {
            steps {
                echo "Executing TestNG automation suite..."
                bat "mvn test -DseleniumGrid=${SELENIUM_GRID} -DretryCount=${RETRY_COUNT}"
            }
        }
    }

    post {

        always {
            echo "Stopping Selenium Grid..."
            bat "docker compose -p selenium-grid -f \"${COMPOSE_FILE}\" down"

            // Publish even when tests fail, so the report is always available.
            // allowMissing is false: a missing report means something is wrong.
            publishHTML(
                target: [
                    reportDir: 'target/reports',
                    reportFiles: 'ExtentReport.html',
                    reportName: 'OrangeHRM Extent Report',
                    keepAll: true,
                    alwaysLinkToLastBuild: true,
                    allowMissing: false
                ]
            )

            archiveArtifacts(
                artifacts: 'target/reports/*.html, target/screenshots/*.png',
                fingerprint: true,
                allowEmptyArchive: true   // screenshots only exist on failures
            )

            // allowEmptyResults is false: no JUnit XML means the run itself broke.
            junit(
                testResults: 'target/surefire-reports/*.xml',
                allowEmptyResults: false
            )
        }

        success {
            echo "Automation execution completed successfully."
        }

        failure {
            echo "Automation execution failed. Please check Jenkins logs and reports."
        }
    }
}