#!/bin/bash

# Nginx conf 경로
NGINX_CONF="/etc/nginx/conf.d/gooroomi.conf"

# 현재 활성화된 환경 확인 (주석 처리 여부로 판단)
if grep -q '^\s*server 127.0.0.1:8081;' $NGINX_CONF; then
  CURRENT_ENV="blue"
else
  CURRENT_ENV="green"
fi

echo "현재 활성화된 환경: $CURRENT_ENV"

# 롤백할 환경 결정
echo "트래픽을 롤백합니다..."
if [ "$CURRENT_ENV" == "blue" ]; then
  TARGET_ENV="green"
else
  TARGET_ENV="blue"
fi

# 롤백 대상 환경 상태 확인
PORT=$(if [ "$TARGET_ENV" == "blue" ]; then echo "8081"; else echo "8082"; fi)
HEALTH_CHECK=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:$PORT/actuator/health")

if [ "$HEALTH_CHECK" == "200" ]; then
  echo "롤백 대상 환경 Health check 성공, Nginx 설정을 업데이트합니다..."
  
  # Nginx 설정에서 기존 활성화 서버를 주석 처리, 대상 서버를 활성화
  if [ "$TARGET_ENV" == "blue" ]; then
    sudo sed -i 's/^\s*#\?server 127.0.0.1:8081;/    server 127.0.0.1:8081;/' $NGINX_CONF
    sudo sed -i 's/^\s*server 127.0.0.1:8082;/    #server 127.0.0.1:8082;/' $NGINX_CONF
  else
    sudo sed -i 's/^\s*#\?server 127.0.0.1:8082;/    server 127.0.0.1:8082;/' $NGINX_CONF
    sudo sed -i 's/^\s*server 127.0.0.1:8081;/    #server 127.0.0.1:8081;/' $NGINX_CONF
  fi

  # Nginx 설정 리로드
  sudo nginx -s reload
  
  echo "Nginx 설정이 업데이트되었습니다. 트래픽이 $TARGET_ENV 환경으로 롤백됩니다."
else
  echo "롤백 대상 환경 Health check 실패! 롤백을 중단합니다."
  echo "문제를 해결한 후 다시 시도하세요."
  exit 1
fi 