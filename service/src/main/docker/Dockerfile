FROM openjdk:17
MAINTAINER Eiffel-Community

# Expose port 8080. The port can be accessed using option -p or -P of
# docker's command 'run'.
EXPOSE 8080

ARG URL

# Explicitly select the file to pass to "java -jar" so that additional
# jar dependencies can be added to ${JAVA_APP_DIR} without creating
# ambiguity.
ENV JAVA_APP_JAR="${JAVA_APP_DIR}/generate.war"

# Disable Jolokia and jmx_exporter.
ENV AB_OFF true

RUN echo "Building RemRem-Generate image based on artifact url: ${URL}"
ADD ${URL} ${JAVA_APP_JAR}

# Start the service
ENTRYPOINT /usr/java/openjdk-17/bin/java -jar "${JAVA_APP_JAR}"
