#!/bin/bash
set -e

dotnet dev-certs https

(cd Server && dotnet watch run) &
(cd Client && pnpm run dev) &
caddy run --config ./Util/Development.Caddyfile
