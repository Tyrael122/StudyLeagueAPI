FROM amazoncorretto:21.0.2

COPY target/*.jar app.jar

ENV TZ="America/Sao_Paulo"

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]