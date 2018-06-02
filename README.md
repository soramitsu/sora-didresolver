# Sora DID Method specification

## 1. Introduction

Decentralized Identifiers (DIDs) are a new type of identifier for verifiable, "self-sovereign" digital identity. DIDs are fully under the control of the DID subject, independent from any centralized registry, identity provider, or certificate authority. DIDs are URLs that relate a DID subject to means for trustable interactions with that subject. DIDs resolve to DID Documents — simple documents that describe how to use that specific DID. Each DID Document contains at least three things: cryptographic material, authentication suites, and service endpoints. Cryptographic material combined with authentication suites provide a set of mechanisms to authenticate as the DID subject (e.g. public keys, pseudonymous biometric templates, etc.). Service endpoints enable trusted interactions with the DID subject.

DID is a unique identifier, which identifies company, people, service or device. 

## 2. The Sora DID Scheme

Following section 3.1 of DID spec we define Sora DID scheme with the following ABNF:
```=
method             = "sora"
did-type           = "iroha" / "uuid" / "ed"
did                = "did:" method ":" did-type ":" identifier
did-reference      = did [ "/" did-path ] [ "#" did-fragment ]
did-path           ; same syntax as URI path
did-fragment       ; same syntax as URI fragment
```

Where `identifier` is did-type specific identifier.


### 2.1 "iroha" DID type

For Sora based on Iroha, **iroha** did-type can be used. Identifier in this case should be valid account id. 

Example:

```=
did:sora:iroha:bogdan@soramitsu.co.jp
```
### 2.2 "uuid" DID type

This type if used in case, if user does not have Iroha account and still wants to create a DID. As identifier we use UUID v4 (universally unique identifier, version 4). 

Example:

```=
did:sora:uuid:caab4570-5f3f-4050-8d61-15306dea4bcf
```

### 2.3 "ed" DID type

This type is used, when it is required to bind a DID to Ed25519 keypair. This may be needed to prevent some types of identity fraud: DID is not bound to any cryptographic suite, therefore users can create any DID. 
"ed" did-type prevents this by requiring to use a base58 encoded Ed25519 public key as a DID.

Example:
```C++=
// ed25519 hexencoded 32-byte pubkey: 
407e57f50ca48969b08ba948171bb2435e035d82cec417e18e4a38f5fb113f83
// "ed" did
did:sora:ed:5LkqENiDNdFpXiji8wPoVTWvRq2Q11vpKfNmufa6owUn
```

**proof** must contain a signature which can be verified with the public key stored in DID.



## 3. Sora DID Document

If a DID is the index key in a key-value pair, then the DID Document is the value to which the index key points: 


```
map[did] = document
e.g.
map[did:sora:uuid:caab4570-5f3f-4050-8d61-15306dea4bcf] = {..json-ld..}
```

### 3.1 DID Subject
The DID subject is the identifier that the DID Document is about, i.e., it is the DID described by DID Document. 


