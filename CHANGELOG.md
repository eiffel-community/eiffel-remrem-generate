## 2.1.5
- Made changes to fix the test-failures
- Updated eiffel-remrem-parent version from 2.0.6 to 2.0.7
- updated eiffel-remrem-semantics version from 2.2.1 to 2.2.2
- Introduced new (/message_protocols) Endpoint which returns the available message protocols list and their respective edition names. 
- Updated all curl commands in documentation
- Removed archived repo remrem-shared dependencies
- Uplifted eiffel-remrem-parent version from 2.0.7 to 2.0.8
- Uplifted eiffel-remrem-semantics version from 2.2.2 to 2.2.3

## 2.1.4
- Fixed issue related to ER lookup strategy in REMReM-generate.

## 2.1.3
- Updated the eiffel-remrem-parent version from 2.0.5 to 2.0.6
- Updated the eiffel-remrem-semantics version from 2.1.0 to 2.2.1
- Excluded the log4j-to-slf4j dependency to resolve vulnerability issues.
- Uplifted eiffel-remrem-parent version from 2.0.4 to 2.0.5
- Uplifted eiffel-remrem-shared version from 2.0.4 to 2.0.5
- Removed ER retry mechanism

## 2.1.2
- Implemented the functionality to read the Jasypt encryption key from jasypt.key file

## 2.1.1
- Fixed the test case for REMReM Generate to support lenient validation for Eiffel Protocol.

## 2.1.0
- Updated REMReM Generate to support lenient validation for Eiffel Protocol.

## 2.0.18
- ER lookup control parameter lookupLimit has been set to 1 or higher.

## 2.0.17
- Fixed failed testcase in EiffelRemERLookupControllerUnitTest class.

## 2.0.16
- REMReM lookups controlled 'failIfNoneFound' and 'failIfMultipleFound' lookups per lookup
  object within an event instead of globally per call.

## 2.0.15
- Fixed the broken link which points to generate documentation in swagger.

## 2.0.14
- Uplifted eiffel-remrem-semantics version from 2.0.12 to 2.0.13.

## 2.0.13
- Added the lookupInExternalERs and lookupLimit parameters to ER lookup.

## 2.0.12
- Uplifted eiffel-remrem-parent version from 2.0.2 to 2.0.4.
- Uplifted eiffel-remrem-shared version from 2.0.2 to 2.0.4.
- Uplifted eiffel-remrem-semantics version from 2.0.9 to 2.0.12.

## 2.0.11
- Added REMReM Generate documentation in master branch
- Uplifted eiffel-remrem-semantics version from 2.0.8 to 2.0.9.

## 2.0.10
- Uplifted eiffel-remrem-semantics version from 2.0.6 to 2.0.8.

## 2.0.9
- Fixed for ER Lookup functionality do not work due to hardcoded context path.

## 2.0.8
- REMReM generate service is being started when ER not configured.

## 2.0.7
- Update pom.xml

## 2.0.6
- Added ER Lookup configurations for a link and code to handle the lookup towards ER when generating events.
- Uplifted eiffel-remrem-generate version from 2.0.5 to 2.0.6

## 2.0.5
- Uplifted eiffel-remrem-parent version from 2.0.1 to 2.0.2.
- Uplifted eiffel-remrem-shared version from 2.0.1 to 2.0.2.
- Uplifted eiffel-remrem-semantics version from 2.0.5 to 2.0.6.

## 2.0.4
- Uplifted eiffel-remrem-parent version from 2.0.0 to 2.0.1.
- Uplifted eiffel-remrem-shared version from 2.0.0 to 2.0.1.
- Uplifted eiffel-remrem-semantics version from 2.0.4 to 2.0.5.

## 2.0.3
- Upgraded eiffel-remrem-semantics version from 2.0.3 to 2.0.4

## 2.0.2
- Upgraded eiffel-remrem-semantics version from 2.0.2 to 2.0.3.

## 2.0.1
- Upgraded eiffel-remrem-semantics version from 2.0.1 to 2.0.2.

## 2.0.0
- Upgraded eiffel-remrem-semantics version from 1.0.1 to 2.0.1.
- Parent group changed from Ericsson to eiffel-community.

## 1.0.1
- Changed Link from http://ericsson.github.io to https://eiffel-community.github.io.
- Upgraded eiffel-remrem-semantics version from 1.0.0 to 1.0.1.

## 1.0.0
- Upgraded eiffel-remrem-parent version from 0.0.8 to 1.0.0.
- Upgraded eiffel-remrem-shared version from 0.4.2 to 1.0.0.
- Upgraded eiffel-remrem-semantics version from 0.5.3 to 1.0.0.

## 0.10.9
- Upgraded eiffel-remrem-parent version from 0.0.7 to 0.0.8.
- Upgraded eiffel-remrem-shared version from 0.4.1 to 0.4.2.
- Upgraded eiffel-remrem-semantics version from 0.5.2 to 0.5.3.

## 0.10.8
- Adaptation of Spring application to execute without external Tomcat installation.

## 0.10.7
- Upgraded eiffel-remrem-parent version from 0.0.6 to 0.0.7.
- Upgraded eiffel-remrem-shared version from 0.4.0 to 0.4.1.
- Upgraded eiffel-remrem-semantics version from 0.5.1 to 0.5.2.

## 0.10.6
- Updated versions of parent from 0.0.5 to 0.0.6 and shared from 0.3.9 to 0.4.0
- Uplifted semantics version from 0.5.0 to 0.5.1

## 0.10.5
- Updated parent, shared and semantics version

## 0.10.4
- Updated remrem shared and semantics version

## 0.10.3
- Updated parent version

## 0.10.2
- Migrated from gradle to maven

## 0.10.1
- Updated versions of few dependencies.

## 0.10.0
- Removed based64 encryption mechanism for ldap manager password

## 0.9.9
- - Added jasypt-spring-boot-starter dependency to support open text encryption

## 0.9.8
- Removed Protocol Interface dependency
- Uplifted semantics version to 0.4.1

## 0.9.7
- Added more information in error messages for generate service
- Uplifted semantics version to 0.4.0

## 0.9.6
- Added swagger for generate service
- Added more verifications for generate service
- Added presentation in html for all GET endpoints
- Changed year in copyright headers from 2017 to 2018

## 0.9.5
- CLI changed to spring boot application and added logback support to remrem generate

## 0.9.4
- Moved ldap related functionality from shared
- Added functionality to disable the Remrem Generate Authentication.
- Uplifted remrem-semantics version to 0.3.7

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
