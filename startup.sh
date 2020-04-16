#!/usr/bin/env bash

set -e

if test -e local.env; then
    set -a
    source local.env
    set +a
else
    printf "%sNo local environment found. Use verify-local-startup or openssl to generate a local.env file\n%s" "$(tput setaf 1)" "$(tput sgr0)"
    exit
fi

source ../verify-local-startup/lib/services.sh
source ../verify-local-startup/config/env.sh

build_service ../verify-stub-idp

if ! docker ps | grep stub-idp-postgres-db >/dev/null
then
  printf "$(tput setaf 3)Postgres is required for stub-idp, attempting to start postgres using docker.\\n$(tput sgr0)"
  docker run --rm -d -p 5432:5432 -e POSTGRES_PASSWORD=docker --name stub-idp-postgres-db postgres >/dev/null
fi

start_service stub-idp ../verify-stub-idp/stub-idp configuration/local/stub-idp.yml $STUB_IDP_PORT
wait
