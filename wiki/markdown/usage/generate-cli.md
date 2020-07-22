# REMReM Generate CLI

## Usage

For using Eiffel REMReM Generate CLI self executing **generate-cli.jar** file should be executed in command line with parameters described below.

```
java -jar generate-cli.jar -h

You passed help flag.
usage: java -jar

 -d,--debug                  enable debug traces

 -f,--content_file           message content file

 -h,--help                   show help

 -json,--json_content        JSON content

 -mp,--messaging_protocol    name of messaging protocol to be used, e.g. eiffelsemantics

 -lv,--lenientValidation     lenientValidation will perform the only mandatory field validation and non-mandatory validation failures will place in Eiffel message as a new property(remremGenerateFailures)

 -r,--response_file          file to store the response in, optional

 -t,--message_type           message type, mandatory if -f or -json is given

 -v,--list_versions          lists the versions of generate and all loaded protocols
 ```


**Generate templates for Eiffel REMReM Semantics to be used are available [here.](https://github.com/eiffel-community/eiffel-remrem-semantics)**

The message must exist in a JSON file, for example **_eiffelactivityfinished.json_** might contain the following message:

```
{
    "msgParams": {
        "meta": {
            "type": "EiffelActivityFinishedEvent",
            "version": "3.0.0",
            "tags": [""],
            "source": {
                "domainId": "",
                "host": "",
                "name": "",
                "uri": ""
            }
        }
    },
    "eventParams": {
        "data": {
            "outcome": {
                "conclusion": "required if outcome present, can be one of SUCCESSFUL,UNSUCCESSFUL,FAILED,ABORTED,TIMED_OUT,INCONCLUSIVE",
                "description": ""
            },
            "persistentLogs": [{
                "name": "required if persistentLogs present",
                "uri": "required if persistentLogs present"
            }],
            "customData": [{
                "key": "required if customData present",
                "value": "required if customData present"
            }]
        },
        "links": [{
            "type": "ACTIVITY_EXECUTION",
            "target": "required - UUID of EiffelActivityTriggeredEvent"
            },
            {
            "type": "CAUSE or CONTEXT or FLOW_CONTEXT - optional",
            "target": "required - UUID if type is present"
        }]
    }
}
```

## Examples

Typical examples of usage Eiffel REMReM Generate CLI are described below.

##### With input from the example file shown above and output to a file:

```
$ java -jar generate-cli.jar -f eiffelactivityfinished.json -t eiffelactivityfinishedevent -r output.json
```

##### Same example, but output the result on the console:

```
$ java -jar generate-cli.jar -f eiffelactivityfinished.json -t eiffelactivityfinished
```

##### Same example, but this time the data is supplied as a command line argument:

```
$ java -jar generate-cli.jar -t eiffelactivityfinished -json {"msgParams":{"meta":{"type":"EiffelActivityFinishedEvent","tags":["tag1","tag2"],"source":{"domainId":"example.domain","host":"host","name":"name","uri":"http://java.sun.com/j2se/1.3/","serializer":"pkg:maven/com.github.eiffel-community/eiffel-remrem-semantics@2.0.0"},"security":{"sdm":{"authorIdentity":"test","encryptedDigest":"sample"}}}},"eventParams":{"data":{"outcome":{"conclusion":"SUCCESSFUL"},"persistentLogs":[{"name":"firstLog","uri":"http://myHost.com/firstLog"},{"name":"otherLog","uri":"isbn:0-486-27557-4"}]},"links":[{"type":"ACTIVITY_EXECUTION","target":"aaaaaaaa-bbbb-5ccc-8ddd-eeeeeeeeeee1"}]}}
```

##### Lenient Validation example:

#### Input
```
$ java -jar generate-cli.jar -t EiffelArtifactCreatedEvent -lv true -json "{'msgParams':{'meta':{'type':'EiffelArtifactCreatedEvent','version':'3.0.0','tags':[123,'tag2'],'source':{'domainId':'domainID','host':'host','name':'name','uri':'http:\/\/java.sun.com\/j2se\/1.3\/','serializer':'pkg:maven'},'security':{'authorIdentity':'test','encryptedDigest':'sample'}}},'eventParams':{'data':{'gav':{'groupId':'G','artifactId':'A','version':'V'},'fileInformation':[{'name':'name'}],'buildCommand':'trigger','requiresImplementation':'NONE','name':'event','dependsOn':[],'implement':[],'identity':'pkg:abc','customData':[{'key':'firstLog','value':'http:\/\/myHost.com\/firstLog'},{'key':'otherLog','value':'http:\/\/myHost.com\/firstLog33'}]},'links':[{'type':'CAUSE','target':'aaaaaaaa-bbbb-5ccc-8ddd-eeeeeeeeeee1'},{'type':'PREVIOUS_VERSION','target':'aaaaaaaa-bbbb-5ccc-8ddd-eeeeeeeeeee2'},{'type':'COMPOSITION','target':'aaaaaaaa'},{'type':'ENVIRONMENT','target':'aaaaaaaa-bbbb-5ccc-8ddd-eeeeeeeeeee3'}]}}"
```
#### Output

```
[{"meta":{"id":"8afff3b8-96b3-405c-85a5-366bc5f59cc6","type":"EiffelArtifactCreatedEvent","version":"3.0.0","time":1595395508961,"tags":["123","tag2"],"source":{"domainId":"domainID","host":"host","name":"name","serializer":"pkg:maven","uri":"http://java.sun.com/j2se/1.3/"},"security":{"authorIdentity":"test","sequenceProtection":[]}},"data":{"identity":"pkg:abc","fileInformation":[{"name":"name","tags":[]}],"buildCommand":"trigger","requiresImplementation":"NONE","dependsOn":[],"implements":[],"name":"event","customData":[{"key":"firstLog","value":"http://myHost.com/firstLog"},{"key":"otherLog","value":"http://myHost.com/firstLog33"},{"remremGenerateFailures":[{"type":"pattern","message":"ECMA 262 regex \"^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$\" does not match input string \"aaaaaaaa\"","path":"/links/2/target"}]}]},"links":[{"type":"CAUSE","target":"aaaaaaaa-bbbb-5ccc-8ddd-eeeeeeeeeee1"},{"type":"PREVIOUS_VERSION","target":"aaaaaaaa-bbbb-5ccc-8ddd-eeeeeeeeeee2"},{"type":"ENVIRONMENT","target":"aaaaaaaa-bbbb-5ccc-8ddd-eeeeeeeeeee3"}]}]
```

##### For loading protocol jars other than _eiffelsemantics_:

```
$ java -Djava.ext.dirs="/path/to/jars/" -jar generate-cli.jar -f eiffeljobfinished.json -t eiffeljobfinished -mp protocoltype
```

**NOTE:** in the above example, protocol jar file must be present inside "/path/to/jars/" folder.

**NOTE:** `-Djava.ext.dirs` is no longer working in some JAVA8 versions and in JAVA9. So users should create a wrapper project to include both Generate/Publish and their protocol in or place the protocol library in the folder for external dependencies of their JVM installation.

Typical output for these examples is:

```
[
   {
      "meta":{
         "id":"534c0b84-9348-422b-8bee-c4023488cd5d",
         "type":"EiffelActivityFinishedEvent",
         "version":"3.0.0",
         "time":1521534547653,
         "tags":[
            "tag1",
            "tag2"
         ],
         "source":{
            "domainId":"example.domain",
            "host":"host",
            "name":"name",
            "serializer":"pkg:maven/com.github.eiffel-community/eiffel-remrem-semantics@2.0.0",
            "uri":"http://java.sun.com/j2se/1.3/"
         },
         "security":{
            "sdm":{
               "authorIdentity":"test",
               "encryptedDigest":"sample"
            }
         }
      },
      "data":{
         "outcome":{
            "conclusion":"SUCCESSFUL"
         },
         "persistentLogs":[
            {
               "name":"firstLog",
               "uri":"http://myHost.com/firstLog"
            },
            {
               "name":"otherLog",
               "uri":"isbn:0-486-27557-4"
            }
         ],
         "customData":[

         ]
      },
      "links":[
         {
            "type":"ACTIVITY_EXECUTION",
            "target":"aaaaaaaa-bbbb-5ccc-8ddd-eeeeeeeeeee1"
         }
      ]
   }
]       
```

## Exit Codes

If CLI fails internally before trying to generate Eiffel message, user will get exit code. Exit codes are described in the table below.

|  Exit code   |                                     Description                                        |
| -------------| -------------------------------------------------------------------------------------- |
|       1      | User will get this exit code in case of some error that is not described below         |
|       2      | Some CLI options are missed                                                            |
|       3      | Unable to read content from passed file path                                           |
|       4      | Unable to read passed JSON string from command line                                    |
|       5      | Unable to send JSON message to message service                                         |
|       6      | Passed message protocol is not correct                                                 |

