# REMReM Generate Service

## Usage

REMReM Generate Service allows generating of Eiffel messages that will be send by [Eiffel REMReM Publish](https://github.com/eiffel-community/eiffel-remrem-publish) to RabbitMQ.

Information about the REMReM Generate Service all endpoints can be got and easily accessed using next links:

```
http://<host>:<port>/<application name>/
```

or 

```
http://<host>:<port>/<application name>/swagger-ui.html
```

Example:

```
http://localhost:8080/generate/
```

Configuration of REMReM Generate can be found [here](configuration.md)


Available REST resources for REMReM Generate Service are described below:

| Resource              | Method | Parameters                                                                                                                                  | Request body                                                                                                                                           | Description                                                                                                                                                                            |
|-----------------------|--------|---------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| /mp                   | POST   | mp - message protocol, required msgType - Eiffel event type, required failIfMultipleFound - default: false failIfNoneFound - default: false, lookupInExternalERs - default: false, lookupLimit - default: 1 *lookupLimit must be greater than or equals to 1, okToLeaveOutInvalidOptionalFields - default: false| { "msgParams": {"meta": {# Matches the meta object }},"eventParams": {"data": {# Matches the data object},"links": { # Matches the links object } }}   | This endpoint is used to generate Eiffel events and then the obtained event could  be published by [Eiffel REMReM Publish](https://github.com/eiffel-community/eiffel-remrem-publish). |
| /event_types/{mp}     | GET    | mp - message protocol, required                                                                                                             |                                                                                                                                                        | This endpoint is used to obtain Eiffel event types implemented in  [Eiffel REMReM Semantics](https://github.com/eiffel-community/eiffel-remrem-semantics).                             |
| /template/{type}/{mp} | GET    | type - Eiffel event type mp - message protocol, required                                                                                    |                                                                                                                                                        | This endpoint is used to obtain Eiffel event templates implemented in  [Eiffel REMReM Semantics](https://github.com/eiffel-community/eiffel-remrem-semantics).                         |
| /versions             | GET    |                                                                                                                                             |                                                                                                                                                        | This endpoint is used to get versions of generate service and all loaded protocols versions  in JSON format.                                                                           |
| /message_protocols    | GET   |                                                                                                                                                        |This endpoint is used to obtain the message protocols list and  edition names in JSON format.                                                                                                                                                              |

## Examples

Typical examples of usage Eiffel REMReM Generate Service endpoints are described below.

You can use command line tools like [curl](https://curl.haxx.se/) or some plugin for your favorite browser. For example:

*   [Postman](https://chrome.google.com/webstore/detail/postman/fhbjgbiflinjbdggehcddcbncdddomop) for Chromium-based browsers
*   [HttpRequester](https://addons.mozilla.org/en-US/firefox/addon/httprequester/) for Firefox

### Examples for `/mp` endpoint

#### Given one message in file _body-single.json_:

```
curl -XPOST -H "Content-Type: application/json" --data @body-single.json 'http://localhost:8080/generate-service/eiffelsemantics?msgType=EiffelActivityFinishedEvent&failIfMultipleFound=false&failIfNoneFound=false&lookupInExternalERs=false&lookupLimit=1&okToLeaveOutInvalidOptionalFields=false'
```

Result:

```
{"meta":{"id":"29f9b8b1-9d4b-41cf-9f42-4e6157850757","type":"EiffelActivityFinishedEvent","version":"3.0.0","time":1499076743638,"tags":["tag1","tag2"],"source":{"domainId":"example.domain","host":"host","name":"name","serializer":"pkg:maven/com.github.eiffel-community/eiffel-remrem-semantics@2.0.4","uri":http://java.sun.com/j2se/1.3/"}}"data":{"outcome":{"conclusion":"SUCCESSFUL","description":"The Result was Successful"},"persistentLogs":[],"customData":[{"key":"translatedSourceID","value":"29f9b8b1-9d4b-41cf-9f42-4e6157850757"}]},"links":[{"type":"ACTIVITY_EXECUTION","target":"69538470-4c3f-4c16-b84e-64ff0275f6fc"},{"type":"FLOW_CONTEXT","target":"ea82a50c-53bb-4d0c-8f03-b50fb83feb16"}]}
```

#### Example with event body passed to service:

```
curl -H "Content-Type: application/json" -X POST -d '{"msgParams": {"meta": {"type": "EiffelActivityFinishedEvent","tags": ["tag1", "tag2"],"source": {"domainId": "example.domain","host": "host","name": "name","uri": "http://java.sun.com/j2se/1.3/"}}},"eventParams": {"data": {"outcome": {"conclusion": "SUCCESSFUL","description": ""},"persistentLogs": [],"customData": []},"links": [{"type": "ACTIVITY_EXECUTION","target": "8b360a88-ae8e-4aec-811f-3017119a4acc"}]}}' 'http://localhost:8080/generate-service/eiffelsemantics?msgType=EiffelActivityFinishedEvent&failIfMultipleFound=false&failIfNoneFound=false&lookupInExternalERs=false&lookupLimit=1&okToLeaveOutInvalidOptionalFields=false'
```
Result:

```
{"meta":{"id":"abae00be-352b-40ed-9b51-4fb01c610a81","type":"EiffelActivityFinishedEvent","version":"3.0.0","time":1638505573517,"tags":["tag1","tag2"],"source":{"domainId":"example.domain","host":"host","name":"name","serializer":"pkg:maven/com.github.eiffel-community/eiffel-remrem-semantics@2.1.0","uri":"http://java.sun.com/j2se/1.3/"}},"data":{"outcome":{"conclusion":"SUCCESSFUL","description":""},"persistentLogs":[],"customData":[]},"links":[{"type":"ACTIVITY_EXECUTION","target":"8b360a88-ae8e-4aec-811f-3017119a4acc"}]}
```

### Examples for `/event_types/{mp}` endpoint

#### Retrieve the event types:

```
curl -X GET --header 'Accept: application/json' 'http://localhost:8080/generate-service/event_types/eiffelsemantics'
```

Result:

```
["EiffelArtifactPublishedEvent","EiffelActivityFinishedEvent","EiffelActivityCanceledEvent","EiffelArtifactCreatedEvent","EiffelActivityTriggeredEvent","EiffelConfidenceLevelModifiedEvent","EiffelActivityStartedEvent","EiffelAnnouncementPublishedEvent","EiffelCompositionDefinedEvent","EiffelTestCaseCanceledEvent","EiffelTestCaseTriggeredEvent","EiffelTestExecutionRecipeCollectionCreatedEvent","EiffelEnvironmentDefinedEvent","EiffelFlowContextDefinedEvent","EiffelSourceChangeCreatedEvent","EiffelSourceChangeSubmittedEvent","EiffelTestCaseFinishedEvent","EiffelTestCaseStartedEvent","EiffelTestSuiteFinishedEvent","EiffelTestSuiteStartedEvent","EiffelIssueVerifiedEvent","EiffelArtifactReusedEvent","EiffelServiceStoppedEvent","EiffelServiceStartedEvent","EiffelServiceReturnedEvent","EiffelServiceDiscontinuedEvent","EiffelServiceDeployedEvent","EiffelServiceAllocatedEvent","EiffelArtifactDeployedEvent","EiffelAlertAcknowledgedEvent","EiffelAlertCeasedEvent","EiffelAlertRaisedEvent","EiffelIssueDefinedEvent"]
```

### Examples for `/template/{type}/{mp}` endpoint

#### Retrieve the event template:

```
curl -X GET --header 'Accept: application/json' 'http://localhost:8080/generate-service/template/EiffelArtifactPublishedEvent/eiffelsemantics'
```

Result:

```
{"msgParams":{"meta":{"type":"EiffelArtifactPublishedEvent","version":"3.0.0","tags":[""],"source":{"domainId":"","host":"","name":"","uri":""}}},"eventParams":{"data":{"locations":[{"type":"required if locations present,can be anyone of ARTIFACTORY,NEXUS,PLAIN,OTHER","uri":"required"}],"customData":[{"key":"required if customData present","value":"required if customData present"}]},"links":[{"type":"ARTIFACT","target":"required - UUID of EiffelArtifactCreatedEvent"},{"type":"CAUSE or CONTEXT or FLOW_CONTEXT - optional","target":"required - UUID if type is present"}]}}
```

### Examples for `/versions` endpoint

```
curl -H "Content-Type: application/json" -X GET http://localhost:8080/generate/versions
```

Result:

```
{"serviceVersion":{"version":"x.x.x"},"endpointVersions":{"semanticsVersion":"x.x.x"}}
```

### Examples for `/message_protocols` endpoint

```
curl -H "Content-Type: application/json" -X GET http://localhost:8080/generate/message_protocols
```

Result:

```
{{"name":"eiffelsemantics", "edition":"xxxx"}, {"name":"eiffel3", "edition":"xxxx"}}
```

## Status Codes

The response generated will have internal status codes for each and every event based on the input JSON provided.

Status codes are generated according to the below table.

| Status code | Result                | Message                                                                                                                       | Comment                                                                                                                                                                         |
|-------------|-----------------------|-------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 200         | OK                    |                                                                                                                               | Event generated successfully                                                                                                                                                    |
| 207         | Multi Status          | Multi-Status                                                                                                                  | Is returned when there's a partial success, i.e. some events failed, some successfully generated, return status should be 207.                                                  |
| 400         | Bad Request           | Could not read document: Unrecognized token                                                                                   | Malformed JSON (missing braces or wrong event type, etc..), client need to fix problem in event before submitting again                                                         |
| 404         | Not Found             | Requested template is not available                                                                                           | The endpoint is not found, or template for specified event type is not found                                                                                                    |
| 406         | Not Acceptable        | No event id found with ERLookup properties, Lenient Validation disabled in configuration and user requested through REST call | Is returned if no event id fetched from configured event repository in REMReM generate, Lenient validation is not enabled in configuration, your not alloed to use this option. |
| 417         | Expectation Failed    | Multiple event ids found with ERLookup properties                                                                             | Is returned if multiple event ids fetched from configured event repository in REMReM generate.                                                                                  |
| 422         | Unprocessable Entity  | Link specific lookup options could not be fulfilled                                                                           | Is returned if Link specific lookup options could not be matched with failIfMultipleFound and failIfNoneFound.                                                                  |
| 500         | Internal Server Error | Internal server error                                                                                                         | When REMReM Generate is down, possible to try again later when server is up                                                                                                     |
| 503         | Service Unavailable   | "No protocol service has been found registered"                                                                               | When specified message protocol is not loaded                                                                                                                                   |


NOTE: In the array of event if any of the event have not valid JSON format,
then in response body we have only one response for that invalid JSON event, it is because the parser can't parse the invalid JSON format here.

### Examples:

There is some examples of passing an array of events and what their responses can be

#### Templates for successful array of events :

```
[{
  "msgParams": {
    "meta": {........
      "source": {......}
    }
  },
  "eventParams": {
    "data": {......},
    "links": [
      {
        "type": "ACTIVITY_EXECUTION",
        "target": "2d4849ec-53b9-4c3f-8b15-390d0ff33cfc"
      }
    ]}},
{
  "msgParams": {
    "meta": {........
      "source": {......}
    }
  },
  "eventParams": {
    "data": {......},
    "links": [
      {
        "type": "ACTIVITY_EXECUTION",
        "target": "2d4849ec-53b9-4c3f-8b15-390d0ff33cfc"
      }
    ]
  }}]

```

#### Response body:

```
[
  {
    "meta": {
      "id": "e878b4dc-7f4e-4bb3-9568-60fa23f014e2",
      "type": "EiffelActivityFinishedEvent",
      "version": "3.0.0",
      "time": 1723455277403,
      "tags": [""],
      "source": {
        "domainId": "",
        "host": "",
        "name": "",
        "serializer": "pkg:maven/com.github.eiffel-community/eiffel-remrem-semantics@2.2.7",
        "uri": ""
      }},
    "data": {
      "outcome": {
        "conclusion": "SUCCESSFUL",
        "description": ""},
      "persistentLogs": [],
      "customData": []
    },
    "links": [
      {
        "type": "ACTIVITY_EXECUTION",
        "target": "2d4849ec-53b9-4c3f-8b15-390d0ff33cfc"
      }
    ]},
  {
    "meta": {
      "id": "a0729ad7-d067-4227-b570-6c0fa6d58ace",
      "type": "EiffelActivityFinishedEvent",
      "version": "3.0.0",
      "time": 1723455277406,
      "tags": [""],
      "source": {
        "domainId": "",
        "host": "",
        "name": "",
        "serializer": "pkg:maven/com.github.eiffel-community/eiffel-remrem-semantics@2.2.7",
        "uri": ""
      }},
    "data": {
      "outcome": {
        "conclusion": "SUCCESSFUL",
        "description": ""},
      "persistentLogs": [],
      "customData": []
    },
    "links": [
      {
        "type": "ACTIVITY_EXECUTION",
        "target": "2d4849ec-53b9-4c3f-8b15-390d0ff33cfc"
      }
    ]}]
    
```

#### Response:

200

In the above array of event both the event template is correct, so that's why response code is appeared as  200

#### Template for partial successful or unsuccessful array of events :

```
[{
  "msgParams": {
    "meta": {........
      "source": {......}
    }
  },
  "eventParams": {
    "data": {......},
    "links": [
      {
        "type": "ACTIVITY_EXECUTION",
        "target": "2d4849ec-53b9-4c3f-8b15-390d0ff33cfc"
      }
    ]}},
{
  "msgParams": {
    "meta": {........
      "source": {......}
    }},
  "eventParams": {
    "data": {......},
    "links": [
      {
        "type": "INCORRECT_TYPE", //Mandatory link type ACTIVITY_EXECUTION is missing
        "target": "2d4849ec-53b9-4c3f-8b15-390d0ff33cfc"
      }
    ]
  }}]

```

#### Response body:

```
[
  {
    "meta": {
      "id": "feba19dc-c095-437e-98f1-a8a5ada7fa25",
      "type": "EiffelActivityFinishedEvent",
      "version": "3.0.0",
      "time": 1723452800241,
      "tags": [""],
      "source": {...}
    },
    "data": {
      "outcome": {....},
      "persistentLogs": [],
      "customData": []},
    "links": [
      {
        "type": "ACTIVITY_EXECUTION",
        "target": "2d4849ec-53b9-4c3f-8b15-390d0ff33cfc"
      }
    ]
  },
  {
    "status code": 400,
    "result": "FAIL",
    "message": "{\"message\":\"Cannot validate given JSON string\",\"cause\":\"com.ericsson.eiffel.remrem.semantics.validator.EiffelValidationException: Mandatory link type ACTIVITY_EXECUTION is missing\"}"
  }
]

```

#### Response:

207

In the above array of event, in the first template links type is missing and for second one links type is there, so first one is correct and second one is incorrect.
So that's why in response we have multi status and it appeared as 207.


#### Template for all unsuccessful array of events :

```
[{
  "msgParams": {
    "meta": {........
      "source": {......}
    }
  },
  "eventParams": {
    "data": {......},
    "links": [
      {
        "type": "INCORRECT_TYPE", //Mandatory link type ACTIVITY_EXECUTION is missing
        "target": "2d4849ec-53b9-4c3f-8b15-390d0ff33cfc"
      }
    ]}},
  {
  "msgParams": {
    "meta": {........
      "source": {......}
    }},
  "eventParams": {
    "data": {......},
    "links": [
      {
        "type": "INCORRECT_TYPE", //Mandatory link type ACTIVITY_EXECUTION is missing
        "target": "2d4849ec-53b9-4c3f-8b15-390d0ff33cfc"
      }
    ]
  }}]

```

#### Response body:

```
[
  {
    "status code": 400,
    "result": "FAIL",
    "message": "{\"message\":\"Cannot validate given JSON string\",\"cause\":\"com.ericsson.eiffel.remrem.semantics.validator.EiffelValidationException: Mandatory link type ACTIVITY_EXECUTION is missing\"}"
  },
  {
    "status code": 400,
    "result": "FAIL",
    "message": "{\"message\":\"Cannot validate given JSON string\",\"cause\":\"com.ericsson.eiffel.remrem.semantics.validator.EiffelValidationException: Mandatory link type ACTIVITY_EXECUTION is missing\"}"
  }
]

```
#### Response:

400

In the above array of event both the event template have not links type that's make him an incorrect event, So that's why the response is appeared as 400.

## Lookups

Presently, to generate Eiffel messages, Eiffel REMREM-Generate is being used. This process requires input mapping exactly one-to-one to Eiffel message Schema.

The "Link" field of Eiffel Message requires UUIDs of earlier sent events to link to. Passing these UUID's each time is a little bit difficult, some times user lookup the ER and manually place those IDs in REMReM Generate semi-structured event. In order to resolve this , we need to provide LOOKUP's for the Link's field ,which will have eventType and Properties. eventType will contain type of event.

Properties will contain any field of event mentioned in eventType or RepoUri or artifact identity (Purl identity including version) to Eiffel, which will make sure it is linked to the correct events. The lookups are used in filling the information for links, which will help users to know what event they want to query with the parameters so that they are automatically fetched and linked by Generate instead of the users adding them as a mandatory param when generating events.

_parameters_ specified should be enough to replace the listed event id as input will help to prepare the Query to ER and Lookup the ER data.

### Example:

As a User, I want to place the LINK type ARTIFACT to Event ID of EiffelArtifactCreatedEvent from Event Repository. The user passes the below template information to REMReM Generate, REMReM Generate will Query to configured Event Repository and place the event ID in LINK's and generate the Eiffel message.

#### Templates for LOOKUPS :

```
          "links":[
                  {
            "type": "ARTIFACT",
            "%lookup%": {
            "eventType": "EiffelArtifactCreatedEvent",
            "properties": [
                { "data.identity": "some purl identifier defining the artifact" }]
                   }
                }]
```

#### ER Query will look like:

```
https://localhost:8080/eventrepository/events/?meta.type=EiffelArtifactCreatedEvent&data.identity=some purl identifier defining the artifact
```

#### Examples for Lookups:

**Example 1:**

```
          "links":[
                  {
            "type": "ARTIFACT",
            "%lookup%": {
            "eventType": "EiffelArtifactCreatedEvent",
            "properties": [
                { "data.identity": "pkg:maven/test/testartifact@0.0.21" }]
                   }
                }]
```

**Example 2 :**

```
          "links":[
                  {
            "type": "ARTIFACT",
            "%lookup%": {
            "eventType": "EiffelArtifactCreatedEvent",
            "properties": [
                { "data.identity": "pkg:maven/test/batik-anim@1.9.1" }]
                   }
                }]
```

**Example 3 (For SourceChangeCreated/SourceChangeSubmitted Events Identifier will be repoUri):**

```
          "links":[
                  {
            "type": "CHANGE",
            "%lookup%": {
            "eventType": "EiffelSourceChangeCreatedEvent",
            "properties": [
                { "data.gitIdentifier.repoUri": "https://github.com/testRepo/myRepo.git" }]
                   }
                }]
```

#### Lookups are provided with four options:

| Options              | Default Value | Description                                                                                                                                                                                                                                                                                                                                                                                                                            |
|----------------------|---------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| failIfMultipleFound: | False         | If value is set to True and multiple event ids are found through any of the provided lookup definitions, then no event will be generated. |
| failIfNoneFound:     | False         | If value is set to True and no event id is found through (at least one of) the provided lookup definitions, then no event will be generated.|                                                                                                 
| lookupInExternalERs:             | False          | If value is set to True then REMReM will query external ERs and not just the locally used ER. The reason for the default value to be False is to decrease the load on external ERs. Here local ER means Single ER which is using REMReM generate.  External ER means multiple ER's which are configured in Local ER.|
| lookupLimit:            | 1             | The number of events returned, through any lookup definition given, is limited to this number. |

### Lookups with Options:

The options in lookups is useful to configure the parameters failIfNoneFound and failIfMultipleFound for each lookup.
If the failIfNoneFound and failIfMultipleFound are available in lookup then it will consider this values rather than the global values.

#### Examples for Lookups with Options:

**Example 1 (Single Lookup with Options):**

```
    "links": [{
        "type": "ARTIFACT",
        "%lookup%": {
            "eventType": "EiffelArtifactCreatedEvent",
            "properties": [{
                "data.identity": "pkg:maven/test/testartifact@0.0.21"
            }],
            "options": {
                "failIfNoneFound": "true",
                "failIfMultipleFound": "true"
            }
        }
    }]
```

**Example 2 (Multiple Lookups with Options):**

```
    "links": [{
            "type": "PREVIOUS_VERSION",
            "%lookup%": {
                "eventType": "EiffelArtifactCreatedEvent",
                "properties": [{
                    "data.identity": "pkg:maven/test/batik-anim@1.9.1"
                }],
                "options": {
                    "failIfNoneFound": "true",
                    "failIfMultipleFound": "false"
                }
            }
        },
        {
            "type": "CAUSE",
            "%lookup%": {
                "eventType": "EiffelSourceChangeSubmittedEvent",
                "properties": [{
                    "data.gitIdentifier.commitId": "ad090b60a4aedc5161da9c035a49b14a319829b4",
                    "data.gitIdentifier.repoUri": "https://github.com/johndoe/myPrivateRepo.git"
                }],
                "options": {
                    "failIfNoneFound": "true"
                }
            }
        }
    ]
```

**Example 3 :**

```
    "links": [{
            "type": "PREVIOUS_VERSION",
            "%lookup%": {
                "eventType": "EiffelArtifactCreatedEvent",
                "properties": [{
                    "data.identity": "pkg:maven/test/batik-anim@1.9.1"
                }],
                "options": {
                    "failIfMultipleFound": "true"
                }
            }
        },
        {
            "type": "CAUSE",
            "%lookup%": {
                "eventType": "EiffelConfidenceLevelModifiedEvent",
                "properties": [{
                    "data.value": "SUCCESS",
                    "data.name": "readyForDelivery"
                }],
                "options": {
                    "failIfNoneFound": "true"
                }
            }
        }
    ]
```
### Lenient Validation:
[Lenient Validation](../usage/lenientvalidation.md).

### Jasypt configurations

Jasypt Encryption support for property sources(passwords, secret info ..etc) is provided in the Application. To support this functionality in our application we need to add the following property in property file

```
jasypt.encryptor.jasyptKeyFilePath: The path of jasypt.key file containing the key value which was used while encrypting the original password
```

The key value in jasypt.key file must be same for both encryption and decryption of the original password(ldap,rabbitmq...etc)

### How to encrypt the password:

```
1) Download the jasypt jar file from any of the below locations
Link to [Jasypt](http://www.jasypt.org/download.html) (or) [Link to Maven](https://mvnrepository.com/artifact/org.jasypt/jasypt/1.9.2)

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

Use the above encrypted password in your property file like this **{ENC(encrypted password)}**

`Ex:activedirectory.managerPassword: {ENC(euJcvto7NtCDiWT7BKFW0A==)}`
