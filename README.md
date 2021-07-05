[![Build Status](https://jenkins.soramitsu.co.jp/buildStatus/icon?job=sora/did-resolver/master)](https://jenkins.soramitsu.co.jp/job/sora/job/did-resolver/job/master/)
[![codecov](https://codecov.io/gh/soramitsu/did-resolver/branch/master/graph/badge.svg)](https://codecov.io/gh/soramitsu/did-resolver)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/72f7aced0a8246da9bbabb1cdbaf9ff2)](https://www.codacy.com/app/Warchant/did-resolver?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=soramitsu/did-resolver&amp;utm_campaign=Badge_Grade)

#Didresolver

##Purpose
Provide a way to authenticate users, organizations and other actors in fully decentralized manner

##Specification
Specification of DAuth can be found [here](https://github.com/soramitsu/sora-specs)

##Usage
Build project: ```./gradlew build```

Run tests: ```./gradlew test```

Create an image with didresolver inside: ```./gradlew dockerBuild```

##Environment variables
Didresolver depends on Iroha blockchain, so it requires direct link to it and preconfigured blockchain
On startup didresolver service expects environment variables:
- DIDRESOLVER_IROHA_HOST: URL of Iroha peer
- DIDRESOLVER_IROHA_PORT: port on which Iroha peer expects incoming messages
- DIDRESOLVER_IROHA_ACCOUNT: precreated account in Iroha in which account details records written
- DIDRESOLVER_IROHA_PRIVATE_KEY: secret key of the predefined account
- DIDRESOLVER_IROHA_PUBLIC_KEY: public key of the predefined account

##API
All endpoints exposed by service can be found in Swagger documentation. By default, it is available by path `/didresolver/swagger-ui.html`
