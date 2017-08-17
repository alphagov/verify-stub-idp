STUB IDP SAML
=============

Responsible for SAML behaviours which are specific to the domain of IDPs (note that we only stub IDPs internally, hence *stub*-idp-saml). At a high level:

* Handling requests from Hub
* Generating responses for Hub

At a lower level this includes:

* Converting OpenSAML objects to IDP domain objects
* Converting IDP domain objects to OpenSAML objects
* Generating Matching Dataset Assertions

Common tasks (e.g. validating signatures) are handled by dependencies such as [saml-security](https://github.gds/gds/saml-security) and [saml-serialisers](https://github.gds/gds/saml-serialisers).

Currently used by [stub idp](https://github.gds/gds/ida-stub-idp) and [compliance tool](https://github.gds/gds/ida-compliance-tool).

Stub IDP SAML Test
------------------

`stub-idp-saml-test` is provided for services which require IDP-like behaviour to set up state for their tests. For example: in order to test hub we need to generate an example AuthnResponse.
`stub-idp-saml-test` provides helpful builders for situations like this.

<small>Note: this library is a slimmed down fork of [saml-lib](https://github.gds/gds/saml-lib)</small>

