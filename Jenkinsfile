pipeline {
    agent any

	tools {
		nodejs "node18"
		maven "Default"
	}

    environment {
        BACKEND_DIR = "backend-catalogo"
        FRONTEND_DIR = "frontend-catalogo"
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                    credentialsId: 'github-credentials',
                    url: 'https://github.com/johanncamilo/ProductCatalogo_NGSpring.git'
            }
        }

        stage('Build Backend (Spring Boot)') {
            steps {
                sh """
                    cd $BACKEND_DIR
                    ./mvnw clean package -Dmaven.test.skip=true
                """
            }
        }

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

        stage('Build Frontend (Angular)') {
            steps {
                sh """
                    cd $FRONTEND_DIR
                    npm ci
                    npm run build
                """
            }
        }

        stage('Build Docker Images') {
            steps {
                sh "docker build -t catalogo-backend $BACKEND_DIR"
                sh "docker build -t catalogo-frontend $FRONTEND_DIR"
            }
        }

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