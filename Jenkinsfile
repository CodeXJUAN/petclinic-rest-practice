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
                script {
                    echo "=== Ejecutando análisis SonarQube ==="
                    withSonarQubeEnv('My SonarQube Server') {
                        sh 'mvn sonar:sonar'
                    }
                }
            }
        }
        
        stage('Quality Gate') {
            steps {
                script {
                    echo "=== Esperando resultado de Quality Gate ==="
                    timeout(time: 1, unit: 'HOURS') {
                        def qg = waitForQualityGate()
                        if (qg.status != 'OK') {
                            error "Quality Gate falló: ${qg.status}"
                        } else {
                            echo "✅ Quality Gate pasó correctamente"
                        }
                    }
                }
            }
        }
    }
    
    post {
        always {
            echo '=== Limpieza ==='
            deleteDir()
        }
        failure {
            echo '❌ Pipeline falló'
        }
        success {
            echo '✅ Pipeline exitoso'
        }
    }
}