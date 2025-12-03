pipeline {
	agent any

	options {
		withChecks()
	}

	tools {
		maven "Default"
	}

	environment {
		BACKEND = "backend-catalogo"
		FRONTEND = "frontend-catalogo"
		SCANNER_HOME = tool 'SonarScanner'
	}

	stages {

		stage('Checkout') {
			steps {
				checkout scm
			}
		}

		/* ----------------------------------------------------
		   BACKEND BUILD + TEST + COVERAGE
		---------------------------------------------------- */
		stage('Build Backend + Tests + Jacoco') {
			steps {
				dir("${BACKEND}") {
					sh 'mvn clean verify -DskipTests=false'
				}
			}
		}

		/* ----------------------------------------------------
		   SONARQUBE ANALYSIS
		---------------------------------------------------- */
		stage('SonarQube Analysis') {
			steps {
				dir("${BACKEND}") {
					withSonarQubeEnv('SonarServer') {
						withCredentials([string(credentialsId: 'sonarqube-token-productcatalogo', variable: 'SONAR_TOKEN')]) {
							sh '''
                                mvn sonar:sonar \
                                    -Dsonar.projectKey=ProductCatalogo \
                                    -Dsonar.projectName=ProductCatalogo \
                                    -Dsonar.host.url=http://sonarqube:9000 \
                                    -Dsonar.login=$SONAR_TOKEN \
                                    -Dsonar.java.binaries=target/classes \
                                    -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                            '''
						}
					}
				}
			}
		}

		stage('Quality Gate') {
			steps {
				script {
					timeout(time: 10, unit: 'MINUTES') {
						def qg = waitForQualityGate()
						if (qg.status != 'OK') {
							unstable("Quality Gate: ${qg.status}")
						}
					}
				}
			}
		}

		/* ----------------------------------------------------
		   FRONTEND BUILD + COVERAGE
		---------------------------------------------------- */
		stage('Build Frontend + Coverage') {
			steps {
				dir("${FRONTEND}") {
					sh '''
                        export CHROME_BIN=/usr/bin/chromium
                        npm install
                        ng test --watch=false --code-coverage --browsers=ChromeHeadless --no-progress || true
                        npm run build
                    '''
				}
			}
		}

		/* ----------------------------------------------------
		   UPLOAD COVERAGE TO CODECOV
		---------------------------------------------------- */
		stage('Upload Coverage to Codecov') {
			steps {
				withCredentials([string(credentialsId: 'codecov-token', variable: 'CODECOV_TOKEN')]) {
					sh """
                        curl -Os https://uploader.codecov.io/latest/linux/codecov
                        chmod +x codecov
                        ./codecov -t ${CODECOV_TOKEN} \
                            -f backend-catalogo/target/site/jacoco/jacoco.xml \
                            -f frontend-catalogo/coverage/lcov.info
                    """
				}
			}
		}

		/* ----------------------------------------------------
		   DOCKER BUILD + DEPLOY (USANDO 2 COMPOSE)
		   docker-compose.yml + docker-compose.ci.yml
		---------------------------------------------------- */
		stage('Docker Build & Deploy') {
			steps {
				script {
					echo "Building Docker images..."
					sh """
				docker build -t catalogo-backend:latest backend-catalogo
				docker build -t catalogo-frontend:latest frontend-catalogo
			"""

					echo "Deploying CI services..."
					sh """
				docker-compose -f docker-compose.yml -f docker-compose.ci.yml up -d --build \
                    mysql sonarqube-db sonarqube backend-catalogo frontend-catalogo

				sleep 10
				docker-compose -f docker-compose.yml -f docker-compose.ci.yml ps
			"""
				}
			}
		}
	}

	post {
		always {
			echo "Archiving results..."

			junit 'backend-catalogo/target/surefire-reports/*.xml'

			jacoco(
				execPattern: 'backend-catalogo/target/jacoco.exec',
				classPattern: 'backend-catalogo/target/classes',
				sourcePattern: 'backend-catalogo/src/main/java'
			)

			publishHTML([
				allowMissing: false,
				alwaysLinkToLastBuild: false,
				keepAll: true,
				reportDir: 'backend-catalogo/target/site/jacoco',
				reportFiles: 'index.html',
				reportName: 'Jacoco Report'
			])

			archiveArtifacts artifacts: 'backend-catalogo/target/*.jar', fingerprint: true

			cleanWs()
		}
	}
}