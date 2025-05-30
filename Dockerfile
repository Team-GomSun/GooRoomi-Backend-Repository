FROM openjdk:17-jdk

COPY ./build/libs/gooroomi-0.0.1-SNAPSHOT.jar gooroomi-backend.jar

ENTRYPOINT ["java", "-jar", "-Duser.timezone=Asia/Seoul", "/gooroomi-backend.jar"]