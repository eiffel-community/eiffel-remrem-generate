## Configuration

#### Running and configuring RemRem-Generate without Tomcat installation

It is also possible to execute RemRem-Generate without external Tomcat installation and using the embedded tomcat in Spring application instead.

If RemRem-Generate is executed without external Tomcat installation, the configuration should be based on Spring application properties, see service/src/main/resources/application.properties file for available properties.

If running with java command, example:

```
java -jar service/target/generate-service-x.x.x.war --properties.parameter1=value --properties.parameter2=value --properties.parameter3=value ......
```

It is also possible to provide path to the spring properties file:

```
java -jar service/target/generate-service-x.x.x.war --spring.config.location=path/to/applicattion.propeties
```

Second option to execute RemRem-Generate is to execute maven commands from the source code root folder, example:

```
mvn spring-boot:run -Dspring-boot.run.arguments=--properties.parameter1=value,--properties.parameter2=value,--properties.parameter3=value,--properties.parameter4=value ......
```

When using maven command, Spring properties can also be changed by editing service/src/main/resources/application.properties file.

#### Running and configuring in Tomcat

Eiffel REMReM Service **generate-service.war** file should be deployed in Tomcat Server. For doing this, generate-service.war file should deployed in directory: _tomcat/webapps_.

Configuration is done in Tomcat using a config.properties file: _tomcat/conf/config.properties_.

**NOTE:** in each example assuming the generate-service.war is deployed in tomcat as **generate**.

**NOTE:** for other than eiffelsemantics protocol, provide the Java opts as:

```
set JAVA_OPTS="-Djava.ext.dirs=/path/to/jars/" in catalina.sh
```

**NOTE:** in the above example, protocol jar file must be present inside "/path/to/jars/" folder.

**NOTE:** "-Djava.ext.dirs" is no longer working in some JAVA8 versions and in JAVA9. So users should create a wrapper project to include both Generate/Publish and their protocol in or place the protocol library in the folder for external dependencies of their JVM installation.

### Jasypt configurations

Jasypt Spring Boot provides Encryption support for property sources(passwords, secret info ..etc) in Spring Boot Applications. To support this functionality in our application we need to add the following property in property file

**jasypt.encryptor.password: The key value which was used while encrypting the original password**

The above encryptor password must be same for both encryption and decryption of the original password(ldap,rabbitmq...etc)  
The encryptor password will be used by jasypt-spring-boot library in application to decrypt the encrypted password at runtime.

#### How to encrypt the password:

```
1) Download the jasypt jar file from any of the below locations
[Link to Jasypt](http://www.jasypt.org/download.html) (or) [Link to Maven](https://mvnrepository.com/artifact/org.jasypt/jasypt/1.9.2)

2) Execute the below command to generate Jasypt encrypted password
    java -cp jasypt-1.9.2.jar org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI input="any password" password=any intermediate key

input   : any password which we want to encrypt(Ex: rabbitmq password, ldap user password, etc...)
password: A Jasypt key used to encrypt the above input( The Jasypt key can be anything, but make sure same key to be used for decryption)

Example:
    Run the below command in Command line

    java -cp jasypt-1.9.2.jar org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI input="dummyPassword" password=dummy

output:
    ----ENVIRONMENT-----------------

    Runtime: Oracle Corporation Java HotSpot(TM) 64-Bit Server VM 25.144-b01

    ----ARGUMENTS-------------------

    algorithm: PBEWithMD5AndDES
    input: dummyPassword
    password: dummy

    ----OUTPUT----------------------

    euJcvto7NtCDiWT7BKFW0A==
```
 
Use the above encrypted password in your property file like this **ENC(encrypted password)**
 
 **Ex:rabbitmq.password: ENC(euJcvto7NtCDiWT7BKFW0A ==)**
 
**Note: REMReM will work without jasypt encryption also but if you have encrypted any text using jasypt library then** _jasypt.encryptor.password_ **property should be present in property file**
 
### Ldap authentication configurations
 
 Active Directory authentication can be enabled for the REMReM REST service by setting the configuration property 
 
   **activedirectory.generate.enabled:true**.

**NOTE:** it is recommended to disable ldap authentication for REMReM Publish Service for correct work of REMReM Publish Service endpoint 

   **/generateAndPublish.**

```   
activedirectory.generate.enabled: <true | false>
activedirectory.ldapUrl:          <LDAP server url>
activedirectory.managerPassword:  <LDAP server manager password>
activedirectory.managerDn:        <LDAP managerDn pattern>
activedirectory.rootDn:           <LDAP rootDn pattern>
activedirectory.userSearchFilter: <LDAP userSearchFilter pattern>
```
 
 **LDAP authentication without Base64 encryption of user details::**
 
```
 $ curl -XPOST -H "Content-Type: application/json" --user username:password --data @ActivityCanceled.json http://localhost:8080/generate-service/eiffelsemantics?msgType=eiffelactivitycanceled
```
 
 **NOTE:** each HTTP request must then include an Authorization header with value 
 
      **Basic <Base64 encoded username:password>**
      
**LDAP authentication with Base64 encryption of user details::**

```
$ curl -XPOST -H "Content-Type: application/json" -H 'Authorization: Basic cGFzc3dvcmQ=' --data @ActivityCanceled.json http://localhost:8080/generate-service/eiffelsemantics?msgType=eiffelactivitycanceled
```

### Event Repository Lookup Configurations

ER lookup configurations define whether to enable querying towards Event Repository or not. ER lookup configurations are enabled by configuring below property in configuration file.

```
 event-repository.enabled : <true | false>
```

**NOTE:** Allowed value for _event-repository.enabled_ is _true/false_.If the value is not one of this the service will be terminated.

If _event-repository.enabled_ is false then REMReM Generate will not Lookup ER data. Refer below snippet for instance,

```
            event-repository.enabled : false
            event-repository.url     : http(s)://<host>:<port>/<context-path>
```

If _event-repository.enabled_ is true then Event Repository URL should be mandatory.

```
            event-repository.enabled : true
            event-repository.url     : http(s)://<host>:<port>/<context-path>
```

**NOTE:** If Lookup configurations are enabled and EventRepository URL is not provided then the remrem service gets terminated.

```
            event-repository.enabled : true
            event-repository.url     :
```

### Lenient Validation Configurations

More about [Lenient Validation](../usage/lenientValidation.md).
```
 lenientValidationEnabledToUsers : <true | false>
``` 
