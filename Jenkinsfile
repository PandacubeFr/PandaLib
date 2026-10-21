pipeline {
    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '100'))
        disableConcurrentBuilds()
    }

    tools {
        jdk 'java-25-openjdk'
        maven 'maven-3.9'
    }

    stages {
        stage('Build') {
            steps {
                sh 'mvn clean install -Dbuild.number=${BUILD_NUMBER} -U -T 6'
            }
        }
    }

    post {
        always {
            junit testResults: '**/target/surefire-reports/TEST-*.xml', allowEmptyResults: true
            archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true, allowEmptyArchive: false
        }
        cleanup {
            cleanWs()
        }
    }
}
