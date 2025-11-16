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
                bat 'mvn clean verify'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Coverage Report') {
            steps {
                echo '=== Generando reporte de cobertura ==='
                bat 'mvn jacoco:report'
            }
            post {
                always {
                    publishHTML(target: [
                        reportDir: 'target/site/jacoco',
                        reportFiles: 'index.html',
                        reportName: 'JaCoCo Coverage Report',
                        keepAll: true,
                        alwaysLinkToLastBuild: true,
                        allowMissing: false
                    ])
                }
            }
        }
        
        stage('SonarQube Analysis') {
            steps {
                echo '=== Análisis de SonarQube ==='
                script {
                    def scannerHome = tool name: 'SonarQube Scanner', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
                    withSonarQubeEnv('SonarQube') {
                        bat """
                            ${scannerHome}/bin/sonar-scanner ^
                            -Dsonar.projectKey=petclinic-backend ^
                            -Dsonar.sources=src/main/java ^
                            -Dsonar.tests=src/test/java ^
                            -Dsonar.java.binaries=target/classes ^
                            -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                        """
                    }
                }
            }
        }
        
        stage('Quality Gate') {
            steps {
                echo '=== Esperando Quality Gate ==='
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
    }
    
    post {
        success {
            echo '✅ Pipeline ejecutado correctamente'
        }
        failure {
            echo '❌ Pipeline falló'
        }
        always {
            echo '=== Limpieza ==='
            cleanWs()
        }
    }
}