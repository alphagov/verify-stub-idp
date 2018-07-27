#!/usr/bin/env bash

set -e

. ../verify-build-scripts/check_for_library_updates.sh

./gradlew --parallel clean build test intTest copyToLib 2>&1

./startup.sh skip-build

./kill-all-the-services.sh

echo SUCCESS!

check_for_library_updates saml-extensions saml-serializers common-utils saml-security

tput setaf 3
printf "\nOnce you publish a new version of stub-idp-saml "
tput bold
printf "PLEASE DON'T FORGET "
tput sgr0
tput setaf 3
printf "to update the following dependent projects:\n"

printf "\n verify-hub"
printf "\n verify-test-rp"
printf "\n ida-compliance-tool"

tput bold
printf "\nThank you! :)\n\n"
tput sgr0
