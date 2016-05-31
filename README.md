# eiffel-remrem-generate
[![Build Status](https://travis-ci.org/Ericsson/eiffel-remrem-generate.svg?branch=master)](https://travis-ci.org/Ericsson/eiffel-remrem-generate)
[![Coverage Status](https://coveralls.io/repos/github/Ericsson/eiffel-remrem-generate/badge.svg?branch=master)](https://coveralls.io/github/Ericsson/eiffel-remrem-generate?branch=master)
[![](https://jitpack.io/v/Ericsson/eiffel-remrem-generate.svg)](https://jitpack.io/#Ericsson/eiffel-remrem-generate)

## Compatibility
- JDK8
- Gradle 2.13

## Testing
Test integration is accomplished via Travis-CI and coverage is via Coveralls, both of which
can be accessed via above badges. 

### Test Instructions
./gradlew integrationTest --info

## Installation, Features and Release
### Build Instructions
./gradlew assemble

### Embeded Semantics 
This microservice includes support for embedded message semantics for the new version of 
Eiffel messaging

### Release
It is released via Jitpack repo with the latest version on the badge above.

IMPORTANT NOTICE: The contents of this repository currectly reflect a DRAFT. The Eiffel framework has been used in production within Ericsson for several years to great effect; what is presented here is a revision and evolution of that framework - an evolution that is currently ongoing. In other words, anything in this repository should be regarded as tentative and subject to change. It is published here to allow early access and trial and to solicit early feedback.
