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