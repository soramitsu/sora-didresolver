pipeline {
  environment {
    CODECOV_TOKEN = credentials('CODECOV_TOKEN')
    CODACY_PROJECT_TOKEN = credentials('CODACY_PROJECT_TOKEN')
  }
  options {
    buildDiscarder(logRotator(numToKeepStr: '20'))
  }
  agent { dockerfile true }
  stages {
    stage('Build') {
      steps {
        script {
          sh "gradle clean build"
          sh "gradle check"
          sh "gradle jacocoTestReport"
          sh "bash <(curl -s https://codecov.io/bash) -t ${CODECOV_TOKEN}"
          sh "java -jar /opt/codacy.jar report -l Java -r build/reports/coverage.xml"
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
