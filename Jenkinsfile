pipeline {

    agent {
        docker {
            image 'node:22-alpine'
            label 'docker'
            args '-u root:root'
        }
    }

    stages {

        stage('Checkout') {
            steps {
                script {
                    git branch: 'main', url: 'https://github.com/johanncamilo/ProductCatalogo_NGSpring.git'
                }
            }
        }

        stage('Build Backend (Spring Boot)') {
            steps {
                script {
                    sh 'cd backend-catalogo && ./mvnw clean package -DskipTests'
                }
            }
        }

        stage('Build Frontend (Angular)') {
            steps {
                script {
                    sh 'cd frontend-catalogo && npm ci && npm run build'
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                script {
                    sh 'docker build -t catalogo-backend ./backend-catalogo'
                    sh 'docker build -t catalogo-frontend ./frontend-catalogo'
                }
            }
        }

        stage('Test Backend') {
            steps {
                script {
                    sh 'cd backend-catalogo && ./mvnw test'
                }
            }
        }

        stage('Deploy Local (Docker Compose)') {
            steps {
                script {
                    sh 'docker compose -f docker-compose.yml down'
                    sh 'docker compose -f docker-compose.yml up -d --build'
                }
            }
        }

        stage('Notify') {
            steps {
                script {
                    mail to: 'jsborbon@poligran.edu.co',
                         subject: "Pipeline completado: ${env.JOB_NAME}",
                         body: "El pipeline ${env.JOB_NAME} finalizó con estado ${currentBuild.currentResult}."
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }

        success {
            echo "Pipeline completado con éxito!"
        }

        failure {
            echo "Hubo un error en el pipeline!"
        }
    }
}
pipeline {

    agent {
        docker {
            image 'node:22-alpine'
            label 'docker'
            args '-u root:root'
        }
    }

    stages {

        stage('Checkout') {
            steps {
                script {
                    git branch: 'main', url: 'https://github.com/johanncamilo/ProductCatalogo_NGSpring.git'
                }
            }
        }

        stage('Build Backend (Spring Boot)') {
            steps {
                script {
                    sh 'cd backend-catalogo && ./mvnw clean package -DskipTests'
                }
            }
        }

        stage('Build Frontend (Angular)') {
            steps {
                script {
                    sh 'cd frontend-catalogo && npm ci && npm run build'
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                script {
                    sh 'docker build -t catalogo-backend ./backend-catalogo'
                    sh 'docker build -t catalogo-frontend ./frontend-catalogo'
                }
            }
        }

        stage('Test Backend') {
            steps {
                script {
                    sh 'cd backend-catalogo && ./mvnw test'
                }
            }
        }

        stage('Deploy Local (Docker Compose)') {
            steps {
                script {
                    sh 'docker compose -f docker-compose.yml down'
                    sh 'docker compose -f docker-compose.yml up -d --build'
                }
            }
        }

        stage('Notify') {
            steps {
                script {
                    mail to: 'jsborbon@poligran.edu.co',
                         subject: "Pipeline completado: ${env.JOB_NAME}",
                         body: "El pipeline ${env.JOB_NAME} finalizó con estado ${currentBuild.currentResult}."
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }

        success {
            echo "Pipeline completado con éxito!"
        }

        failure {
            echo "Hubo un error en el pipeline!"
        }
    }
}
