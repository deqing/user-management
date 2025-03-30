FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY gradlew .
COPY gradlew.bat .
COPY gradle/ gradle/
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src/ src/

RUN chmod +x gradlew
RUN ./gradlew clean build -x test

WORKDIR /app

COPY ./build/libs/user-management-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 19001

ENTRYPOINT ["java", "-jar", "app.jar"]
