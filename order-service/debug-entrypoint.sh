#!/bin/sh
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8080 -Dhttps.protocols=TLSv1.2,TLSv1.1,TLSv1 -jar /app.jar