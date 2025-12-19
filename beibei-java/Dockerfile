# Use a base image with Java 21
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the Maven wrapper and pom.xml
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Install dependencies (this layer will be cached if pom.xml doesn't change)
# Using -DskipTests to speed up the process, though running tests in build is often good practice
RUN ./mvnw dependency:go-offline

# Copy the source code
COPY src ./src

# Build the application
RUN ./mvnw package -DskipTests

# Run the jar file
# The jar file name depends on the artifactId and version in pom.xml
# beibei-java-0.1.jar matches <artifactId>beibei-java</artifactId> and <version>0.1</version>
CMD ["java", "-jar", "target/beibei-java-0.1.jar"]
