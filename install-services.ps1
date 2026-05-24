$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent $MyInvocation.MyCommand.Path

Write-Host "Installing CareLite build parent" -ForegroundColor Cyan
mvn -DskipTests install -N -f (Join-Path $repoRoot "pom.xml")

$services = @(
    "services\api-gateway",
    "services\identity-service",
    "services\tenant-service",
    "services\clinic-service",
    "services\billing-service",
    "services\document-service",
    "services\notification-service",
    "services\audit-event-service",
    "services\batch-service"
)

foreach ($service in $services) {
    Write-Host ""
    Write-Host "Installing $service" -ForegroundColor Cyan
    mvn -DskipTests install -f (Join-Path $repoRoot "$service\pom.xml")
}

Write-Host ""
Write-Host "All CareLite services installed successfully." -ForegroundColor Green
