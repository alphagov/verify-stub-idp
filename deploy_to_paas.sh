#!/usr/bin/env bash

set -eu
ROOT_DIR="$(dirname "$0")"
cd "$ROOT_DIR"
export CF_HOME="$ROOT_DIR/work"

function cleanup {
  rm -rf "$ROOT_DIR/work"
  unset CF_HOME
}
trap cleanup EXIT

cfSetEnvironmentVariables() {
  cf set-env ida-stub-idp STUB_IDP_SIGNING_PRIVATE_KEY $STUB_IDP_SIGNING_PRIVATE_KEY
  cf set-env ida-stub-idp STUB_IDP_SIGNING_CERT $STUB_IDP_SIGNING_CERT
  cf set-env ida-stub-idp METADATA_TRUSTSTORE $METADATA_TRUSTSTORE
  cf set-env ida-stub-idp STUB_IDPS_FILE_PATH /app/ida-stub-idp/resources/$ENVIRONMENT/stub-idps.yml
  cf set-env ida-stub-idp METADATA_URL $PAAS_METADATA_URL

  # Required by eidas even though its disabled in PAAS.
  cf set-env ida-stub-idp STUB_COUNTRY_SIGNING_PRIVATE_KEY $STUB_COUNTRY_SIGNING_PRIVATE_KEY
  cf set-env ida-stub-idp STUB_COUNTRY_SIGNING_CERT $STUB_COUNTRY_SIGNING_CERT
}

cfSetDatabaseUri() {
   cf unset-env ida-stub-idp DB_URI
   LOCAL_DB_URI="$(cf env ida-stub-idp | grep -o '"jdbc:postgresql://[^"]*' | tr -d '"' |sed 's/\\u0026/\&/g')"
   cf set-env ida-stub-idp DB_URI "$LOCAL_DB_URI"
}

cfBindWithDatabase() {
    cf bind-service ida-stub-idp ida-stub-idp-db
}

cfBindWithLogit() {
  cf bind-service ida-stub-idp ida-stub-idp-logit
}

cfDeployStubIDP() {
  MANIFEST_FILE=manifest.yml
  ARTIFACT_LOCATION="https://artifactory.ida.digital.cabinet-office.gov.uk/artifactory/remote-repos/uk/gov/ida/ida-stub-idp/$ARTIFACT_BUILD_NUMBER/ida-stub-idp-$ARTIFACT_BUILD_NUMBER.zip"
  curl -s ${ARTIFACT_LOCATION} --output "ida-stub-idp-$ARTIFACT_BUILD_NUMBER.zip"
  cf push -f $MANIFEST_FILE -p "ida-stub-idp-$ARTIFACT_BUILD_NUMBER.zip" --hostname $HOSTNAME
}

./login_to_paas.sh "$ROOT_DIR"
cfSetEnvironmentVariables
# This step assumes that postgres database service (named 'ida-stub-idp-db') would already be present
# If it's not present, please use create_database_service_on_pass to create it before running this script
cfBindWithDatabase
cfSetDatabaseUri
# This step assumes that logit service (named 'ida-stub-idp-logit') would already be present
# If it's not present, please use create_logit_service to create it before running this script
cfBindWithLogit
cfDeployStubIDP
