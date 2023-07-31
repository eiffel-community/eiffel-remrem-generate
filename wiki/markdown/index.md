## Introduction

REMReM (**REST Mailbox for Registered Messages**) is a set of tools that can be used to generate validated Eiffel messages and publish them on a RabbitMQ message bus. They can be run as micro services or as stand-alone CLI versions. For more details on the micro services and the REMReM design, see [Eiffel REMReM](https://github.com/eiffel-community/eiffel-remrem)

Eiffel REMReM Generate takes a partial message in JSON format, validates it and adds mandatory fields before outputting a complete, valid Eiffel message.


## Pre-requisites

*   JDK 8
*   Tomcat 8

For supporting latest features, Eiffel REMReM Generate should use the latest version of [Eiffel REMReM Semantics](https://github.com/eiffel-community/eiffel-remrem-semantics).

## Components

*   REMReM Generate CLI (Command Line Interface)
*   REMReM Generate Service

## Compatibility
Both `generate` and [`publish`](https://github.com/eiffel-community/eiffel-remrem-publish) services use [`semantics`](https://github.com/eiffel-community/eiffel-remrem-semantics) library. Their mutual compatibility is available in [Compatibility](https://github.com/eiffel-community/eiffel-remrem-publish/blob/master/wiki/markdown/index.md#components) section of publish service.