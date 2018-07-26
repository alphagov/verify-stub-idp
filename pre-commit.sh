#!/usr/bin/env bash

set -e

./gradlew --parallel clean build test intTest copyToLib 2>&1

./startup.sh skip-build

./kill-all-the-services.sh

echo SUCCESS!
