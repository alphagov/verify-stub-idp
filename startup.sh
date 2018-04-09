#!/usr/bin/env bash

if test -e local.env; then
    source local.env
else
    printf "$(tput setaf 1)No local environment found. Use verify-local-startup or openssl to generate a local.env file\n$(tput sgr0)"
    exit
fi

source ../verify-local-startup/lib/services.sh
source ../verify-local-startup/config/env.sh

if test ! "$1" == "skip-build"; then
    ./gradlew clean build copyToLib
fi

mkdir -p logs
start_service stub-idp . configuration/local/stub-idp.yml $STUB_IDP_PORT
wait
