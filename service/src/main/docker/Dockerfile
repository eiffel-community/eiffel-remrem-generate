FROM tomcat:9-jdk21
LABEL org.opencontainers.image.authors="Eiffel-Community"

# Expose port 8080. The port can be accessed using option -p or -P of
# docker's command 'run'.
EXPOSE 8080

ARG URL

RUN echo "Building RemRem-Generate image based on artifact url: ${URL}"
ADD ${URL} /usr/local/tomcat/webapps/ROOT.war

