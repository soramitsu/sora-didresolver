def workerLabel = 'docker-build-agent'
def deploymentHost = [
  'develop': 'ubuntu@test-1.sora.soramitsu.co.jp',
  'master': 'ubuntu@srv-1.sora.soramitsu.co.jp']
def dockerImage = 'openjdk:8'
def sendTo = 'sora-ci@soramitsu.co.jp'
def replyTo = 'buildbot@sora.org'

node(workerLabel) {
  scmVars = checkout scm
  try {
    docker.image("${dockerImage}").inside('-v /var/run/docker.sock:/var/run/docker.sock -v /tmp:/tmp') {
      stage('build') {
        sh(script: "./gradlew clean build -x test --parallel --configure-on-demand")
      }

      stage('test') {
        sh(script: "./gradlew test")
      }
    }
    withCredentials([
      usernamePassword(credentialsId: 'sorabot-github-oauth', usernameVariable: 'GH_USER', passwordVariable: 'GH_TOKEN'),
      string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN'),
      usernamePassword(credentialsId: 'sora-nexus-credentials', usernameVariable: 'DH_USER', passwordVariable: 'DH_PASS'),
      string(credentialsId: 'sora-repo-url', variable: 'DH_REPO_URL') ]) {
      if (env.CHANGE_ID != null ) {
        // it is a PR
        // then do PR analysis by sorabot
        docker.image("${dockerImage}").inside {
          stage('sonarqube') {
            sh(script: "./gradlew sonarqube -x test --configure-on-demand \
              -Dsonar.links.ci=${BUILD_URL} \
              -Dsonar.github.pullRequest=${env.CHANGE_ID} \
              -Dsonar.github.oauth=${GH_TOKEN} \
              -Dsonar.analysis.mode=preview \
              -Dsonar.github.disableInlineComments=true \
              -Dsonar.github.repository=soramitsu/did-resolver \
              -Dsonar.host.url=https://sonar.soramitsu.co.jp \
              -Dsonar.login=${SONAR_TOKEN} \
             ")
          } // end stage
        } // end docker
      } // end if
      if (scmVars.GIT_LOCAL_BRANCH ==~ /master|develop/) {
        docker.image("${dockerImage}-alpine").inside('-v /var/run/docker.sock:/var/run/docker.sock') {
          stage('sonarqube') {
            // push analysis results to sonar
            // do not use --parallel, as sonarqube can not work in this mode
            sh(script: "#!/bin/sh\n./gradlew sonarqube -x test --configure-on-demand \
              -Dsonar.host.url=https://sonar.soramitsu.co.jp \
              -Dsonar.login=${SONAR_TOKEN}")
          } // end stage
          stage('build and push docker image') {
            sh(script: "#!/bin/sh\napk update && apk add docker")
            sh(script: "#!/bin/sh\ndocker login --username ${DH_USER} --password '${DH_PASS}' https://${DH_REPO_URL}")
            sh(script: "#!/bin/sh\n./gradlew dockerPush -x test -PrepoUrl=${DH_REPO_URL}")
          } // end stage
        } // end docker
        stage ('deploy services') {
          sshagent(['jenkins-aws-ec2']) {
            sh "scp -o StrictHostKeyChecking=no ./deploy/update-and-deploy.sh ${deploymentHost[scmVars.GIT_LOCAL_BRANCH]}:/tmp"
            sh "ssh -o StrictHostKeyChecking=no \
              ${deploymentHost[scmVars.GIT_LOCAL_BRANCH]} 'chmod +x /tmp/update-and-deploy.sh; \
              DH_USER=${DH_USER} DH_PASS=${DH_PASS} DH_REPO_URL=${DH_REPO_URL} GIT_REPO_URL=${scmVars.GIT_URL} GIT_BRANCH=${scmVars.GIT_LOCAL_BRANCH} /tmp/update-and-deploy.sh'"
          } // end sshagent
        } // end stage
      } // end if
    } // end withCredentials
  } // end try
  catch(Exception e) {
    currentBuild.result = 'FAILURE'
  } // end catch
  finally {
    stage('upload test artifacts') {
      junit allowEmptyResults: true, keepLongStdio: true, testResults: '**/build/test-results/**/*.xml'
      jacoco execPattern: '**/build/jacoco/test.exec', sourcePattern: '**/src/main/java'
    } //end stage
    stage('send email notification') {
      emailSubject = '${PROJECT_DISPLAY_NAME} :: ${BUILD_STATUS} :: #${BUILD_NUMBER}'
      emailBody =
        '''
          Build status: ${BUILD_STATUS}<br/>
          Job: ${PROJECT_NAME}<br/>
          Build: #${BUILD_NUMBER}<br/>
          ${FAILED_TESTS}<br/>
          More info at: ${BUILD_URL}<br/><br/>
          --------LOG-BEGIN--------<br/>
          <pre style='line-height: 22px; display: block; color: #333; font-family: Monaco,Menlo,Consolas,"Courier New",monospace; padding: 10.5px; margin: 0 0 11px; font-size: 13px; word-break: break-all; word-wrap: break-word; white-space: pre-wrap; background-color: #f5f5f5; border: 1px solid #ccc; border: 1px solid rgba(0,0,0,.15); -webkit-border-radius: 4px; -moz-border-radius: 4px; border-radius: 4px;'>
            ${BUILD_LOG, maxLines=50, escapeHtml=true}
          </pre>
          --------LOG-END--------
      '''
      emailext body: "${emailBody}", replyTo: "${replyTo}", subject: "${emailSubject}", to: "${sendTo}"
    } //end stage
    cleanWs()
  } // end finally
} //end node
