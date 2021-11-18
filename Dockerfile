FROM adoptopenjdk/openjdk11:alpine as build
COPY . .
RUN ./mvnw install

FROM adoptopenjdk/openjdk11:alpine-jre
COPY --from=build target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
