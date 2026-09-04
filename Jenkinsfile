pipeline {

    agent any

    tools {
        maven 'maven-3.9.9'
    }

    environment {

        COMPOSE_PATH = "${WORKSPACE}/docker"

        SELENIUM_GRID = "true"

        RETRY_COUNT = "2"
    }

    stages {

        stage('Checkout') {

            steps {

                echo "Checking out automation framework..."

                git(
                    branch: 'main',
                    url: 'https://github.com/hverma22/Selenium-Test-Framework.git'
                )
            }
        }

        stage('Start Selenium Grid') {

            steps {

                script {

                    echo "Starting Selenium Grid..."

                    bat """
                        docker compose -f "${COMPOSE_PATH}\\docker-compose.yml" up -d
                    """

                    echo "Waiting for Selenium Grid..."

                    sleep time: 30, unit: 'SECONDS'
                }
            }
        }

        stage('Build') {

            steps {

                echo "Compiling automation framework..."

                bat """
                    mvn clean compile
                    -DseleniumGrid=${SELENIUM_GRID}
                """
            }
        }

        stage('Test') {

            steps {

                echo "Executing TestNG automation suite..."

                bat """
                    mvn test
                    -DseleniumGrid=${SELENIUM_GRID}
                    -DretryCount=${RETRY_COUNT}
                """
            }
        }

        stage('Publish Reports') {

            steps {

                echo "Publishing Extent Report..."

                publishHTML(
                    target: [
                        reportDir: 'src/test/resources/ExtentReport',
                        reportFiles: 'ExtentReport.html',
                        reportName: 'OrangeHRM Extent Report',
                        keepAll: true,
                        alwaysLinkToLastBuild: true,
                        allowMissing: true
                    ]
                )
            }
        }
    }

    post {

        always {

            echo "Stopping Selenium Grid..."

            bat """
                docker compose -f "${COMPOSE_PATH}\\docker-compose.yml" down
            """

            echo "Archiving test reports..."

            archiveArtifacts(
                artifacts: 'src/test/resources/ExtentReport/*.html',
                fingerprint: true,
                allowEmptyArchive: true
            )

            junit(
                testResults: 'target/surefire-reports/*.xml',
                allowEmptyResults: true
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