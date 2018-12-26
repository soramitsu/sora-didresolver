#!/bin/bash
DH_USER=${DH_USER:?Docker repo username is not defined}
DH_PASS=${DH_PASS:?Docker repo password is not defined}
REPO_URL=${REPO_URL:?Docker repo URL is not defined}
GIT_BRANCH=${GIT_BRANCH:?Git branch is not defined}
set -ex
sudo rm -rf /opt/services/did-resolver
sudo mkdir -p /opt/services/did-resolver
pushd /opt/services/did-resolver
sudo git init
sudo git pull https://github.com/soramitsu/sora-passport-backend.git $GIT_BRANCH
pushd deploy
set +x
sudo docker login --username ${DH_USER} --password ${DH_PASS} ${REPO_URL}
set -x
sudo cp ../../sora-env/.env-did-resolver .
sudo cp ../../sora-env/.env .
sudo docker-compose -f docker-compose.yml pull
sudo docker-compose -f docker-compose.yml -p sora-test down
sudo docker-compose -f docker-compose.yml -p sora-test up -d
