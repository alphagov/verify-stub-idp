#!/usr/bin/env bash

cd $(dirname "${BASH_SOURCE[0]}")

./gradlew clean
./gradlew distZip -Pversion=local
docker build -t stub-idp:latest -f run.Dockerfile . 2>&1
echo "stub-idp:latest"
