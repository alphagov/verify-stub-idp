#!/bin/sh

. ../verify-build-scripts/check_for_library_updates.sh
./gradlew --daemon --parallel clean build test

check_for_library_updates ida-saml-extensions saml-serializers common-utils saml-security

tput setaf 3
printf "\nOnce you publish a new version of stub-idp-saml "
tput bold
printf "PLEASE DON'T FORGET "
tput sgr0
tput setaf 3
printf "to update the following dependent projects:\n"

printf "\n ida-hub"
printf "\n ida-compliance-tool"
printf "\n ida-stub-idp"

tput bold
printf "\nThank you! :)\n\n"
tput sgr0
