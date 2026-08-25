# Etapa 1: Build (Compilación con Maven en Java 17)
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copia de configuraciones y código fuente
COPY pom.xml .
COPY src ./src

# Compilación omitiendo tests y documentación
RUN mvn clean package -DskipTests -Dasciidoctor.skip=true

# Etapa 2: Runtime (Imagen ligera Java 17 JRE)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copia explícita del JAR generado
COPY --from=build /app/target/apiNomina-0.0.1-SNAPSHOT.jar app.jar

# Puerto de la aplicación
EXPOSE 8080

# Comando de arranque
ENTRYPOINT ["java", "-jar", "app.jar"]