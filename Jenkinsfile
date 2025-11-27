pipeline {
	agent any

	tools {
		maven "Default"
	}

	environment {
		BACKEND = "backend-catalogo"
		FRONTEND = "frontend-catalogo"
	}

	stages {

		stage('Checkout') {
			steps {
				git branch: 'main',
				credentialsId: 'github-credentials',
				url: 'https://github.com/johanncamilo/ProductCatalogo_NGSpring.git'
			}
		}

		stage('Build Backend') {
			steps {
				sh """
                    cd $BACKEND
                    ./mvnw clean package -DskipTests
                """
			}
		}

		stage('SonarQube Analysis') {
			environment {
				SCANNER_HOME = tool 'SonarScanner'
			}
			steps {
				withSonarQubeEnv('SonarServer') {

					withCredentials([string(credentialsId: 'sonarqube-token-productcatalogo', variable: 'SONAR_TOKEN')]) {

						sh '''#!/bin/bash
                            "$SCANNER_HOME/bin/sonar-scanner" \
                              -Dsonar.projectKey=ProductCatalogo \
                              -Dsonar.host.url="$SONAR_HOST_URL" \
                              -Dsonar.sources="backend-catalogo/src/main/java,frontend-catalogo/src" \
                              -Dsonar.java.binaries="backend-catalogo/target/classes" \
                              -Dsonar.token="$SONAR_TOKEN"
                        '''
					}
				}
			}
		}

		stage("Quality Gate") {
			steps {
				script {
					timeout(time: 10, unit: 'MINUTES') {
						waitForQualityGate abortPipeline: true
					}
				}
			}
		}

		stage('Build Frontend') {

			steps {
				sh """
                    cd $FRONTEND
                    npm install
                    npm run build
                """
			}
		}

		stage('Docker Build & Deploy') {
			steps {

				sh '''
                    # Construcción de imágenes
                    docker build -t catalogo-backend backend-catalogo
                    docker build -t catalogo-frontend frontend-catalogo

                    # Levantar solo backend + frontend + mysql + sonarqube
                    # (sin bajar jenkins!)
                    docker compose up -d backend-catalogo frontend-catalogo mysql sonarqube
                '''
			}
		}
	}

	post {
		always {
			cleanWs()
		}
	}
}