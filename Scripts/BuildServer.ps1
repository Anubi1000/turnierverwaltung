$startDir = Get-Location
Set-Location (Join-Path -Path $PSScriptRoot -ChildPath "../Server")

dotnet publish --no-restore -c ReleaseOptimized -r win-x64 --sc true /p:PublishSingleFile=true /p:PublishTrimmed=true

Set-Location $startDir