$startDir = Get-Location
Set-Location (Join-Path -Path $PSScriptRoot -ChildPath "../Server/Install")

dotnet wix build -arch x64 -ext WixToolset.UI.wixext -culture de-DE -pdbtype none -o Turnierverwaltung.msi .\Package.wxs .\WixUI_InstallDir_NoLicense.wxs

Set-Location $startDir