#!/usr/bin/env bash

set -euo pipefail

cd "$(dirname "$0")"

stub_idp_fedconfig="$(pwd)/../verify-stub-idp-federation-config"

# Copy stub-idps.yml for each env into zip
mkdir -p stub-idp/src/dist/resources
rsync -qrv "$stub_idp_fedconfig/configuration/" stub-idp/src/dist/resources/

./gradlew --parallel --no-daemon clean test intTest

./gradlew --no-daemon \
  -Pversion="$BUILD_NUMBER" \
  -PstubidpExtraLogosDirectory="$stub_idp_fedconfig/idp-logos" \
  copyStubIdpLogos copyToLib distZip publish bintrayUpload

bin/build.rb

./gradlew --no-daemon outputDependencies -q > dependencies.properties
