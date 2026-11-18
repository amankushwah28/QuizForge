
FROM maven:3.9-eclipse-temurin-25 AS build

WORKDIR /app

COPY backend/quizforge-backend/pom.xml ./pom.xml
COPY backend/quizforge-backend/src ./src

RUN mvn clean package -DskipTests

FROM tomcat:11-jre25-temurin

COPY --from=build /app/target/quizforge-backend-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/quizforge-backend-1.0-SNAPSHOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]
