#!/usr/bin/env bash

set -eu

cf target -s "$CF_SPACE"

echo Creating stub-idp-db-$ENV Postgres service
cf create-service postgres Free stub-idp-db-$ENV

echo Creating stub-idp-logit-$ENV user-defined service
cf create-user-provided-service stub-idp-logit-$ENV -l "syslog-tls://${LOGIT_ENDPOINT}:21541"
