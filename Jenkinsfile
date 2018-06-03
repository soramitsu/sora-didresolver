pipeline {
  environment {
    CODECOV_TOKEN = credentials('CODECOV_TOKEN')
  }
  options {
    buildDiscarder(logRotator(numToKeepStr: '20'))
  }
  agent { label 'x86_64' }
  stages {
    stage('Build') {
      steps {
        script {
          docker.image('gradle:jdk9').inside {
            sh "gradle clean build"
            sh "gradle check"
            sh "gradle jacocoTestReport"
            sh "bash <(curl -s https://codecov.io/bash) -t ${CODECOV_TOKEN}"
          }
        }
      }
      post {
        cleanup {
          script {
            cleanWs()
          }
        }
      }
    }
  }
}
