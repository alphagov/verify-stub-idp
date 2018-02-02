#!/usr/bin/env bash

set -eu

./login_to_paas.sh
#./pre-commit.sh

MANIFEST_FILE=deployment/manifest.yml
STUB_IDP_RESOURCES=deployment/resources/paas

cd ../ida-stub-idp
./gradlew -x test \
      pushToPaas \
      -PmanifestFile=$MANIFEST_FILE \
      -PincludeDirectory=$STUB_IDP_RESOURCES
