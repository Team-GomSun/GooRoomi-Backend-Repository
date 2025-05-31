#!/bin/bash

# Nginx conf 경로
NGINX_CONF="/etc/nginx/conf.d/gooroomi.conf"

# 로그 디렉토리 생성
mkdir -p /home/ubuntu/gooroomi/gooroomi-prod-logs
chmod 755 /home/ubuntu/gooroomi/gooroomi-prod-logs

# Docker 네트워크 생성 (없으면 생성)
docker network create gooroomi-network 2>/dev/null || true

# 현재 활성화된 환경 확인 (주석 처리 여부로 판단)
if grep -q '^\s*server 127.0.0.1:8081;' $NGINX_CONF 2>/dev/null; then
  CURRENT_ENV="blue"
  TARGET_ENV="green"
else
  # 초기 배포이거나 green이 활성화된 경우
  CURRENT_ENV="green"
  TARGET_ENV="blue"
fi

echo "현재 활성화된 환경: $CURRENT_ENV"
echo "대상 환경: $TARGET_ENV"

# 기존에 실행 중인 대상 환경 컨테이너 정리
echo "기존 $TARGET_ENV 환경을 정리합니다..."
docker-compose -f /home/ubuntu/gooroomi/docker-compose.$TARGET_ENV.yml down 2>/dev/null || true
docker rm -f gooroomi-$TARGET_ENV 2>/dev/null || true
sleep 5  # 컨테이너가 완전히 종료될 때까지 대기

# 대상 환경 빌드 및 실행
echo "$TARGET_ENV 환경을 빌드하고 실행합니다..."
docker-compose -f /home/ubuntu/gooroomi/docker-compose.$TARGET_ENV.yml build
docker-compose -f /home/ubuntu/gooroomi/docker-compose.$TARGET_ENV.yml up -d

# 새 환경이 준비될 때까지 대기
echo "새 환경이 준비될 때까지 30초 대기..."
sleep 30

# 대상 환경 상태 확인
PORT=$(if [ "$TARGET_ENV" == "blue" ]; then echo "8081"; else echo "8082"; fi)
HEALTH_CHECK=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:$PORT/actuator/health")

if [ "$HEALTH_CHECK" == "200" ]; then
  echo "Health check 성공, Nginx 설정을 업데이트합니다..."
  # Nginx 설정에서 기존 활성화 서버를 주석 처리, 대상 서버를 활성화
  if [ "$TARGET_ENV" == "blue" ]; then
    sudo sed -i 's/^\s*#\?server 127.0.0.1:8081;/    server 127.0.0.1:8081;/' $NGINX_CONF
    sudo sed -i 's/^\s*server 127.0.0.1:8082;/    #server 127.0.0.1:8082;/' $NGINX_CONF
  else
    sudo sed -i 's/^\s*#\?server 127.0.0.1:8082;/    server 127.0.0.1:8082;/' $NGINX_CONF
    sudo sed -i 's/^\s*server 127.0.0.1:8081;/    #server 127.0.0.1:8081;/' $NGINX_CONF
  fi

  # Nginx 설정 리로드
  if command -v nginx >/dev/null 2>&1; then
    sudo nginx -s reload
    echo "Nginx 설정이 업데이트되었습니다. 트래픽이 $TARGET_ENV 환경으로 전환됩니다."
  else
    echo "Nginx가 설치되어 있지 않습니다. 설치 후 다시 시도하세요."
    exit 1
  fi
  
  # 이전 환경이 실행 중인지 확인하고, 실행 중이면 유지
  if docker ps | grep -q "gooroomi-$CURRENT_ENV"; then
    echo "이전 환경($CURRENT_ENV)은 롤백을 위해 유지됩니다."
  else
    echo "이전 환경이 실행 중이 아닙니다. 필요시 다시 배포하세요."
  fi
else
  echo "Health check 실패! 배포를 중단합니다."
  echo "컨테이너 로그를 확인합니다:"
  docker logs gooroomi-$TARGET_ENV --tail 50
  echo "$TARGET_ENV 환경을 중지합니다..."
  docker-compose -f /home/ubuntu/gooroomi/docker-compose.$TARGET_ENV.yml down || docker rm -f gooroomi-$TARGET_ENV 2>/dev/null || true
  exit 1
fi