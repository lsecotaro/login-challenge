# Use the Gradle base image with JDK 11
FROM gradle:7.4-jdk11

# Set the working directory inside the container
WORKDIR /app

# Copy only the Gradle wrapper and configuration files to leverage Docker layer caching
COPY gradle/ gradle/
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .
COPY gradle.properties .

# Pre-download dependencies to optimize subsequent builds
RUN ./gradlew dependencies

# Copy the rest of the application code
COPY . .

# Build the application
RUN ./gradlew build

# Expose the application port (replace 8080 with your application's port)
EXPOSE 8080

# Set the command to run the application
CMD ["java", "-jar", "build/libs/login-challenge.jar"]
