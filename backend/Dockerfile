# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
# Descarga las dependencias primero para cachear esta capa
RUN mvn dependency:go-offline
COPY src ./src
# Compila el proyecto
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
# Copia el JAR compilado desde la etapa anterior
COPY --from=build /app/target/*.jar app.jar
# Expone el puerto que usa Spring Boot
EXPOSE 8080
# Inicia la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
