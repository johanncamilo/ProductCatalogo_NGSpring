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

		/** NUEVA ETAPA: SUBIR COBERTURA A CODECOV **/
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

			recordCoverage(
				tools: [[
					parser: 'JACOCO',
					pattern: 'backend-catalogo/target/site/jacoco/*.xml'
				]]
			)

			archiveArtifacts artifacts: 'backend-catalogo/target/*.jar', fingerprint: true

			cleanWs()
		}
	}
}