/*
    Copyright 2026 Ericsson AB.
    For a full list of individual contributors, please see the commit history.
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/
package com.ericsson.eiffel.remrem.generate.constants;

public final class RemremGenerateResponseExamples {

    public static final String MP_RESPONSE_200_SINGLE_EVENT_EXAMPLE = """
        {
            "meta": {
                "type": "EiffelActivityFinishedEvent",
                "id": "aaaaaaaa-bbbb-5ccc-8ddd-eeeeeeeeeee0",
                "version":"",
                "time": 1495061797000,
                "tags": [],
                    "source": {
                      "serializer": "pkg:maven/com.github.eiffel-community/eiffel-remrem-semantics@2.4.4"
                    }
            },
            "data": {"outcome": {"conclusion": "SUCCESSFUL"}, "persistentLogs": [], "customData": []},
            "links": [{"type":"ACTIVITY_EXECUTION", "target": "aaaaaaaa-bbbb-5ccc-8ddd-eeeeeeeeeee1"}]
        }
    """;

    public static final String MP_RESPONSE_200_MULTIPLE_EVENTS_EXAMPLE = """
        [{
            "meta": {
                "type": "EiffelActivityFinishedEvent",
                "id": "aaaaaaaa-bbbb-5ccc-8ddd-eeeeeeeeeee0",
                "version":"3.3.0",
                "time": 1495061797000,
                "tags": [],
                    "source": {
                      "serializer": "pkg:maven/com.github.eiffel-community/eiffel-remrem-semantics@2.4.4"
                    }
            },
            "data": {"outcome": {"conclusion": "SUCCESSFUL"}, "persistentLogs": [], "customData": []},
            "links": [{"type":"ACTIVITY_EXECUTION", "target": "aaaaaaaa-bbbb-5ccc-8ddd-eeeeeeeeeee1"}]
        },
        {
               "meta": {
                "type": "EiffelActivityFinishedEvent",
                "id": "aaaaaaaa-bbbb-5ccc-8ddd-eeeeeeeeeee2",
                "version":"4.0.0",
                "time": 1495061797000,
                "tags": [],
                    "source": {
                      "serializer": "pkg:maven/com.github.eiffel-community/eiffel-remrem-semantics@2.4.4"
                    }
            },
            "data": {"outcome": {"conclusion": "TIMED_OUT"}, "persistentLogs": [], "customData": []},
            "links": []
        }]
    """;

    public static final String MP_RESPONSE_207_EXAMPLE = """
        [
          {
            "meta": {
              "id": "127c69cb-7161-4f90-8044-92adf7989b15",
              "type": "EiffelActivityFinishedEvent",
              "version": "3.3.0",
              "time": 1774338693440,
              "tags": [],
              "source": {
                "serializer": "pkg:maven/com.github.eiffel-community/eiffel-remrem-semantics@2.4.4"
              }
            },
            "data": {
              "outcome": {
                "conclusion": "SUCCESSFUL"
              },
              "persistentLogs": [],
              "customData": []
            },
            "links": [
              {
                "type": "ACTIVITY_EXECUTION",
                "target": "aaaaaaaa-bbbb-5ccc-8ddd-eeeeeeeeeee1"
              }
            ]
          },
          {
            "status code": 400,
            "result": "FAIL",
            "message": {
                "message": "Missing fields found in body JSON",
                "cause": "eventParams or msgParams or msgParams.meta are missed"
            }
          }
        ]
    """;

    public static final String MP_RESPONSE_400_SINGLE_EVENT_EXAMPLE = """
        {
            "status code": 400,
             "message": {
                 "message": "Missing fields found in body JSON",
                 "cause": "eventParams or msgParams or msgParams.meta are missed"
             },
             "result": "FAIL"
        }
    """;

    public static final String MP_RESPONSE_400_MULTIPLE_EVENTS_EXAMPLE = """
        [
            {
                "status code": 400,
                "message": {
                 "message": "Missing fields found in body JSON",
                 "cause": "eventParams or msgParams or msgParams.meta are missed"
                },
                "result": "FAIL"
            },
            {
                "status code": 400,
                "message": {
                 "message": "Missing fields found in body JSON",
                 "cause": "eventParams or msgParams or msgParams.meta are missed"
                },
                "result": "FAIL"
            }
        ]
    """;

    public static final String MP_RESPONSE_500_EXAMPLE = """
        {
            "status code": 500,
            "message": "Error while processing the request",
            "result": "FAIL"
        }
    """;

    public static final String MP_RESPONSE_503_EXAMPLE = """
        {
            "status code": 503,
             "message": "Handler of Eiffel protocol 'other' not found",
             "result": "FAIL"
        }
    """;

    public static final String MP_REQUEST_INPUT_EXAMPLE = """
        {
            "msgParams": {"meta": {"type": "EiffelActivityFinishedEvent"}},
            "eventParams": {
                "data": {"outcome": {"conclusion": "SUCCESSFUL"}},
                "links": [{"type":"ACTIVITY_EXECUTION", "target": "aaaaaaaa-bbbb-5ccc-8ddd-eeeeeeeeeee1"}]
            }
        }
    """;

    // Response examples for /versions API endpoint

    public static final String VERSIONS_RESPONSE_200_EXAMPLE = """
        {
          "serviceVersion": {
            "serviceVersion": "0.0.1"
          },
          "endpointVersions": {
            "semanticsVersion": "0.0.1"
          }
        }
    """;

    // Response examples for /template/{type/{mp} API endpoint

    public static final String TEMPLATE_RESPONSE_200_EXAMPLE = """
        {
          "msgParams": {
            "meta": {
              "type": "EiffelAnnouncementPublishedEvent",
              "version": "3.0.0",
              "tags": [
                ""
              ],
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
              "heading": "required",
              "body": "required",
              "uri": "",
              "severity": "required, can be anyone of MINOR,MAJOR,CRITICAL,BLOCKER,CLOSED,CANCELED ",
              "customData": [
                {
                  "key": "required if customData present",
                  "value": "required if customData present"
                }
              ]
            },
            "links": [
              {
                "type": "CAUSE or CONTEXT or FLOW_CONTEXT or MODIFIED_ANNOUNCEMENT - optional",
                "target": "required - UUID if type is present"
              }
            ]
          }
        }
    """;

    public static final String TEMPLATE_RESPONSE_404_EXAMPLE = """
        {
          "status_code": 404,
          "result": "FAIL",
          "message": "Requested template is not available"
        }
    """;

    public static final String TEMPLATE_RESPONSE_500_EXAMPLE = """
        {
          "status_code": 500,
          "result": "FAIL",
          "message": "Internal server error"
        }
    """;

    public static final String TEMPLATE_RESPONSE_503_EXAMPLE = """
        {
          "status_code": 503,
          "result": "FAIL",
          "message": "No protocol has been found registered"
        }
    """;

    // Response examples for /message_protocols API endpoint

    public static final String MESSAGE_PROTOCOLS_RESPONSE_200_EXAMPLE = """
        [
          {
            "name": "eiffelsemantics",
            "edition": "Paris"
          }
        ]
    """;

    // Response examples for /event_types/{mp} API endpoint

    public static final String EVENT_TYPES_RESPONSE_200_EXAMPLE = """
        [
          "EiffelArtifactPublishedEvent",
          "EiffelActivityFinishedEvent",
          "EiffelActivityCanceledEvent",
          "EiffelArtifactCreatedEvent",
          "EiffelActivityTriggeredEvent",
          "EiffelConfidenceLevelModifiedEvent",
          "EiffelActivityStartedEvent"
        ]
    """;

    public static final String EVENT_TYPES_RESPONSE_500_EXAMPLE = """
        {
          "status_code": 500,
          "result": "FAIL",
          "message": "Internal Server Error"
        }
    """;

    public static final String EVENT_TYPES_RESPONSE_503_EXAMPLE = """
        {
          "status_code": 503,
          "result": "FAIL",
          "message": "No protocol service has been found registered"
        }
    """;

    private RemremGenerateResponseExamples() {}
}
