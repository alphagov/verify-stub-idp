#!/usr/bin/env bash

set -eu

: "${DOCKER_TAG:=stub-idp:latest}"

cd "$(dirname "${BASH_SOURCE[0]}")"

./gradlew clean
./gradlew distZip -Pversion=local
docker build -t "$DOCKER_TAG" -f run.Dockerfile . 2>&1
echo "$DOCKER_TAG"
