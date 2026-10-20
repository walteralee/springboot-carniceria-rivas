# Imagen de Carnicería Rivas: Spring Boot con la web incluida
# Uso:  docker compose up -d   y abrir http://localhost:8080

# 1) Compilar el .jar
FROM eclipse-temurin:21-jdk AS build
WORKDIR /src
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw -B -q dependency:go-offline
COPY src src
RUN ./mvnw -B -q -DskipTests package

# 2) Ejecutar
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /src/target/carniceria-rivas.jar app.jar

# La base de datos se guarda en /app/datos (volumen)
EXPOSE 8080
VOLUME /app/datos

ENTRYPOINT ["java", "-jar", "app.jar"]
