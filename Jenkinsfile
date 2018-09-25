def workerLabel = 'bca-sora-standalone'

node(workerLabel) {
  stage('build') {
    checkout scm
    sh(script: "./gradlew clean build -x test --parallel --configure-on-demand")
  }

  stage('test') {
    sh(script: "./gradlew test")
    junit allowEmptyResults: true, keepLongStdio: true, testResults: '**/build/test-results/**/*.xml'
    jacoco execPattern: '**/build/jacoco/test.exec', sourcePattern: '**/src/main/java'
  }

  if (env.BRANCH_NAME.contains('master')) {
    stage('build docker') {
      withCredentials([string(credentialsId: 'sora-repo-url', variable: 'REPO_URL')]) {
        withCredentials([usernamePassword(credentialsId: 'sora-nexus-credentials', usernameVariable: 'DH_USER', passwordVariable: 'DH_PASS')]) {
          sh(script: "docker login --username ${DH_USER} --password '${DH_PASS}' https://${REPO_URL}")
        }
        sh(script: "./gradlew dockerPush -PrepoUrl=${REPO_URL}")
      }
    }
  }

  if (env.CHANGE_ID != null && env.CHANGE_TARGET ==~ /(master|develop)/) {
    stage('coverage') {
      withCredentials([usernamePassword(credentialsId: 'sorabot-github-oauth', usernameVariable: 'GH_USER', passwordVariable: 'GH_TOKEN')]) {
        withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
          sh(script: "./gradlew sonarqube -x test --configure-on-demand \
            -Dsonar.host.url=https://sonar.soramitsu.co.jp \
            -Dsonar.login=${SONAR_TOKEN}", returnStatus: true)
          sh(script: "./gradlew sonarqube -x test --configure-on-demand \
            -Dsonar.links.ci=${BUILD_URL} \
            -Dsonar.github.pullRequest=${env.CHANGE_ID} \
            -Dsonar.github.oauth=${GH_TOKEN} \
            -Dsonar.analysis.mode=preview \
            -Dsonar.github.repository=soramitsu/did-resolver \
            -Dsonar.host.url=https://sonar.soramitsu.co.jp \
            -Dsonar.login=${SONAR_TOKEN}", returnStatus: true)
        }
      }
    }
  }
}
