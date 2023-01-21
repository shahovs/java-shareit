#FROM amazoncorretto:11-alpine-jdk
FROM bellsoft/liberica-openjdk-alpine-musl:11
#ENV TZ=Europe/Podgorica
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]