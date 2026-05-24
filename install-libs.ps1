$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent $MyInvocation.MyCommand.Path

Write-Host "Installing CareLite build parent" -ForegroundColor Cyan
mvn -DskipTests install -N -f (Join-Path $repoRoot "pom.xml")

$libs = @(
    "libs\carelite-common",
    "libs\carelite-security",
    "libs\carelite-tenancy",
    "libs\carelite-observability",
    "libs\carelite-events",
    "libs\carelite-test-support"
)

foreach ($lib in $libs) {
    Write-Host ""
    Write-Host "Installing $lib" -ForegroundColor Cyan
    mvn -DskipTests install -f (Join-Path $repoRoot "$lib\pom.xml")
}

Write-Host ""
Write-Host "All CareLite libs installed successfully." -ForegroundColor Green
