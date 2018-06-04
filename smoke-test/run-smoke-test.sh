#!/usr/bin/env sh
set -eu

docker-compose up -d selenium-hub
docker-compose build smoke-tester
docker-compose run \
               -e RP_URL=$RP_URL \
               -e IDP_NAME=$IDP_NAME \
               -e IDP_USERNAME=$IDP_USERNAME \
               -e IDP_PASSWORD=$IDP_PASSWORD \
               smoke-tester \
               paas_smoke_test.rb
docker-compose down

