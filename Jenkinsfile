pipeline {
	agent any

	tools {
		maven "Default"
	}

	environment {
		BACKEND = "backend-catalogo"
		FRONTEND = "frontend-catalogo"
		SCANNER_HOME = tool 'SonarScanner'
	}

	stages {

		/* ----------------------------------------------------
		   CHECKOUT
		---------------------------------------------------- */
		stage('Checkout') {
			steps {
				withChecks("Checkout") {
					checkout scm
				}
			}
		}

		/* ----------------------------------------------------
		   BACKEND BUILD + TEST + COVERAGE
		---------------------------------------------------- */
		stage('Build Backend + Tests + Jacoco') {
			steps {
				withChecks("Backend Build & Tests") {
					dir("${BACKEND}") {
						sh 'mvn clean verify -DskipTests=false'
					}
				}
			}
		}

		/* ----------------------------------------------------
		   SONARQUBE ANALYSIS
		---------------------------------------------------- */
		stage('SonarQube Analysis') {
			steps {
				withChecks("SonarQube Analysis") {
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
		}

		/* ----------------------------------------------------
		   QUALITY GATE
		---------------------------------------------------- */
		stage('Quality Gate') {
			steps {
				withChecks("Quality Gate") {
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
		}

		/* ----------------------------------------------------
		   FRONTEND BUILD + COVERAGE (KARMA)
		---------------------------------------------------- */
		stage('Build Frontend + Coverage') {
			steps {
				withChecks("Frontend Build & Tests") {
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
		}

		/* ----------------------------------------------------
		   CODECOV UPLOAD
		---------------------------------------------------- */
		stage('Upload Coverage to Codecov') {
			steps {
				withChecks("Codecov Upload") {
					withCredentials([string(credentialsId: 'codecov-token', variable: 'CODECOV_TOKEN')]) {
						sh """
                            curl -Os https://uploader.codecov.io/latest/linux/codecov
                            chmod +x codecov
                            ./codecov -t ${CODECOV_TOKEN} \
                                -f backend-catalogo/target/site/jacoco/jacoco.xml \
                                -f frontend-catalogo/coverage/lcov.info \
                                --verbose
                        """
					}
				}
			}
		}

		/* ----------------------------------------------------
		   DOCKER BUILD & DEPLOY
		---------------------------------------------------- */
		stage('Docker Build & Deploy') {
			steps {
				withChecks("Docker Build & Deploy") {
					script {
						sh """
                            docker build -t catalogo-backend:latest backend-catalogo
                            docker build -t catalogo-frontend:latest frontend-catalogo
                        """

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
	}

	/* -------------------------------------------------------
	   POST BLOCK PROTEGIDO (NO marca FAILURE)
	------------------------------------------------------- */
	post {
		always {

			echo "Archiving results..."

			// JUNIT
			catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
				junit 'backend-catalogo/target/surefire-reports/*.xml'
			}

			// JACOCO
			catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
				jacoco(
					execPattern: 'backend-catalogo/target/jacoco.exec',
					classPattern: 'backend-catalogo/target/classes',
					sourcePattern: 'backend-catalogo/src/main/java'
				)
			}

			// HTML REPORT
			catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
				publishHTML([
					allowMissing: true,
					alwaysLinkToLastBuild: false,
					keepAll: true,
					reportDir: 'backend-catalogo/target/site/jacoco',
					reportFiles: 'index.html',
					reportName: 'Jacoco Report'
				])
			}

			// ARTIFACTS
			catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
				archiveArtifacts artifacts: 'backend-catalogo/target/*.jar', fingerprint: true
			}

			// CLEAN WORKSPACE
			catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
				cleanWs()
			}

			echo "Post actions completed."
		}
	}
}