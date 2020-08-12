## Lenient Validation
The Lenient validation introduced in REMReM Generate since the version 2.1.0

Using the lenient validation user can continue the Eiffel message generate with non-fatal error fields in the message.

This validation flexibility applies to only Eiffel's message optional fields. see
 [Eiffel REMReM semantics](https://github.com/eiffel-community/eiffel/tree/master/schemas)
for more about mandatory and optional fields.

The Lenient validation will perform the only on mandatory field validation and other validation failures will place in Eiffel message as a new customData property(remremGenerateFailures).

#### Other Validations are:
- pattern validations
- enum
- format
- type

The lenientValidationEnabledToUsers is an optional parameter to CLI and service and possible inputs are true and false. The default lenientValidationEnabledToUsers is false.

The REMReM Generate CLI the lenientValidationEnabled option user can pass through command line option -lv (true/false)

The REMreM Generate service the lenient validation is enable with two options one is lenientValidationEnabledToUsers is set through configuration file 
and second one is REST endpoint(/generate) option okToLeaveOutInvalidOptionalFields

The Configuration parameter for the whole REMReM Generate instance lenientValidationEnabledToUsers (default false)
REST parameter for each /generate call okToLeaveOutInvalidOptionalFields (default false)

If the configuration parameter is set to false and the REST parameter is set to true REMReM /generate REST call return with an error code stating that it is not allowed to ask for lenient validation.

If both parameters are true then REMReM will remove the optional event fields from the input event data that does not validate successfully, and add those removed field information added into customData.

#### Example 1: Lenient Validation Enabled and Input invalid optional field (links.type.COMPOSITION)
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
{
   "meta":{
      "id":"ea2b6ef3-c03a-432c-9c9e-0eec5730fddb",
      "type":"EiffelArtifactCreatedEvent",
      "version":"3.0.0",
      "time":1596708141578,
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
            "key":"remremGenerateFailures",
            "value":[
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
```

#### Example 2: Lenient Validation Disabled and Input invalid optional field (links.type.COMPOSITION)
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