**Requirements**:
1. A DID Document MUST have exactly one DID subject.
2. The key for this property MUST be **id**.
3. The value of this key MUST be a valid [`DID`](#2-The-Sora-DID-Scheme).
4. When this DID Document is registered with the target distributed ledger or network, the registered DID MUST match this DID subject value.

```json=
{
    "id": "did:sora:iroha:bogdan@soramitsu.co.jp"
}
```

### 3.2 Public Keys

Public keys are used for digital signatures, encryption and other cryptographic operations, which in turn are the basis for purposes such as authentication or establishing secure communication with service endpoints. In addition, public keys may play a role in authorization mechanisms of DID CRUD operations.

The primary intention is that a DID Document lists public keys whose corresponding private keys are controlled by the entity identified by the DID ("owned" public keys). However, a DID Document MAY also list "non-owned" public keys.


**Requirements**:

1. A DID Document MUST include a **publicKey** property.
2. The value of the **publicKey** property SHOULD be an array of public keys.
3. Each public key MUST include **id** and **type** properties, and exactly one **value** property. *TODO: add link with ed25519-sha3-spec*.
4. Each public key MAY include an **owner** property, which identifies the entity that controls the corresponding private key. If this property is missing, it is assumed to be the DID subject.
6. The value property of a public key MUST be **publicKey<Pem|Base58|Hex>**


Example:

```JSON=
{
  "id": "did:sora:iroha:bogdan@soramitsu.co.jp",
  ...
  "publicKey": [{
    "id": "did:sora:iroha:bogdan@soramitsu.co.jp#keys-1",
    "type": "RsaVerificationKey2018",
    "publicKeyPem": "-----BEGIN PUBLIC KEY...END PUBLIC KEY-----\r\n"
  }, {
    "id": "did:sora:iroha:bogdan@soramitsu.co.jp#keys-2",
    "type": "Ed25519VerificationKey2018",
    // note, that this key is owned by other subject
    "owner": "did:sora:iroha:takemiya@soramitsu.co.jp",
    "publicKeyBase58": "H3C2AVvLMv6gmMNam3uVAjZpfkcJCwDwnZn6z3wXmqPV"
  }, {
    "id": "did:sora:iroha:bogdan@soramitsu.co.jp#keys-3",
    "type": "Secp256k1VerificationKey2018",
    // note, that this key is owned by other subject, defined with uuid
    "owner": "did:sora:uuid:6c6abdda-4f7a-489d-9119-ebc8a0d7097b",
    "publicKeyHex": "02b97c30de767f084ce3080168ee293053ba33b235d7116a3263d29f1450936b71"
  }],
  ...
}
```


### 3.4 Authentication

Authentication is the mechanism by which an entity can cryptographically prove that they are associated with a DID and DID Description. Note that Authentication is separate from Authorization because an entity may wish to enable other entities to update the DID Document (for example, to assist with key recovery) without enabling them to prove ownership (and thus be able to impersonate the entity).

**Requirements**:

1. A DID Document MUST include a **authentication** property.
2. The value of the **authentication** property should be an array of proof mechanisms.
3. Each proof mechanism MUST include the **type** property.
4. Each proof mechanism MAY embed or reference a public key (see Section [3.3 Public Keys](33-Public-Keys)).

Example with referenced public key:

```JSON=
{
  "id": "did:sora:iroha:bogdan@soramitsu.co.jp",
  ...
  // these keys can be used to authenticate as did:sora:iroha:bogdan@soramitsu.co.jp
  "authentication": [{
    "type": "RsaSignatureAuthentication2018",
    "publicKey": "did:sora:iroha:bogdan@soramitsu.co.jp#keys-1"
  }
  ],
  ...
}
```

Example with embedded public key:

```JSON=
{
  "id": "did:sora:iroha:bogdan@soramitsu.co.jp",
  ...
  "authentication": [{
    "type": "Secp256k1VerificationKey2018",
    "owner": "did:sora:uuid:6c6abdda-4f7a-489d-9119-ebc8a0d7097b",
    "publicKeyHex": "02b97c30de767f084ce3080168ee293053ba33b235d7116a3263d29f1450936b71"
  }
  ],
  ...
}
```

> ### ~~3.5 Authorization and Delegation~~
> 
> Authorization is the mechanism by which an entity states how one may perform operations on behalf of the entity. Delegation is the mechanism that an entity may use to authorize other entities to act on its behalf. Note that Authorization is separate from Authentication as explained in [Section 3.4 Authentication](#34-Authentication). This is particularly important for key recovery in the case of key loss, when the entity no longer has access to their keys, or key compromise, where the owner’s trusted third parties need to override malicious activity by an attacker. 
> 
> *TODO*

### 3.6 Service Endpoints (optional)

In addition to publication of authentication and authorization mechanisms, the other primary purpose of a DID Document is to enable discovery of service endpoints for the entity. A service endpoint may represent any type of service the entity wishes to advertise, including decentralized identity management services for further discovery, authentication, authorization, or interaction. The rules for service endpoints are:

1. A DID Document MAY include a service property.
2. The value of the service property should be an array of service endpoints.
3. Each service endpoint must include type and serviceEndpoint properties, and MAY include additional properties.
4. The service endpoint protocol SHOULD be published in an open standard specification.
5. The value of the serviceEndpoint property MUST be a valid URI conforming to [RFC3986] and normalized according to the rules in section 6 of [RFC3986] and to any normalization rules in its applicable URI scheme specification or a JSON-LD object.

Example:
```JSON=
{
    "service": [{
        "type": "IrohaService",
        "serviceEndpoint": "tcp://iroha.example.com:10003/"
    }, {
        "type": "DidResolverService",
        "serviceEndpoint": "https://example.com/"
    }]
}
```

### 3.7 Guardian

A guardian is an identity owner who creates and maintains an identity record for a dependent who is not in a position to hold or control the necessary cryptographic keys (e.g., a parent creating an identity record for a child). In this case, there are no owner keys to represent the ultimate identity owner. So the DDO needs to assert the identity of the guardian.

**Requirements**:

1. A DDO that includes an **owner** MAY have a **guardian**.
2. A DDO that does not include an **owner** MUST have a **guardian**.
3. The key for this property MUST be **guardian**.
4. The value of this key MUST be a valid DID.
5. The guardian DID MUST resolve to a DDO that has an owner property, i.e., guardian relationships must not be nested.

Example:
```JSON=
{
    "guardian": "did:sora:iroha:bogdan@soramitsu.co.jp"
}
```

### 3.8 Created

Standard metadata for identifier records includes a timestamp of the original creation. The rules for including a creation timestamp are:

**Requirements**:
1. A DID Document MUST have zero or one property representing a **creation** timestamp.
2. The key for this property MUST be **created**.
3. The value of this key MUST be a valid XML datetime value as defined in [section 3.3.7 of W3C XML Schema Definition Language (XSD) 1.1 Part 2: Datatypes](https://www.w3.org/TR/xmlschema11-2/).
4. This datetime value MUST be normalized to UTC 00:00 as indicated by the trailing "Z".

```JSON=
{
  "created": "2002-10-10T17:00:00Z"
}
```

### 3.9 Updated

Standard metadata for identifier records includes a timestamp of the most recent change. The rules for including a updated timestamp are:

1. A DID Document MUST have zero or one property representing an **updated** timestamp. It is RECOMMENDED to include this property.
2. The key for this property MUST be **updated**.
3. The value of this key MUST follow the formatting rules (3, 4, 5) from section [3.8 Created (Optional)](#38-Created-optional).
Example:

```JSON=
{
  "updated": "2016-10-17T02:41:00Z"
}
```

### 3.10 Proof

A proof on a DID Document is cryptographic proof of the integrity of the DID Document.

This proof is NOT proof of the binding between a DID and a DID Document. 

**Requirements**:

1. A DID Document MUST have exactly one property representing a proof.
2. The key for this property MUST be **proof**.
3. The value of this key MUST be an ARRAY of **valid** JSON-LD proofs as defined by [4 and 7 sections of Linked Data Proofs spec](https://w3c-dvcg.github.io/ld-signatures/). 
    - signature of every proof SHOULD NOT include other proofs.
    - signature of every proof MUST include "type", "created", "creator", "nonce" and "purpose" (if present) fields. *TODO: implement the crypto in library*
5. Every proof MUST be valid.
6. Every proof MUST have:
    - **type** - signature type
    - **created** - [created timestamp](#38-Created)
    - **creator** - URI to a public key of the signature creator
    - **signatureValue<Base58|Hex>** - signature data. 
    - **nonce** - a random string value.
7. Every proof MAY have:
    - **purpose** - short textual description, why "creator" created this proof.


Example:

```JSON=
{
  "proof": [{
    "type": "LinkedDataSignature2015",
    "created": "2016-02-08T16:02:20Z",
    "creator": "did:sora:iroha:bogdan@soramitsu.co.jp#keys-1",
    "signatureValueBase58": "QNB13Y7Q9...1tzjn4w=="
  }]
}
```

## 4. Sora DID Operations

According to section 6 of DID Spec, we need to implement a DID resolver - software component (API), which is capable of doing CRUD operations on DID documents. 
Every DID resolver MUST be available through base URL. We will use https://example.com as base URL through this specification.

In essence, these are operations on a global key-value map. All operations are PUBLIC, but modification operations are authorized with [**proof**](#310-Proof-optional).

If DID is an identifier, then DDO (Document).

Example of DDO for given DID: ```did:sora:ed:H3C2AVvLMv6gmMNam3uVAjZpfkcJCwDwnZn6z3wXmqPV```

```JSON=
{
  "id": "did:sora:ed:H3C2AVvLMv6gmMNam3uVAjZpfkcJCwDwnZn6z3wXmqPV",

  "publicKey": [{
    "id": "did:sora:ed:H3C2AVvLMv6gmMNam3uVAjZpfkcJCwDwnZn6z3wXmqPV#keys-1",
    "type": "RsaVerificationKey2018",
    "owner": "did:sora:ed:H3C2AVvLMv6gmMNam3uVAjZpfkcJCwDwnZn6z3wXmqPV",
    "publicKeyPem": "-----BEGIN PUBLIC KEY...END PUBLIC KEY-----\r\n"
  }, {
    "id": "did:sora:ed:H3C2AVvLMv6gmMNam3uVAjZpfkcJCwDwnZn6z3wXmqPV#keys-2",
    "type": "RsaEncryptionKey2018",
    "owner": "did:sora:ed:H3C2AVvLMv6gmMNam3uVAjZpfkcJCwDwnZn6z3wXmqPV",
    "publicKeyPem": "-----BEGIN PUBLIC KEY...END PUBLIC KEY-----\r\n"
  }, {
    "id": "did:sora:ed:H3C2AVvLMv6gmMNam3uVAjZpfkcJCwDwnZn6z3wXmqPV#keys-3",
    "type": "Ed25519VerificationKey2018",
    "owner": "did:sora:ed:H3C2AVvLMv6gmMNam3uVAjZpfkcJCwDwnZn6z3wXmqPV",
    "publicKeyBase58": "H3C2AVvLMv6gmMNam3uVAjZpfkcJCwDwnZn6z3wXmqPV"
  }],

  "authentication": [{
    // this key can be used to authenticate as this did
    "type": "RsaSignatureAuthentication2018",
    "publicKey": "did:sora:ed:H3C2AVvLMv6gmMNam3uVAjZpfkcJCwDwnZn6z3wXmqPV#keys-1"
  }],

  "created": "2002-10-10T17:00:00Z",

  "proof": [{
    "type": "Ed25519Signature2018",
    "created": "2002-10-10T17:00:00Z",
    "creator": "did:sora:ed:H3C2AVvLMv6gmMNam3uVAjZpfkcJCwDwnZn6z3wXmqPV#keys-3",
    "signatureValue": "QNB13Y7Q9...1tzjn4w=="
  }]
}
```
request: byte -> pojo -> serialize

Proof section must contain a signature by one of authenticated keys (**authentication** section).

### 4.1 Create / Register

This operation is used to register new DID in Identity System. In essence, Create operation is HTTP POST request with DDO encoded in JSON:

```HTTP=
POST /did HTTP/1.1
Host: example.com

{
  <content of DDO>    
}
```

Return codes:

| Code | Description |
| -------- | -------- | 
| 200     | Success. Returned when DDO is successfully registered.|
| 400 | Failure. Returned when DDO has invalid format. |
| 401 | Failure. Returned when DDO contains bad proof (invalid signature). |
| 422 | Failure. Returned when DID is already registered. |
| 500 | Failure. Returned in case of server errors. |


### 4.2 Read (Resolve)

This operation is used to query DDO given DID.

This is GET request:

```HTTP=
GET /did/:did HTTP/1.1
Host: example.com
```

- `:did` is [URLEncoded DID](https://www.urlencoder.org/). Example:
  `bogdan@soramitsu.co.jp` is `bogdan%40soramitsu.co.jp%0D%0A`

Example:
```HTTP=
GET /did/bogdan%40soramitsu.co.jp%0D%0A HTTP/1.1
Host: example.com
```

Return codes:
| Code | Description |
| -------- | -------- | 
| 200     | Success. Server returns DDO in response as JSON document. |
| 404 | Failure. DID is not registered. |
| 500 | Failure. Returned in case of server errors. |


### 4.3 Update (Replace)

This operation essentially is a "Replace" operation, e.g. old DDO is replaced with new DDO given DID.

This is HTTP PUT request:
```HTTP=
PUT /did/:did HTTP/1.1
Host: example.com

{
    <DDO content>
}
```

- `:did` is URLEncoded DID.

New DDO MUST contain [**updated**](#39-Updated-optional) property with time > **created** (e.g., DDO is updated after created).

Return codes:
| Code | Description |
| -------- | -------- | 
| 200     | Success. Returned when DDO was updated. |
| 400 | Failure. Returned when DDO has invalid format. |
| 401 | Failure. DDO contains bad proof (Not Authorized). |
| 404 | Failure. DID is not registered. |
| 500 | Failure. Returned in case of server errors. |

### 4.4 Delete/Revoke

This operation is used for DDO revocation or removal.
In essence, revocation is a removal of key from a global key-value map. E.g. `map[did]=null`.

This is DELETE HTTP request:

```HTTP=
DELETE /did/:did HTTP/1.1
Host: example.com

{
  "headers": "DELETE /did/:did HTTP/1.1\r\nHost: example.com",
  "proof": {
    "type": "Ed25519Signature2018",
    "created": "2002-10-10T17:00:00Z",
    "creator": "did:sora:ed:H3C2AVvLMv6gmMNam3uVAjZpfkcJCwDwnZn6z3wXmqPV#keys-3",
    "signatureValue": "QNB13Y7Q9...1tzjn4w=="
  }
}
```

- body
    - it MUST contain property **headers** with HTTP request headers.
    - it MUST contain property **proof** signed with one of [authentication keys](#34-Authentication).
- `:did` - URLEncoded DID.

Return codes:
| Code | Description |
| -------- | -------- | 
| 200     | Success. Returned when DDO was deleted. |
| 401 | Failure. DDO contains bad proof (Not Authorized). |
| 404 | Failure. DID is not registered. |
| 500 | Failure. Returned in case of server errors. |

## Non-DDO operations

### 1. Resolve public key

Given URI to a public key `did:sora:iroha:bogdan@soramitsu.co.jp#keys-1`, it is required to get its content.

Algorithm:
1. Let *did* be given key URI with removed `#keys-1`, e.g. `did:sora:iroha:bogdan@soramitsu.co.jp`
2. Perform [DDO resolution](#42-Read-Resolve)
3. Find a public key for given URI

---

# Sora Verifiable Claims Data Model Specification

This document follows original w3c https://w3c.github.io/vc-data-model/ specification and intended to clarify things, specific for Sora.

## 1. Claim

Claim is a statement about subject:
![](https://w3c.github.io/vc-data-model/diagrams/claim.svg)

It can be represented as JSON:
```JSON=
{
    "id": "subject",
    "property": "value"
}
```

Claim example: 

```JSON=
"claim": {
    "id": "did:sora:iroha:bogdan@soramitsu.co.jp",
    "ageOver": 21
}
```

Claims can be grouped together, effectively making single claim:
```JSON=
"claim": {
    "id": "did:sora:iroha:bogdan@soramitsu.co.jp",
    "ageOver": 21,
    "ageBelow": 30
}
```

## 2. ProtectedClaim

Since claims are published on a blockchain and thus they are public, it is very important to keep Users' personal information private. 

To accomplish this, we use **ProtectedClaim**. It looks like a Claim, but has additional "salt" property, which is used as a key for HMAC. It is needed to increase security against preimage attacks. 

The whole ProtectedClaim content is hashed with SHA3-256 [*TODO: what is the best way to hash it?*] and divided on two parts, public and private. Public part is signed and used to attest the private part.

```JSON=
// Private Part of the Claim
"claim": [{
    "id": "did:sora:iroha:bogdan@soramitsu.co.jp",
    "ageOver": "21",
    "salt": "salt1"
  }, {
    "id": "did:sora:iroha:bogdan@soramitsu.co.jp",
    "ageBelow": "30",
    "salt": "salt2"
  }]


// Public part of the Claim
// calculated as: 
// sha3_256(normalize<URDNA2015>(ProtectedClaim))
"claim": [
  "ffb9f9f3f61c88d07a45cfeb8b8311fcb82415aa791160d28a8bcff2abbc21c7", 
  "9b1d60ad7406609f4902779193487b3cec58e522435a16eaad98573d124d04b1", 
  "deeee9e2e4aef4daba6380fa9e46108c9f919c4820f5433f5cf8742bae293d0c"
]
```

For the reasons of privacy, Issuer MUST create single claim per attribute in the document. If Passport contains 10 fields, then "claim" should be an array of 10 ProtectedClaims.


## 3. Credential

**Requirements**:

1. Credential MAY contain Claims, if it does not affect User privacy.
2. Credential MUST contain public part of ProtectedClaim, if privacy is a concern.
3. Credential MUST contain the following properties:
   - "id" - crenential identifier. UUIDv4. MUST be unique.
   - "issuer" - DID of issuer.
   - "issued" - xsd:dateTime
   - "claim" - set of hexstrings
   - "proof" - same as [3.10 Proof](#310-Proof-optional)

Verifiable Credential is a Credenential, which has "proof" property (e.g. signed).

Verifiable Credential example:
```JSON=
{
    "id": "fe8f1f4d-d971-4e2b-8633-4a31c856fe04",
    "issuer": "did:sora:iroha:takemiya@soramitsu.co.jp",
    "issued": "2010-01-01T19:73:24Z",
    "claim": [
      "ffb9f9f3f61c88d07a45cfeb8b8311fcb82415aa791160d28a8bcff2abbc21c7",  
      "9b1d60ad7406609f4902779193487b3cec58e522435a16eaad98573d124d04b1", 
      "deeee9e2e4aef4daba6380fa9e46108c9f919c4820f5433f5cf8742bae293d0c"
    ],
    "proof": [{
      "type": "LinkedDataSignature2015",
      "created": "2016-02-08T16:02:20Z",
      "creator": "did:sora:iroha:takemiya@soramitsu.co.jp#keys-1",
      "signatureValue": "QNB13Y7Q9...1tzjn4w=="
    }]
}
```
