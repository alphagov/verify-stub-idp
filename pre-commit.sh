#!/usr/bin/env bash

./gradlew --parallel clean build integrationTest copyToLib 2>&1

./startup.sh skip-build
