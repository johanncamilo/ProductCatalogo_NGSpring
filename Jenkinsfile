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
					withSonarQubeEnv('SonarServer') {
						withCredentials([string(credentialsId: 'sonarqube-token-productcatalogo',
							variable: 'SONAR_TOKEN')]) {
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
				// Moved outside dir() block to avoid nesting issues
				script {
					echo "Waiting a few seconds for SonarQube to register the task..."
					sleep 10
				}
			}
		}

		stage('Quality Gate') {
			steps {
				script {
					timeout(time: 15, unit: 'MINUTES') {
						def qg = waitForQualityGate()
						if (qg.status != 'OK') {
							unstable(message: "Quality gate failed: ${qg.status}")
						}
					}
				}
			}
		}

		stage('Build Frontend + Coverage') {
			agent {
				docker {
					image 'node:18'
				}
			}
			steps {
				dir("${FRONTEND}") {
					sh '''
                        npm install
                        npm run test -- --watch=false --code-coverage
                        npm run build
                    '''
				}
			}
		}

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

		stage('Docker Build & Deploy') {
			steps {
				script {
					echo "Building Docker images..."
					sh '''
                        docker build -t catalogo-backend:latest backend-catalogo
                        docker build -t catalogo-frontend:latest frontend-catalogo
                        echo "Docker images built successfully"
                    '''

					echo "Deploying containers..."
					sh '''
                        docker compose down --remove-orphans || true
                        docker compose up -d mysql sonarqube-db sonarqube backend-catalogo frontend-catalogo
                        sleep 10
                        echo "Services deployed successfully"
                        docker compose ps
                    '''
				}
			}
		}

		stage('Restart Quality Gate') {
			steps {
				script {
					echo "Restarting SonarQube Quality Gate..."
					sh '''
                        sleep 5
                        docker restart sonarqube || true
                        for i in {1..30}; do
                            if curl -sf http://sonarqube:9000/api/system/health > /dev/null 2>&1; then
                                echo "SonarQube is healthy"
                                break
                            fi
                            echo "Waiting for SonarQube to be ready... ($i/30)"
                            sleep 2
                        done
                    '''
				}
			}
		}
	}

	post {
		always {
			echo "Archiving test results and coverage..."

			junit 'backend-catalogo/target/surefire-reports/*.xml'

			// Use JaCoCo plugin
			jacoco(
				execPattern: 'backend-catalogo/target/jacoco.exec',
				classPattern: 'backend-catalogo/target/classes',
				sourcePattern: 'backend-catalogo/src/main/java'
			)

			// Publish HTML report
			publishHTML([
				allowMissing: false,
				alwaysLinkToLastBuild: true,
				keepAll: true,
				reportDir: 'backend-catalogo/target/site/jacoco',
				reportFiles: 'index.html',
				reportName: 'JaCoCo Coverage Report'
			])

			archiveArtifacts artifacts: 'backend-catalogo/target/*.jar', fingerprint: true

			cleanWs()
		}
	}
}