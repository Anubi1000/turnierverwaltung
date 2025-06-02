$startDir = Get-Location
Set-Location (Join-Path -Path $PSScriptRoot -ChildPath "../Server.Tests")

dotnet test /p:CollectCoverage=true /p:CoverletOutputFormat=cobertura /p:CoverletOutput='./coverage.xml' /p:ExcludeByFile=**/Migrations/*.cs

Set-Location $startDir