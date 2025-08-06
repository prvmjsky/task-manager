FROM eclipse-temurin:21-jdk

ARG GRADLE_VERSION=8.10.2

WORKDIR /

COPY ./ .

RUN ./gradlew --no-daemon dependencies

RUN ./gradlew --no-daemon build

EXPOSE 8080

CMD java -jar build/libs/app-0.0.1-SNAPSHOT.jar