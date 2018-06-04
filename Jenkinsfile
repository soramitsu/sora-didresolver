pipeline {
  environment {
    CODECOV_TOKEN = credentials('CODECOV_TOKEN')
    CODACY_PROJECT_TOKEN = credentials('CODACY_PROJECT_TOKEN')
  }
  options {
    buildDiscarder(logRotator(numToKeepStr: '20'))
  }
  agent { label 'x86_64' }
  stages {
    stage('Build') {
      steps {
        script {
          docker.image('gradle:jdk8').inside {
            sh "gradle clean build"
            sh "gradle check"
            sh "gradle jacocoTestReport"
            sh "bash <(curl -s https://codecov.io/bash) -t ${CODECOV_TOKEN}"
            sh "wget https://github.com/codacy/codacy-coverage-reporter/releases/download/4.0.1/codacy-coverage-reporter-4.0.1-assembly.jar"
            sh "java -jar codacy-coverage-reporter-4.0.1-assembly.jar report -l Java -r build/reports/coverage.xml"
          }
        }
      }
      post {
        always {
          junit 'build/test-results/**/*.xml'
        }
        cleanup {
          script {
            cleanWs()
          }
        }
      }
    }
  }
}
