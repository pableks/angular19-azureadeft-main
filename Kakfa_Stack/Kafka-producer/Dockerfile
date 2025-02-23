#Dockerfile
FROM eclipse-temurin:22-jdk AS buildstage 

RUN apt-get update && apt-get install -y maven

#directorio de trabajo
WORKDIR /app

COPY pom.xml .
COPY src /app/src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:22-jdk 

# Crear directorio para el wallet
RUN mkdir -p /opt/oracle/wallet

# Copiar el jar
COPY --from=buildstage /app/target/Kafka-producer-0.0.1-SNAPSHOT.jar /app/kafka-producer.jar



#Puerto
EXPOSE 8086

CMD [ "java", "-jar", "/app/kafka-producer.jar" ]


