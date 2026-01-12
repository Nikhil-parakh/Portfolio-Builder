# Stage 1: Build
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# Sab kuch copy karo
COPY . .

# ✅ YE LINE ADD KARO (Debugging ke liye)
# Ye logs me print karega ki folder me kya kya files aayi hain
RUN echo "Folder contents:" && ls -la

# Build run karo
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM openjdk:17.0.1-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]