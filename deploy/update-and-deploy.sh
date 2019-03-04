#!/bin/bash
DH_USER=${DH_USER:?Docker repo username is not defined}
DH_PASS=${DH_PASS:?Docker repo password is not defined}
DH_REPO_URL=${DH_REPO_URL:?Docker repo URL is not defined}
GIT_REPO_URL=${GIT_REPO_URL:?Docker repo URL is not defined}
GIT_BRANCH=${GIT_BRANCH:?Git branch is not defined}
#VERSION - optional
set -ex
sudo rm -rf /opt/services/did-resolver
sudo mkdir -p /opt/services/did-resolver
pushd /opt/services/did-resolver
sudo git init
sudo git pull $GIT_REPO_URL $GIT_BRANCH
pushd deploy
set +x
sudo docker login --username ${DH_USER} --password ${DH_PASS} ${DH_REPO_URL}
set -x
sudo cp ../../sora-env/.env-did-resolver .
sudo cp ../../sora-env/.env .
sudo echo "${VERSION}" >> .env
sudo docker network create sora-net || true
sudo docker-compose -f docker-compose.yml pull
sudo docker-compose -f docker-compose.yml -p sora down
sudo docker-compose -f docker-compose.yml -p sora up -d
