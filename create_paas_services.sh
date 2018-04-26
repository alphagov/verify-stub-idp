#!/usr/bin/env bash

set -eu

cf target -s "$CF_SPACE"

echo Creating ida-stub-idp-db Postgres service
cf create-service postgres Free ida-stub-idp-db

echo Creating ida-stub-idp-logit user-defined service
cf create-user-provided-service ida-stub-idp-logit -l "syslog-tls://${LOGIT_ENDPOINT}:21541"
