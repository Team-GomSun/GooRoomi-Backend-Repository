#!/bin/bash

cd "$(dirname "$0")/.."

# 로그 디렉토리 생성
mkdir -p /home/ubuntu/gooroomi/gooroomi-prod-logs
chmod 755 /home/ubuntu/gooroomi/gooroomi-prod-logs

# Docker 네트워크 생성
docker network create gooroomi-network || true

# Blue 환경 시작
echo "Blue 환경을 시작합니다..."
docker-compose -f docker-compose.blue.yml build
docker-compose -f docker-compose.blue.yml up -d

# Blue 환경이 준비될 때까지 대기
echo "Blue 환경이 준비될 때까지 10초 대기..."
sleep 10

# Blue 환경 상태 확인
HEALTH_CHECK=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8081/actuator/health)

if [ "$HEALTH_CHECK" == "200" ]; then
  echo "Blue 환경 Health check 성공"
  
  # Green 환경도 시작
  echo "Green 환경을 시작합니다..."
  docker-compose -f docker-compose.green.yml build
  docker-compose -f docker-compose.green.yml up -d
  
  echo "Blue-Green 배포 환경이 준비되었습니다."
  echo "현재 Blue 환경이 활성화되어 있습니다."
else
  echo "Blue 환경 Health check 실패! 초기 설정을 중단합니다."
  docker-compose -f docker-compose.blue.yml down
  exit 1
fi 