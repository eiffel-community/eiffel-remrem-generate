## Logging

REMReM Generate application logging is implemented using the logback-classic. It requires user to configure the logback.xml file.  
When used without logback.xml log messages will look as below:

```
  %PARSER_ERROR[wEx]%PARSER_ERROR[clr] %PARSER_ERROR[clr] %PARSER_ERROR[clr] %PARSER_ERROR[clr]
       %PARSER_ERROR[clr] %PARSER_ERROR[clr] %PARSER_ERROR[clr]
```
To configure the logback.xml file use **-Dlogging.config=path/logback.xml**

To get info about logback configurations see [here](https://logback.qos.ch/manual/configuration.html).

To get sample logback.xml to use see [here](https://eiffel-community.github.io/eiffel-remrem-generate/logback/logback-sample.xml).

To enable log level changes at run time without restart using actuator endpoint. Do the changes using curl command like this:
curl -X POST http://localhost:8080/actuator/loggers/com.ericsson.eiffel.remrem -H "Content-Type: application/json" -d '{"configuredLevel": "DEBUG"}'