FROM openjdk:20-jdk-slim

WORKDIR /app

COPY target/pi_javafxmllll-1.0-SNAPSHOT.jar app.jar

RUN apt-get update && apt-get install -y wget unzip && \
    wget https://download2.gluonhq.com/openjfx/17.0.2/openjfx-17.0.2_linux-x64_bin-sdk.zip && \
    unzip openjfx-17.0.2_linux-x64_bin-sdk.zip -d /opt && \
    rm openjfx-17.0.2_linux-x64_bin-sdk.zip

ENV PATH="${PATH}:/opt/javafx-sdk-17.0.2/bin"
ENV JAVAFX_PATH="/opt/javafx-sdk-17.0.2/lib"

ENTRYPOINT ["java", "--module-path", "/opt/javafx-sdk-17.0.2/lib", "--add-modules", "javafx.controls,javafx.fxml", "-jar", "app.jar"]
