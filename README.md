# Verify Stub IDP

This microservice is a stub IDP that can be white-labelled to simulate any IDP, or used as a Stub eIDAS Proxy Service Node.

[![Build Status](https://travis-ci.org/alphagov/verify-stub-idp.svg?branch=master)](https://travis-ci.org/alphagov/verify-stub-idp)

## Note about the history of this repository

Commits made up to and including
83a97b5157c25f3dad56bc0293ef5e78d8a2b2a4 in this repository may have
had their history altered as part of the open sourcing process.  That
means the apparent changesets are incomplete due to removed files, and
thus the commit messages could not be representative of the changes
made at the time.

Merge commits were not preserved as part of the open sourcing process.

## Running

Use [verify-local-startup](https://github.com/alphagov/verify-local-startup) to start the app.  It will also create all the appropriate config/PKI to run the app locally.

## Configuring the app

A yaml file containing IDPs needs to be created and the file location set as  `stubIdpsYmlFileLocation` in the app yml config file

The format is as follows:

```yaml
stubIdps:
  - friendlyId: stub-idp-one-for-rp1
    displayName: Stub IDP One
    assetId: stub-idp-one
    sendKeyInfo: true
    idpUserCredentials:
        - user: userName
          password: <password hash>
```

The `friendlyId` can be used to enable multiple IDPs but using the same `displayName` and `assetId` e.g. for different on-boarding relying parties.

Header images for IDPs should be placed into `ida-stub-idp/src/main/resources/assets/images/providers/` and are referenced as `assetId` e.g. stub-idp-one.png is referenced as `stub-idp-one`

## Deploying to PaaS

Before Stub IDP can be deployed to PaaS, you need to provision two services in the space you are deploying to. Make sure you are logged into PaaS Cloudfoundry and then run `./create_paas_services.sh` as follows:

```
CF_SPACE=target-space LOGIT_ENDPOINT=endpoint ./create_paas_services.sh
```

The Logit endpoint can be obtained by logging into logit.io (via GApps) and copying the Verify Stubs stack ID.

## Connecting to a hub/using as an IDP

You need to use the entityId `http://stub_idp.acme.org/{friendlyId}/SSO/POST` or as the template configured in the main config file, with the shared key/cert configured in the main config file (`stub-idp.yml`)

The SSO URI for that IDP will be `http://localhost:50140/{friendlyId}/SAML2/SSO` or equivalent.

You need to set the hub (or service) entityId that messages will be received from/sent back to, as well as where the hub/service metadata can be received from -> see the `hubEntityId` and `metadata` configuration blocks.

# Using as a Stub eIDAS Proxy Service Node

Set the values in `europeanIdentity` including HUB_CONNECTOR_ENTITY_ID and CONNECTOR_NODE_METADATA_URI for the consuming relying party/service provider/hub. These values may be the same if the Connector Node metadata is published at the same URL as its entity ID.

Several countries can be dynamically stubbed at once - see the full list in [EidasScheme](src/main/java/uk/gov/ida/stub/idp/domain/EidasScheme.java) (use the values, not the enum keys).  Once the metadata is retrieved for each stub it can be used by the consuming relying party/service provider/hub.  Metadata is at http://localhost:50140/[scheme]/ServiceMetadata  Test users are all `stub-country*`.

## Test Users

Test users can be uploaded to the IDPs [docs](https://alphagov.github.io/rp-onboarding-tech-docs/pages/env/envEndToEndTests.html#createtestusers)

Basic auth for the `/{friendlyId}/users` endpoint is enabled by default and can be configured using `basicAuthEnabledForUserResource`

## Issues and responsible disclosure

If you think you have discovered a security issue in this code please email [disclosure@digital.cabinet-office.gov.uk](mailto:disclosure@digital.cabinet-office.gov.uk) with details.

For non-security related bugs and feature requests please [raise an issue](https://github.com/alphagov/verify-stub-idp/issues/new) in the GitHub issue tracker.

# Verify Stub IDP SAML

[![Build Status](https://travis-ci.org/alphagov/verify-stub-idp-saml.svg?branch=master)](https://travis-ci.org/alphagov/verify-stub-idp-saml)

Responsible for SAML behaviours which are specific to the domain of IDPs (note that we only stub IDPs internally, hence *stub*-idp-saml). At a high level:

* Handling requests from Hub
* Generating responses for Hub

At a lower level this includes:

* Converting OpenSAML objects to IDP domain objects
* Converting IDP domain objects to OpenSAML objects
* Generating Matching Dataset Assertions

Common tasks (e.g. validating signatures) are handled by dependencies such as [saml-security](https://github.com/alphagov/verify-saml-security) and [saml-serializers](https://github.com/alphagov/verify-saml-serializers).

Currently used by stub idp and compliance tool.

## Stub IDP SAML Test

`stub-idp-saml-test` is provided for services which require IDP-like behaviour to set up state for their tests. For example: in order to test hub we need to generate an example AuthnResponse.
`stub-idp-saml-test` provides helpful builders for situations like this.

### Building the project

`./gradlew clean build`

## Code of Conduct
This project is developed under the [Alphagov Code of Conduct](https://github.com/alphagov/code-of-conduct)

## Licence

[MIT Licence](LICENCE)

This code is provided for informational purposes only and is not yet intended for use outside GOV.UK Verify
