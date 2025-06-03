FROM mcr.microsoft.com/dotnet/sdk:9.0

RUN curl -fsSL https://deb.nodesource.com/setup_24.x | bash - && \
    apt update && apt install -y nodejs && \
    corepack enable

RUN apt install -y debian-keyring debian-archive-keyring curl gnupg && \
    curl -1sLf 'https://dl.cloudsmith.io/public/caddy/stable/gpg.key' | gpg --dearmor -o /usr/share/keyrings/caddy-stable-archive-keyring.gpg && \
    curl -1sLf 'https://dl.cloudsmith.io/public/caddy/stable/debian.deb.txt' | tee /etc/apt/sources.list.d/caddy-stable.list && \
    apt update && apt install -y caddy

COPY ./postCreate.sh ./postStart.sh /scripts/