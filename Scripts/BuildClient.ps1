$startDir = Get-Location
Set-Location (Join-Path -Path $PSScriptRoot -ChildPath "../Client")

pnpm run generate

Set-Location $startDir