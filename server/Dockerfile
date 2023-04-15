FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar java-shareit-server.jar
#RUN bin/bash -c 'ln -snf /usr/share/zoneinfo/Europe/Moscow /etc/localtime'
ENTRYPOINT ["java","-jar","/java-shareit-server.jar"]