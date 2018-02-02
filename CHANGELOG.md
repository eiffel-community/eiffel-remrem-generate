
## 0.9.4
- Moved ldap related functionality from shared
- Added functionality to disable the Remrem Generate Authentication.

## 0.9.3
- Added endpoint to get event templates
- Uplifted remrem-semantics to 0.3.5
- Uplifted remrem-protocol-interface to 0.0.7

## 0.9.2
- Uplifted remrem-semantics version to 0.3.2 to support continuous operation events

## 0.9.1
- Uplifted generate version to resolve versions endpoint issue

## 0.9.0
- Uplifted remrem-semantics to 0.3.1 to override given input meta.source.serializer GAV information

## 0.8.9
- Uplifted remrem-protocol version to 0.0.5 and remrem-semantics version to 0.3.0

## 0.8.8
- Uplifted remrem-semantics version to 0.2.9 to accept case insensitive eventType through query params.

## 0.8.7
- Added validation that can detect duplicate keys in the JSON request body

## 0.8.6
- Uplift remrem semantics to 0.2.8 and eiffelinputEventType checking.

## 0.8.5
- Uplift remrem semantics to 0.2.7 and eiffelevents as camelcase.

## 0.8.4
- Removed spring configurations from config.properties file and handled through code.

## 0.8.3
- Changed REMReM generate to read required properties from tomcat/conf/config.properties for service.

## 0.8.2
- Uplifted remrem-semantics version to 0.2.6 to support links validation.

## 0.8.1
- Added copyright headers to the source code.
- Uplifted remrem-shared version to 0.3.3 to get the versions of generate and all loaded protocols.

## 0.8.0
- Updated remrem-shared version to 0.3.2 to support base64 encryption functionality for Ldap manager password.

## 0.7.9
- Moved ldap related functionality to shared

## 0.7.8
- Added HttpStatus codes for generated ouput

## 0.7.7
- Changed the project structure to build seperate
  binaries for CLI and Service.

## 0.7.6
- Updated the documentation for generate.

## 0.7.5
- upgraded semantics version in build.gradle to support all
  EiffelEvents in the eiffel repo from topic-drop4 branch 
- Changed json inputs for test cases

## 0.7.4
- Fixed domain property injection in SecurityConfig

## 0.7.3
- Added single protocol end-point in remrem generate.

## 0.7.2
- Explicitly stated some dependencies.

## 0.7.1
- Fix authentication required even when activedirectory.enabled=false

## 0.7.0
- Added functionality to list endpoint versions

## 0.6.3
- Removed unused dependency.

## 0.6.2
- added optional authentication to an Active Directory server for all 
  REST endpoints

## 0.6.1
- Removed printstack traces to stdout.
  

## 0.6.0
- added functionality to load dynamic protocol jars in both cli as well
  as service


## 0.5.9
- update gradle.build to resolve transient dependecies conflicts during
  integration tests
- refactoring to fix error when help flag passed
- update gradle and version of eiffel semantics library
- added unit and integration tests for cli