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
                    ./mvnw clean package
                """
			}
		}

		stage('SonarQube Analysis') {
			environment {
				scannerHome = tool 'SonarScanner'
			}
			steps {
				withCredentials([string(credentialsId: 'sonarqube-token', variable: 'SONAR_TOKEN')]) {
					sh """
                ${scannerHome}/bin/sonar-scanner \
                  -Dsonar.projectKey=ProductCatalogo \
                  -Dsonar.host.url=http://sonarqube:9000 \
                  -Dsonar.login=$SONAR_TOKEN \
                  -Dsonar.sources=$BACKEND_DIR/src/main/java,$FRONTEND_DIR/src \
                  -Dsonar.java.binaries=$BACKEND_DIR/target/classes
            """
				}
			}
		}

		stage('Quality Gate') {
			steps {
				timeout(time: 10, unit: 'MINUTES') {
					waitForQualityGate abortPipeline: true
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
				sh """
                    cd $FRONTEND
                    npm install
                    npm run build
                """
			}
		}

		stage('Docker Build & Deploy') {
			steps {
				sh """
                    docker build -t catalogo-backend $BACKEND
                    docker build -t catalogo-frontend $FRONTEND
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