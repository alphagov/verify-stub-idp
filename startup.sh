#!/usr/bin/env bash

set -e

if test -e local.env; then
    set -a
    source local.env
    set +a
else
    printf "$(tput setaf 1)No local environment found. Use verify-local-startup or openssl to generate a local.env file\n$(tput sgr0)"
    exit
fi

source ../verify-local-startup/lib/services.sh
source ../verify-local-startup/config/env.sh

if test ! "$1" == "skip-build"; then
    ./gradlew clean build copyToLib
fi

if ! docker ps | grep stub-idp-postgres-db
then
    printf "$(tput setaf 3)Postgres not running... Attempting to start postgres using docker...\n$(tput sgr0)"
    docker run --rm -d -p 5432:5432 --name stub-idp-postgres-db postgres
fi

mkdir -p logs
start_service stub-idp . configuration/stub-idp.yml $STUB_IDP_PORT
wait
