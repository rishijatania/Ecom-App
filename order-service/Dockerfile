FROM openjdk:8-jdk-alpine
# Environment Variable that defines the endpoint of sentiment-analysis python api: 
EXPOSE 8080
ARG JAR_FILE=target/order-service-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar","-Dhttps.protocols=TLSv1.2,TLSv1.1,TLSv1"]