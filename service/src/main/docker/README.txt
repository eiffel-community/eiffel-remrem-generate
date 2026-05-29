Build RemRem-Generate Docker image:
1. Build RemRem-Generate service artifact:
cd (git root dir)/
mvn clean package -DskipTests -pl service/ -am

2. Build RemRem-Generate Docker image:
cd (git root dir)
docker build -t remrem-generate:<version> --build-arg URL=./service/target/generate-service-<version>.war -f service/src/main/docker/Dockerfile .

3. Run RemRem-Generate
docker run --name remrem-generate -p 8080:8080 -v ./service/src/main/resources/application.properties:/usr/local/tomcat/config/application.properties  remrem-generate:<version>
