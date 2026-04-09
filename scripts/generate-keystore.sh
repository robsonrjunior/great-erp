#!/usr/bin/env bash
set -euo pipefail

KEYSTORE_PATH="${KEYSTORE_PATH:-src/main/resources/config/tls/keystore.p12}"
KEY_ALIAS="${KEY_ALIAS:-great-erp}"
KEY_VALIDITY_DAYS="${KEY_VALIDITY_DAYS:-3650}"
KEY_DNAME="${KEY_DNAME:-CN=great-erp.local, OU=IT, O=Great ERP, L=Sao Paulo, ST=SP, C=BR}"

if ! command -v keytool >/dev/null 2>&1; then
  echo "Erro: keytool nao encontrado no PATH. Instale/configure o JDK." >&2
  exit 1
fi

if [[ ! -d "$(dirname "$KEYSTORE_PATH")" ]]; then
  mkdir -p "$(dirname "$KEYSTORE_PATH")"
fi

set +o pipefail
NEW_KEYSTORE_PASSWORD="$(tr -dc 'A-Za-z0-9' </dev/urandom | head -c 24)"
set -o pipefail

rm -f "$KEYSTORE_PATH"

keytool -genkeypair \
  -alias "$KEY_ALIAS" \
  -keyalg RSA \
  -keysize 2048 \
  -storetype PKCS12 \
  -keystore "$KEYSTORE_PATH" \
  -storepass "$NEW_KEYSTORE_PASSWORD" \
  -keypass "$NEW_KEYSTORE_PASSWORD" \
  -validity "$KEY_VALIDITY_DAYS" \
  -dname "$KEY_DNAME"

echo "Keystore gerado em: $KEYSTORE_PATH"
echo "Alias: $KEY_ALIAS"
echo "Nova senha (guarde em segredo): $NEW_KEYSTORE_PASSWORD"
