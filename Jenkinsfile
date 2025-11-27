pipeline {
	agent any

	tools {
		nodejs "node18"
		maven "Default"
		sonarScanner "SonarScanner"
	}

	environment {
		BACKEND_DIR = "backend-catalogo"
		FRONTEND_DIR = "frontend-catalogo"
	}

	stages {

		/* ------------------------- CHECKOUT ------------------------- */
		stage('Checkout') {
			steps {
				git branch: 'main',
				credentialsId: 'github-credentials',
				url: 'https://github.com/johanncamilo/ProductCatalogo_NGSpring.git'
			}
		}

		/* ------------------------- BUILD BACKEND ------------------------- */
		stage('Build Backend (Spring Boot)') {
			steps {
				sh """
                    cd $BACKEND_DIR
                    ./mvnw clean package -Dmaven.test.skip=true
                """
			}
		}

		/* ------------------------- TESTS + JACOCO ------------------------- */
		stage('Run Tests + Coverage') {
			steps {
				sh """
                    cd $BACKEND_DIR
                    ./mvnw test
                """
			}
			post {
				always {
					junit "$BACKEND_DIR/target/surefire-reports/*.xml"
				}
			}
		}

		/* ------------------------- SONARQUBE ------------------------- */
		stage('SonarQube Analysis') {
			steps {
				withSonarQubeEnv('SonarServer') {
					sh """
                        sonar-scanner \
                          -Dsonar.projectKey=ProductCatalogo \
                          -Dsonar.projectName=ProductCatalogo \
                          -Dsonar.sources=$BACKEND_DIR/src/main/java \
                          -Dsonar.tests=$BACKEND_DIR/src/test/java \
                          -Dsonar.java.binaries=$BACKEND_DIR/target/classes \
                          -Dsonar.junit.reportPaths=$BACKEND_DIR/target/surefire-reports \
                          -Dsonar.jacoco.reportPaths=$BACKEND_DIR/target/jacoco.exec \
                          -Dsonar.coverage.jacoco.xmlReportPaths=$BACKEND_DIR/target/site/jacoco/jacoco.xml \
                          -Dsonar.host.url=\$SONAR_HOST_URL \
                          -Dsonar.login=\$SONAR_AUTH_TOKEN
                    """
				}
			}
		}

		/* ----------------------- SONAR QUALITY GATE ----------------------- */
		stage("Quality Gate") {
			steps {
				timeout(time: 10, unit: 'MINUTES') {
					waitForQualityGate abortPipeline: true
				}
			}
		}

		/* ------------------------- CODECOV ------------------------- */
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

		/* ------------------------- BUILD FRONTEND ------------------------- */
		stage('Build Frontend (Angular)') {
			steps {
				sh """
                    cd $FRONTEND_DIR
                    npm ci
                    npm run build
                """
			}
		}

		/* ------------------------- DOCKER BUILD ------------------------- */
		stage('Build Docker Images') {
			steps {
				sh "docker build -t catalogo-backend $BACKEND_DIR"
				sh "docker build -t catalogo-frontend $FRONTEND_DIR"
			}
		}

		/* ------------------------- DOCKER DEPLOY ------------------------- */
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