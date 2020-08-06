## Lenient Validation
The Lenient validation introduced in REMReM Generate since the version 2.1.0

Using the lenient validation user can continue the Eiffel message generate with non-fatal error fields in the message.

This validation flexibility applies to only Eiffel's message optional fields. see
 [Eiffel REMReM semantics](https://github.com/eiffel-community/eiffel/tree/master/schemas)
for more about mandatory and optional fields.

The Lenient validation will perform the only mandatory and type field validation and other validation failures will place in Eiffel message as a new property(remremGenerateFailures)

#### Other Validations are:
- pattern validations
- enum
- format

The ignoreOptionalFieldValidationErrors is an optional parameter to CLI and service and possible inputs are true and false. The default ignoreOptionalFieldValidationErrors is false.

The REMReM Generate CLI the ignoreOptionalFieldValidationErrors option user can pass through command line option -iov (true/false)

The REMreM Generate service ignoreOptionalFieldValidationErrors is set through configuration file.

#### Example 1: ignoreOptionalFieldValidationErrors = true and Input invalid optional field (links.type.COMPOSITION)
```
{
   "msgParams":{
      "meta":{
         "type":"EiffelArtifactCreatedEvent",
         "version":"3.0.0",
         "tags":[
            123,
            "tag2"
         ],
         "source":{
            "domainId":"domainID",
            "host":"host",
            "name":"name",
            "uri":"http://java.sun.com/j2se/1.3/",
            "serializer":"pkg:maven"
         },
         "security":{
            "authorIdentity":"test",
            "encryptedDigest":"sample"
         }
      }
   },
   "eventParams":{
      "data":{
         "gav":{
            "groupId":"G",
            "artifactId":"A",
            "version":"V"
         },
         "fileInformation":[
            {
               "name":"name"
            }
         ],
         "buildCommand":"trigger",
         "requiresImplementation":"NONE",
         "name":"event",
         "dependsOn":[

         ],
         "implement":[

         ],
         "identity":"pkg:abc",
         "customData":[
            {
               "key":"firstLog",
               "value":"http://myHost.com/firstLog"
            },
            {
               "key":"otherLog",
               "value":"http://myHost.com/firstLog33"
            }
         ]
      },
      "links":[
         {
            "type":"CAUSE",
            "target":"aaaaaaaa-bbbb-5ccc-8ddd-eeeeeeeeeee1"
         },
         {
            "type":"PREVIOUS_VERSION",
            "target":"aaaaaaaa-bbbb-5ccc-8ddd-eeeeeeeeeee2"
         },
         {
            "type":"COMPOSITION",
            "target":"aaaaaaaa"
         },
         {
            "type":"ENVIRONMENT",
            "target":"aaaaaaaa-bbbb-5ccc-8ddd-eeeeeeeeeee3"
         }
      ]
   }
}
```

#### Output

```
[
   {
      "meta":{
         "id":"8afff3b8-96b3-405c-85a5-366bc5f59cc6",
         "type":"EiffelArtifactCreatedEvent",
         "version":"3.0.0",
         "time":1595395508961,
         "tags":[
            "123",
            "tag2"
         ],
         "source":{
            "domainId":"domainID",
            "host":"host",
            "name":"name",
            "serializer":"pkg:maven",
            "uri":"http://java.sun.com/j2se/1.3/"
         },
         "security":{
            "authorIdentity":"test",
            "sequenceProtection":[

            ]
         }
      },
      "data":{
         "identity":"pkg:abc",
         "fileInformation":[
            {
               "name":"name",
               "tags":[

               ]
            }
         ],
         "buildCommand":"trigger",
         "requiresImplementation":"NONE",
         "dependsOn":[

         ],
         "implements":[

         ],
         "name":"event",
         "customData":[
            {
               "key":"firstLog",
               "value":"http://myHost.com/firstLog"
            },
            {
               "key":"otherLog",
               "value":"http://myHost.com/firstLog33"
            },
            {
               "remremGenerateFailures":[
                  {
                     "type":"pattern",
                     "message":"ECMA 262 regex \"^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$\" does not match input string \"aaaaaaaa\"",
                     "path":"/links/2/target"
                  }
               ]
            }
         ]
      },
      "links":[
         {
            "type":"CAUSE",
            "target":"aaaaaaaa-bbbb-5ccc-8ddd-eeeeeeeeeee1"
         },
         {
            "type":"PREVIOUS_VERSION",
            "target":"aaaaaaaa-bbbb-5ccc-8ddd-eeeeeeeeeee2"
         },
         {
            "type":"ENVIRONMENT",
            "target":"aaaaaaaa-bbbb-5ccc-8ddd-eeeeeeeeeee3"
         }
      ]
   }
]
```

The ignoreOptionalFieldValidationErrors = true created new message property with "remremGenerateFailures"
```
"remremGenerateFailures":[
                  {
                     "type":"pattern",
                     "message":"ECMA 262 regex \"^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$\" does not match input string \"aaaaaaaa\"",
                     "path":"/links/2/target"
                  }
               ]
```

#### Example 2: ignoreOptionalFieldValidationErrors = true and Input invalid optional field (links.type.COMPOSITION)
Input same as example1

##### output
```
[
   {
      "message":"Cannot validate given JSON string",
      "cause":"com.ericsson.eiffel.remrem.semantics.validator.EiffelValidationException: [ECMA 262 regex \"^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$\" does not match input string \"aaaaaaaa\"]"
   }
]
```
