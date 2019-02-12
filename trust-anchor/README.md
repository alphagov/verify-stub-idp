
# Verify Eidas Trust Anchor

### About Trust Anchor
[European identity schemes](https://ec.europa.eu/digital-single-market/en/e-identification) each have unique metadata containing their identity providers and public keys. Every metadata file is signed with a country-specific key which allows metadata consumers to trust its authenticity.

We collect certificates for connected European countries into one place and sign them all together with a Verify key.

Our relying parties can trust the collection of certificates because of the [GOV.UK Verify](https://gov.uk/verify) certificate, and then trust individual metadata files by using the collection. This signed collection is called the ‘signed trust anchor’.

GOV.UK Verify expresses these anchors as [JSON Web Keys (JWK)](https://tools.ietf.org/html/rfc7517) and serves them signed in compact [JSON Web Signature (JWS)](https://tools.ietf.org/html/rfc7515) format.

### Building the project

`./gradlew clean build`

## Licence

[MIT Licence](LICENCE)

This code is provided for informational purposes only and is not yet intended for use outside GOV.UK Verify
