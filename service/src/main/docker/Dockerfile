FROM fabric8/java-centos-openjdk8-jre:1.6.4
MAINTAINER Eiffel-Community 
EXPOSE 8080
ARG URL

# Explicitly select the file to pass to "java -jar" so that additional
# jar dependencies can be added to ${JAVA_APP_DIR} without creating
# ambiguity.
ENV JAVA_APP_JAR ${JAVA_APP_DIR}/generate.war

# Disable Jolokia and jmx_exporter.
ENV AB_OFF true

RUN echo Building RemRem-Generate image based on artifact url: ${URL}
ADD --chown=jboss ${URL} ${JAVA_APP_JAR}
