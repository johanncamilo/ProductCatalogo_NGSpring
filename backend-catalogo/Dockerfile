# ====== Build ======
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -B -DskipTests package

# ====== Runtime ======
FROM eclipse-temurin:21-jre
WORKDIR /app
ENV JAVA_OPTS=""
ENV SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/db_catalogo_ci
ENV SPRING_DATASOURCE_USERNAME=root
ENV SPRING_DATASOURCE_PASSWORD=12345678
COPY --from=build /app/target/backend-catalogo-0.0.1-SNAPSHOT.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]