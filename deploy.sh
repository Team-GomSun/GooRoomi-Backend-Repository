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

mkdir -p /home/ubuntu/gooroomi/gooroomi-prod-logs
chmod 755 /home/ubuntu/gooroomi/gooroomi-prod-logs

JAR_PATH="/home/ubuntu/gooroomi/gooroomi-0.0.1-SNAPSHOT.jar"
echo "jar path : $JAR_PATH"
chmod +x $JAR_PATH
nohup java -jar $JAR_PATH --spring.profiles.active=prod > /home/ubuntu/gooroomi/gooroomi-prod-logs/application.log 2>&1 &
