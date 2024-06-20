# Usar una imagen base con Maven para construir la aplicación
FROM maven:3.8.4-openjdk-17-slim AS build

# Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar los archivos de configuración y el código fuente al contenedor
COPY pom.xml .
COPY src ./src

# Construir la aplicación
RUN mvn clean package -DskipTests

# Usar una imagen base más ligera para ejecutar la aplicación
FROM openjdk:17-jdk-slim

# Crear un volumen para almacenar archivos temporales
VOLUME /tmp

# Copiar el archivo JAR generado desde la etapa de construcción
COPY --from=build /app/target/order-0.0.1-SNAPSHOT.jar app.jar

# Definir el comando de entrada para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.profiles.active=docker"]
