pipeline {

    agent any

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/johanncamilo/ProductCatalogo_NGSpring.git'
            }
        }

        stage('Build Backend (Spring Boot)') {
            steps {
                sh 'cd backend-catalogo && ./mvnw clean package -Dmaven.test.skip=true'
            }
        }

        stage('Build Frontend (Angular)') {
            steps {
                sh 'cd frontend-catalogo && npm ci && npm run build'
            }
        }

        stage('Build Docker Images') {
            steps {
                sh 'docker build -t catalogo-backend ./backend-catalogo'
                sh 'docker build -t catalogo-frontend ./frontend-catalogo'
            }
        }

        stage('Deploy Local (Docker Compose)') {
            steps {
                sh 'docker compose down'
                sh 'docker compose up -d --build'
            }
        }

    }

    post {
        always {
            cleanWs()
        }
    }
}