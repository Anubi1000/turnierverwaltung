$startDir = Get-Location
Set-Location (Join-Path -Path $PSScriptRoot -ChildPath "../Server")

dotnet tool restore
dotnet wix extension add -g WixToolset.UI.wixext

Set-Location $startDir