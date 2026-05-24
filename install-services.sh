#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "Installing CareLite build parent"
mvn -DskipTests install -N -f "${repo_root}/pom.xml"

services=(
  "services/api-gateway"
  "services/identity-service"
  "services/tenant-service"
  "services/clinic-service"
  "services/billing-service"
  "services/document-service"
  "services/notification-service"
  "services/audit-event-service"
  "services/batch-service"
)

for service in "${services[@]}"; do
  echo
  echo "Installing ${service}"
  mvn -DskipTests install -f "${repo_root}/${service}/pom.xml"
done

echo
echo "All CareLite services installed successfully."
