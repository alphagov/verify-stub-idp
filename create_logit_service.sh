#!/usr/bin/env bash

set -eu

echo "Enter the Logstash endpoint"
read endpoint
echo "Enter the TCP-SSL port"
read port
cf create-user-provided-service ida-stub-idp-logit -l syslog-tls://$endpoint:$port