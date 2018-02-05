#!/usr/bin/env bash

set -eu

./login_to_paas.sh
#./pre-commit.sh

MANIFEST_FILE=deployment/manifest.yml
STUB_IDP_RESOURCES='deployment/resources'

cd ../ida-stub-idp
./gradlew -x test \
      pushToPaas \
      -PmanifestFile=$MANIFEST_FILE \
      -PincludeDirectories=$STUB_IDP_RESOURCES
