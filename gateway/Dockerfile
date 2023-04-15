FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar java-shareit-gateway.jar
#RUN bin/bash -c 'ln -snf /usr/share/zoneinfo/Europe/Moscow/etc/localtime'
ENTRYPOINT ["java","-jar","/java-shareit-gateway.jar"]

