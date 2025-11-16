pipeline {
    agent any
    
    tools {
        maven 'Maven-3.9'
        jdk 'JDK-21'
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo '=== Checkout del código ==='
                checkout scm
            }
        }
        
        stage('Build & Tests') {
            steps {
                echo '=== Compilando y ejecutando tests ==='
                sh 'mvn clean compile test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Coverage Report') {
            steps {
                sh 'mvn jacoco:report'
                publishHTML([
                    target: [
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/site/jacoco',
                        reportFiles: 'index.html',
                        reportName: 'Code Coverage Report'
                    ]
                ])
            }
        }
        
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonar-server') {
                    sh 'mvn sonar:sonar'
                }
            }
        }
        
        stage('Quality Gate') {
            steps {
                timeout(time: 1, unit: 'HOURS') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
    }
    
    post {
        always {
            echo '=== Limpieza ==='
            deleteDir()  // En lugar de cleanWs
        }
        failure {
            echo '❌ Pipeline falló'
        }
        success {
            echo '✅ Pipeline exitoso'
        }
    }
}