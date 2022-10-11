# Run RemRem-Generate Applciation

RemRem-Generate application can be executed with Maven command or with the maven built war file.

## Requirements

- Java
- Maven


## Run RemRem-Generate With Maven Command

1. Change to service directory: 
`cd service`

1. Execute maven command to build and run RemRem-Generate:
`mvn spring-boot:run`


## Run RemRem-Generate With Java Command

1. Change to service directory: 
`cd service`

2. Execute maven package command to build the RemRem-Generate war file:
`mvn package -DskipTests`

This will produce a war file in the "target" folder.
The RemRem-Generate released war file can be downloaded from JitPack.

1. Run RemRem-Generate application war file
There is some alternatives to execute the war file with java command.
`java -classpath service/target/generate-service-2.1.5.war org.springframework.boot.loader.WarLauncher`
or
`java -jar service/target/generate-service-2.1.5.war`


Provide customized RemRem-Generate application.properties configuration via the spring.config.location java flag which need to be appended to the java command line:
`-Dspring.config.location=/path/to/application.properties`


## Override RemRem-Generate Eiffel Protocol Version

Eiffel-RemRem Protocol versions is developed and released by Github project:
https://github.com/eiffel-community/eiffel-remrem-semantics

Eiffel-RemRem-Semantic versions is released in Jitpack and can be downloaded as jar file.
Eiffel-Remrem-Semantic releases version jar files:
https://jitpack.io/com/github/eiffel-community/eiffel-remrem-semantics

Example of one Eiffel-Semantic-version Jitpack download url address:
https://jitpack.io/com/github/eiffel-community/eiffel-remrem-semantics/2.2.1/eiffel-remrem-semantics-2.2.1.jar

Eiffel-RemRem-Generate is released with a built-in eiffel-semantic protocol version which can be overridden with a different eiffel-semantic version by adding the external eiffel-semantic version jar file to classpath.

Execute RemRem-Generate application with external eiffel-semantic version jar file to classpath:
`java -classpath /path/to/protocol/eiffel-remrem-semantics-2.2.2.jar:service/target/generate-service-2.1.5.war org.springframework.boot.loader.WarLauncher`

Another alternative (works only with some Java 8 versions):
`java -Djava.ext.dirs=/home/etobiak/git3/dockerfiles/common/eiffel-remrem-generate/proto/eiffel-remrem-semantics-2.2.2.jar -jar service/target/generate-service-2.1.5.war`

Go to http://localhost:8080/versions and "semanticsVersion" field should show the overridden eiffel-semantic version.

