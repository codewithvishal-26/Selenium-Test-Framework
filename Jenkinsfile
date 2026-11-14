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
                bat "docker compose -f \"${COMPOSE_FILE}\" up -d"

                echo "Waiting for Grid to report ready (polling, max ~2 min)..."
                bat '''
                    @echo off
                    set /a tries=0
                    :wait
                    curl -s http://localhost:4444/status | findstr /C:"\\"ready\\": true" >nul && goto ready
                    set /a tries+=1
                    if %tries% GEQ 40 (echo Grid not ready & exit /b 1)
                    timeout /t 3 /nobreak >nul
                    goto wait
                    :ready
                    echo Grid is ready
                '''
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
            bat "docker compose -f \"${COMPOSE_FILE}\" down"

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
