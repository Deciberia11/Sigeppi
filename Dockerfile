# ============================================================
# SIGEPPI - imagen Docker (Tomcat 10.1 + Java 17)
# ============================================================
# Construir:  docker build -t sigeppi .
# Ejecutar:   docker run -p 8080:8080 \
#               -e SIGEPPI_DB_URL="jdbc:postgresql://HOST/BD?sslmode=require" \
#               -e SIGEPPI_DB_USER="usuario" \
#               -e SIGEPPI_DB_PASS="clave" \
#               sigeppi
# Abrir:      http://localhost:8080/sigeppi/
# ============================================================

# --- Etapa 1: compilar el .war con Maven ---
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B package -DskipTests

# --- Etapa 2: ejecutar en Tomcat 10.1 ---
FROM tomcat:10.1-jdk17-temurin
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/target/sigeppi.war /usr/local/tomcat/webapps/sigeppi.war
EXPOSE 8080
CMD ["catalina.sh", "run"]
