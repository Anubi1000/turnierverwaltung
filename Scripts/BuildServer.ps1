$startDir = Get-Location
Set-Location (Join-Path -Path $PSScriptRoot -ChildPath "../Server")

dotnet publish --no-restore /p:PublishProfile=./Properties/PublishProfiles/Win_x64.xml ./Turnierverwaltung.Server.csproj

Set-Location $startDir