#!/bin/bash
set -e

DOMAIN=$(/opt/elasticbeanstalk/bin/get-config environment -k DUCKDNS_DOMAIN)
TOKEN=$(/opt/elasticbeanstalk/bin/get-config environment -k DUCKDNS_TOKEN)
SUBDOMAIN="${DOMAIN%.duckdns.org}"

if [ ! -x /usr/bin/caddy ]; then
  curl -fsSL "https://caddyserver.com/api/download?os=linux&arch=amd64" -o /usr/bin/caddy
  chmod +x /usr/bin/caddy
fi

mkdir -p /etc/caddy
cat > /etc/caddy/Caddyfile <<CADDYFILE
${DOMAIN} {
    reverse_proxy 127.0.0.1:8080
}
CADDYFILE

cat > /etc/cron.d/duckdns <<CRON
*/5 * * * * root curl -fsS "https://www.duckdns.org/update?domains=${SUBDOMAIN}&token=${TOKEN}&ip=" >/dev/null 2>&1
CRON
chmod 644 /etc/cron.d/duckdns

# 최초 1회 즉시 IP 갱신 (배포 직후 도메인이 최신 인스턴스 IP를 가리키도록)
curl -fsS "https://www.duckdns.org/update?domains=${SUBDOMAIN}&token=${TOKEN}&ip=" >/dev/null 2>&1 || true

systemctl daemon-reload
systemctl enable caddy
systemctl restart caddy
