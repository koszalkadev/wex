# Build stage
FROM gradle:latest AS BUILD
WORKDIR /usr/app/
COPY . .
RUN gradle build

# Package stage
FROM amazoncorretto:21
EXPOSE 8080
VOLUME /tmp
COPY ./build/libs/wex-0.0.1-SNAPSHOT.jar wex.jar
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prod", "/wex.jar"]
