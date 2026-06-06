FROM openjdk:8-jre-alpine

WORKDIR /app

COPY build/libs/finlink-1.0.0.jar app.jar

ENV TZ=Asia/Shanghai \
    JAVA_OPTS="-Xmx512m -Xms256m"

EXPOSE 10030

ENTRYPOINT exec java $JAVA_OPTS -jar app.jar
