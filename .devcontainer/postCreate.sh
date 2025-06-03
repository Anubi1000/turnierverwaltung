#!/bin/bash
set -e

cd Server
dotnet restore

cd ../Client
echo | pnpm install