#!/bin/bash

services=${@:-"stub-idp"}

for service in $services; do
  pkill -9 -f "${service}.jar"
done

# what about postgres?

exit 0
