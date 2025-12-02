pipeline {
	agent any

	tools {
		maven "Maven-3.9"
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

		stage('Build Backend + Tests + Jacoco') {
			steps {
				dir("${BACKEND}") {
					sh 'mvn clean verify -DskipTests=false'
				}
			}
		}

		stage('SonarQube Analysis') {
			steps {
				dir("${BACKEND}") {

					withSonarQubeEnv('SonarQube') {

						withCredentials([string(credentialsId: 'sonarqube-token-productcatalogo',
							variable: 'SONAR_TOKEN')]) {

							sh '''
                                mvn sonar:sonar \
                                  -Dsonar.projectKey=ProductCatalogo \
                                  -Dsonar.projectName=ProductCatalogo \
                                  -Dsonar.host.url=$SONAR_HOST_URL \
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
						waitForQualityGate abortPipeline: true
					}
				}
			}
		}

		stage('Build Frontend') {
			agent {
				docker {
					image 'node:18'
				}
			}
			steps {
				dir("${FRONTEND}") {
					sh '''
                        npm install
                        npm run build
                    '''
				}
			}
		}

		stage('Docker Build & Deploy') {
			steps {
				sh '''
                    docker build -t catalogo-backend backend-catalogo
                    docker build -t catalogo-frontend frontend-catalogo

                    docker compose up -d backend-catalogo frontend-catalogo mysql sonarqube
                '''
			}
		}
	}

	post {
		always {
			echo "Archiving test results and coverage..."

			// JUnit test results
			junit 'backend-catalogo/target/surefire-reports/*.xml'

			// Jacoco XML
			recordCoverage(
				tools: [[
					parser: 'JACOCO',
					pattern: 'backend-catalogo/target/site/jacoco/*.xml'
				]]
			)

			// Build artifacts
			archiveArtifacts artifacts: 'backend-catalogo/target/*.jar', fingerprint: true

			cleanWs()
		}
	}
}