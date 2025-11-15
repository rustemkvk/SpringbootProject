pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '20'))
        timeout(time: 20, unit: 'MINUTES')
    }

    environment {
        COMPOSE_PROJECT_NAME = "student-ci-${BUILD_NUMBER}"
        SPRING_DATASOURCE_URL = 'jdbc:postgresql://localhost:5433/studentdb'
        SPRING_DATASOURCE_USERNAME = 'postgres'
        SPRING_DATASOURCE_PASSWORD = 'password'
    }

    stages {
        stage('Checkout') {
            steps { checkout scm }
        }
        stage('Start test database') {
            steps { sh 'docker compose up -d --wait db' }
        }
        stage('Build and test') {
            steps { sh 'mvn --batch-mode --no-transfer-progress clean verify' }
        }
        stage('Build application image') {
            when { branch 'main' }
            steps { sh 'docker build --tag student-api:${BUILD_NUMBER} .' }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
            archiveArtifacts allowEmptyArchive: true,
                artifacts: 'target/*.jar,target/cucumber-report/**', fingerprint: true
            sh 'docker compose down --volumes --remove-orphans || true'
        }
        cleanup { deleteDir() }
    }
}
