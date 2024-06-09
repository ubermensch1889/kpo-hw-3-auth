FROM openjdk:17

COPY target/*.jar kpo-auth.jar

ENTRYPOINT ["java", "-jar", "kpo-auth.jar"]

EXPOSE 8081
