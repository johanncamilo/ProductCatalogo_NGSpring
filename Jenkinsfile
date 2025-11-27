pipeline {
	agent any

	tools {
		maven "Default"
	}

	environment {
		BACKEND_DIR = "backend-catalogo"
		FRONTEND_DIR = "frontend-catalogo"
	}

	stages {

		/* ---------------------------- CHECKOUT ---------------------------- */
		stage('Checkout') {
			steps {
				git branch: 'main',
				credentialsId: 'github-credentials',
				url: 'https://github.com/johanncamilo/ProductCatalogo_NGSpring.git'
			}
		}

		/* ------------------------ BUILD + TEST (JAVA) --------------------- */
		stage('Build & Test Backend') {
			steps {
				sh """
                    cd $BACKEND_DIR
                    ./mvnw clean verify
                """
			}
			post {
				always {
					junit "$BACKEND_DIR/target/surefire-reports/*.xml"
				}
			}
		}

		/* -------------------------- PACKAGE (JAVA) ------------------------ */
		stage('Package Backend') {
			steps {
				sh """
                    cd $BACKEND_DIR
                    ./mvnw package -DskipTests
                """
			}
		}

		/* -------------------------- SONARQUBE ----------------------------- */
		stage('SonarQube Analysis') {
			environment {
				scannerHome = tool 'SonarScanner'
			}
			steps {
				withSonarQubeEnv('SonarServer') {
					sh """
                        ${scannerHome}/bin/sonar-scanner \
                          -Dsonar.projectKey=ProductCatalogo \
                          -Dsonar.projectName=ProductCatalogo \
                          -Dsonar.sources=$BACKEND_DIR/src/main/java,$FRONTEND_DIR/src \
                          -Dsonar.tests=$BACKEND_DIR/src/test/java \
                          -Dsonar.java.binaries=$BACKEND_DIR/target/classes \
                          -Dsonar.junit.reportPaths=$BACKEND_DIR/target/surefire-reports \
                          -Dsonar.jacoco.reportPaths=$BACKEND_DIR/target/jacoco.exec \
                          -Dsonar.coverage.jacoco.xmlReportPaths=$BACKEND_DIR/target/site/jacoco/jacoco.xml \
                          -Dsonar.inclusions=**/*.java,**/*.ts,**/*.html,**/*.css \
                          -Dsonar.exclusions=**/node_modules/**,**/*.spec.ts \
                          -Dsonar.javascript.lcov.reportPaths=$FRONTEND_DIR/coverage/lcov.info
                    """
				}
			}
		}

		/* ----------------------- QUALITY GATE ----------------------------- */
		stage("Quality Gate") {
			steps {
				timeout(time: 10, unit: 'MINUTES') {
					waitForQualityGate abortPipeline: true
				}
			}
		}

		/* -------------------------- CODECOV ------------------------------- */
		stage('Upload Coverage to Codecov') {
			steps {
				withCredentials([string(credentialsId: 'codecov-token', variable: 'CODECOV_TOKEN')]) {
					sh """
                        curl -Os https://uploader.codecov.io/latest/linux/codecov
                        chmod +x codecov
                        ./codecov -t "$CODECOV_TOKEN" \
                                  -R $BACKEND_DIR \
                                  -f $BACKEND_DIR/target/site/jacoco/jacoco.xml
                    """
				}
			}
		}

		/* ---------------------- BUILD FRONTEND (ANGULAR) ----------------- */
		stage('Build Frontend (Angular)') {
			agent {
				docker {
					image 'node:18'
					args '-u root:root'
				}
			}
			steps {
				sh """
                    cd $FRONTEND_DIR
                    npm ci
                    npm run build
                """
			}
		}

		/* -------------------------- DOCKER BUILD -------------------------- */
		stage('Build Docker Images') {
			steps {
				sh "docker build -t catalogo-backend $BACKEND_DIR"
				sh "docker build -t catalogo-frontend $FRONTEND_DIR"
			}
		}

		/* -------------------------- DOCKER DEPLOY ------------------------- */
		stage('Deploy Local (docker compose)') {
			steps {
				sh """
                    docker compose down
                    docker compose up -d --build
                """
			}
		}
	}

	post {
		always {
			cleanWs()
		}
	}
}