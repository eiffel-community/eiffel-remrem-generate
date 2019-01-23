FROM tomcat:8.0-jre8
LABEL maintainer Eiffel-Community 
ARG URL
RUN echo Bulding RemRem-Publish image based on artifact url: ${URL}
RUN ["rm", "-fr", "/usr/local/tomcat/webapps/ROOT"]
ADD ${URL} /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]
