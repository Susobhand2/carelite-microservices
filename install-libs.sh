#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "Installing CareLite build parent"
mvn -DskipTests install -N -f "${repo_root}/pom.xml"

libs=(
  "libs/carelite-common"
  "libs/carelite-security"
  "libs/carelite-tenancy"
  "libs/carelite-observability"
  "libs/carelite-events"
  "libs/carelite-test-support"
)

for lib in "${libs[@]}"; do
  echo
  echo "Installing ${lib}"
  mvn -DskipTests install -f "${repo_root}/${lib}/pom.xml"
done

echo
echo "All CareLite libs installed successfully."
