FROM registry.cn-hangzhou.aliyuncs.com/wxws/openjdk:8-jre

MAINTAINER lightdiary

RUN cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone

COPY lightdiary.jar /app.jar

EXPOSE 8081

ENTRYPOINT ["java", \
  "-Xms256m", "-Xmx512m", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-Ddruid.mysql.usePingMethod=false", \
  "-jar", "/app.jar"]
