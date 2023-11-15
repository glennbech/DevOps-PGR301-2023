FROM maven:3.9-amazoncorretto-11 as builder
WORKDIR build

COPY pom.xml pom.xml
COPY src src

# Run maven package and cache maven dependencies between builds
RUN --mount=type=cache,target=/root/.m2 mvn -ntp package

FROM amazoncorretto:11.0.21-alpine3.18
WORKDIR app
COPY --from=builder build/target/*.jar app.jar
CMD java -jar app.jar