#!/usr/bin/env bash
set -euo pipefail

SSH_HOST="seunglol"
REMOTE_APP_DIR="/home/ubuntu/app"
REMOTE_JAR="extension-blocker.jar"
SERVICE_NAME="extension-blocker.service"
HEALTH_URL="http://localhost:8081/api/extensions/fixed"
HEALTH_RETRIES=15
HEALTH_INTERVAL=2

cd "$(dirname "$0")"

echo "==> 빌드 (테스트 포함)"
./gradlew build --console=plain

JAR_PATH=$(ls -t build/libs/*.jar | grep -v '\-plain\.jar$' | head -1)
if [ -z "$JAR_PATH" ]; then
  echo "빌드된 jar를 찾을 수 없습니다." >&2
  exit 1
fi
echo "==> 빌드 결과: $JAR_PATH"

echo "==> 서버로 전송"
scp "$JAR_PATH" "$SSH_HOST:$REMOTE_APP_DIR/${REMOTE_JAR}.new"

echo "==> 서버에서 교체 및 재시작"
ssh "$SSH_HOST" bash -s <<EOF
set -euo pipefail
cd "$REMOTE_APP_DIR"
if [ -f "$REMOTE_JAR" ]; then
  cp "$REMOTE_JAR" "${REMOTE_JAR}.prev"
fi
mv "${REMOTE_JAR}.new" "$REMOTE_JAR"
sudo systemctl restart "$SERVICE_NAME"
EOF

echo "==> 헬스체크"
for i in $(seq 1 "$HEALTH_RETRIES"); do
  if ssh "$SSH_HOST" "curl -sf $HEALTH_URL" > /dev/null 2>&1; then
    echo "==> 배포 성공"
    exit 0
  fi
  sleep "$HEALTH_INTERVAL"
done

echo "==> 헬스체크 실패, 이전 jar로 롤백"
ssh "$SSH_HOST" bash -s <<EOF
set -euo pipefail
cd "$REMOTE_APP_DIR"
if [ -f "${REMOTE_JAR}.prev" ]; then
  mv "${REMOTE_JAR}.prev" "$REMOTE_JAR"
  sudo systemctl restart "$SERVICE_NAME"
  echo "롤백 완료"
else
  echo "이전 jar가 없어 롤백할 수 없습니다. 수동 확인이 필요합니다." >&2
fi
EOF
echo "==> 배포 실패" >&2
exit 1
