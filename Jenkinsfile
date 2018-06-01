pipeline {
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
          }
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
