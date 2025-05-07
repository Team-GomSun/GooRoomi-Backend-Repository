#!/bin/bash
CURRENT_PID=$(pgrep -f .jar)
echo "$CURRENT_PID"
if [ -z "$CURRENT_PID" ]; then
        echo "no process"
else
        echo "kill $CURRENT_PID"
        kill -9 "$CURRENT_PID"
        sleep 3
fi

# 로그 디렉토리 설정
LOG_DIR="/home/ubuntu/gooroomi/gooroomi-prod-logs"
mkdir -p $LOG_DIR
chown ubuntu:ubuntu $LOG_DIR
chmod 750 $LOG_DIR

# Docker Compose 실행
cd /home/ubuntu/gooroomi
docker-compose down
docker-compose up -d

# 애플리케이션 실행
JAR_PATH="/home/ubuntu/gooroomi/gooroomi-0.0.1-SNAPSHOT.jar"
echo "jar path : $JAR_PATH"
chmod +x $JAR_PATH
nohup java -jar $JAR_PATH --spring.profiles.active=prod > "$LOG_FILE" 2>&1 &