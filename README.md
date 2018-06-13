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

## Licence

[MIT Licence](LICENCE)

This code is provided for informational purposes only and is not yet intended for use outside GOV.UK Verify

