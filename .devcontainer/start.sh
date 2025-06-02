#!/bin/bash
set -e

(cd Server && dotnet watch run) &
(cd Client && pnpm run dev) &
caddy run --config ./Util/Development.Caddyfile
