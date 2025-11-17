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
                sh 'cd backend-catalogo && ./mvnw -DskipTests -DskipITs -Dmaven.test.skip=true clean package'
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
                sh 'docker compose -f docker-compose.yml down'
                sh 'docker compose -f docker-compose.yml up -d --build'
            }
        }

        stage('Notify') {
            steps {
                mail to: 'jsborbon@poligran.edu.co',
                     subject: "Pipeline completado: ${env.JOB_NAME}",
                     body: "El pipeline ${env.JOB_NAME} finalizó con estado ${currentBuild.currentResult}."
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}