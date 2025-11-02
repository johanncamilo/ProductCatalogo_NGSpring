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
ENV SPRING_DATASOURCE_URL=jdbc:h2:file:/data/catalogo;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
COPY --from=build /app/target/backend-catalogo-0.0.1-SNAPSHOT.jar /app/app.jar
VOLUME ["/data"]
EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
