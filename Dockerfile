FROM maven:3.5.3-jdk-8-alpine
COPY pom.xml .
COPY . .
WORKDIR .
RUN mvn package

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "./target/hack-0.0.1-jar-with-dependencies.jar"]