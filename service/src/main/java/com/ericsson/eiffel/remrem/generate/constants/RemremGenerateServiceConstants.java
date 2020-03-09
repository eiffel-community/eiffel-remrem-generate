/*
    Copyright 2018 Ericsson AB.
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

public final class RemremGenerateServiceConstants {

        public static final String NO_SERVICE_ERROR = "{\"status_code\": 503, \"result\": \"FAIL\", "
                + "\"message\":\"No protocol service has been found registered\"}";

        public static final String NO_TEMPLATE_ERROR = "{\"status_code\": 404, \"result\": \"FAIL\", "
                + "\"message\":\"Requested template is not available\"}";

        public static final String INTERNAL_SERVER_ERROR = "{\"status_code\": 500, \"result\": \"FAIL\", "
                + "\"message\":\"Internal server error\"}";

        public static final String JSON_ERROR_MESSAGE_FIELD = "message";

        public static final String DOCUMENTATION_URL = "https://eiffel-community.github.io/eiffel-remrem-generate/index.html";

        public static final String UNAVAILABLE_FOR_FAILIFMULTIPLEFOUND = "{\"status_code\": 417, \"result\": \"FAIL\", "
                + "\"message\":\"Muliple event ids found with ERLookup properties\"}";

        public static final String UNAVAILABLE_FOR_FAILIFNONEFOUND = "{\"status_code\": 406, \"result\": \"FAIL\", "
                + "\"message\":\"No event id found with ERLookup properties\"}";

        public static final String LIMIT = "The number of events to be displayed.";

        public static final String CONNECT_TO_EXTERNAL_ERS = "Determines if external ER's should be used to compile the results of query."
                + "Use false to use External ER's.";

}
