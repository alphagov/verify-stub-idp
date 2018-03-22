#!/usr/bin/env bash

set -eu
ROOT_DIR="$(dirname "$0")"
cd "$ROOT_DIR"
export CF_HOME="$ROOT_DIR/work"

APP_NAME='ida-stub-idp'
TEST_APP_NAME="$APP_NAME-test"

TEST_HOSTNAME=$HOSTNAME-test
CF_DOMAIN=cloudapps.digital

function cleanup {
  rm -rf "$ROOT_DIR/work"
  unset CF_HOME
}
trap cleanup EXIT

cfSetEnvironmentVariables() {
  cf set-env $TEST_APP_NAME STUB_IDP_SIGNING_PRIVATE_KEY "$STUB_IDP_SIGNING_PRIVATE_KEY"
  cf set-env $TEST_APP_NAME STUB_IDP_SIGNING_CERT "$STUB_IDP_SIGNING_CERT"
  cf set-env $TEST_APP_NAME METADATA_TRUSTSTORE "$METADATA_TRUSTSTORE"
  cf set-env $TEST_APP_NAME STUB_IDPS_FILE_PATH "/app/ida-stub-idp/resources/$ENVIRONMENT/stub-idps.yml"
  cf set-env $TEST_APP_NAME METADATA_URL "$PAAS_METADATA_URL"
  cf set-env $TEST_APP_NAME METADATA_ENTITY_ID "$METADATA_ENTITY_ID"

  # Required by eidas
  cf set-env $TEST_APP_NAME STUB_COUNTRY_SIGNING_PRIVATE_KEY "$STUB_COUNTRY_SIGNING_PRIVATE_KEY"
  cf set-env $TEST_APP_NAME STUB_COUNTRY_SIGNING_CERT "$STUB_COUNTRY_SIGNING_CERT"
  cf set-env $TEST_APP_NAME STUB_IDP_HOSTNAME "${HOSTNAME}.${CF_DOMAIN}"
  cf set-env $TEST_APP_NAME HUB_CONNECTOR_ENTITY_ID "https://hub-connector-eidas-${ENVIRONMENT}.${CF_DOMAIN}/metadata.xml"
}

cfSetDatabaseUri() {
   LOCAL_DB_URI="$(cf env $TEST_APP_NAME | grep -o '"jdbc:postgresql://[^"]*' | tr -d '"' |sed 's/\\u0026/\&/g')"
   cf set-env $TEST_APP_NAME DB_URI "$LOCAL_DB_URI"
}

cfBindWithDatabaseAndLogit() {
  cf bind-service $TEST_APP_NAME ida-stub-idp-db
  cf bind-service $TEST_APP_NAME ida-stub-idp-logit
}

cfPushArtifact() {
  ARTIFACT_LOCATION="https://artifactory.ida.digital.cabinet-office.gov.uk/artifactory/remote-repos/uk/gov/ida/ida-stub-idp/$ARTIFACT_BUILD_NUMBER/ida-stub-idp-$ARTIFACT_BUILD_NUMBER.zip"
  curl -s ${ARTIFACT_LOCATION} --output "ida-stub-idp-$ARTIFACT_BUILD_NUMBER.zip"
  cf push $TEST_APP_NAME -f manifest.yml --no-start -p "ida-stub-idp-$ARTIFACT_BUILD_NUMBER.zip" --hostname $TEST_HOSTNAME
}

cfBlueGreenDeployment() {
  cf start $TEST_APP_NAME
  checkServiceStatus "$TEST_HOSTNAME" "$TEST_APP_NAME"

  cf map-route $TEST_APP_NAME $CF_DOMAIN --hostname $HOSTNAME
  cf unmap-route $TEST_APP_NAME $CF_DOMAIN --hostname $TEST_HOSTNAME
  cf unmap-route $APP_NAME $CF_DOMAIN --hostname $HOSTNAME

  cf delete -f $APP_NAME
  cf rename $TEST_APP_NAME $APP_NAME

  checkServiceStatus "$HOSTNAME" "$APP_NAME"
}

checkServiceStatus() {
  local HOST_NAME=$1
  local APP_NAME=$2
  if [ "$(curl -sL --retry 5 --retry-delay 10  -w "%{http_code}\\n" https://"$HOST_NAME.$CF_DOMAIN"/service-status)" != "200" ] ; then
    printf "$(tput setaf 1)Zero downtime deployment failed.\nUse 'cf logs $APP_NAME --recent' for more information.\n$(tput sgr0)"
    exit 1;
  fi
}

./login_to_paas.sh "$ROOT_DIR"

cfPushArtifact
cfSetEnvironmentVariables
# This step assumes that postgres service (named 'ida-stub-idp-db') and logit service (named 'ida-stub-idp-logit') would already be present
# Please use create_database_service_on_pass to create database before running this script
# Please use create_logit_service to create logit before running this script
cfBindWithDatabaseAndLogit
cfSetDatabaseUri
cfBlueGreenDeployment
