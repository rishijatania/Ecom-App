FROM openjdk:8-jdk-alpine
# Environment Variable that defines the endpoint of sentiment-analysis python api: 
EXPOSE 8080
ARG JAR_FILE=target/order-service-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8080","-Dhttps.protocols=TLSv1.2,TLSv1.1,TLSv1","-jar","/app.jar"]