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
package com.ericsson.eiffel.remrem.generate.controller;

import com.ericsson.eiffel.remrem.generate.config.ErLookUpConfig;
import com.ericsson.eiffel.remrem.generate.constants.RemremGenerateServiceConstants;
import com.ericsson.eiffel.remrem.generate.exception.REMGenerateException;
import com.ericsson.eiffel.remrem.protocol.MsgService;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;

import ch.qos.logback.classic.Logger;
import io.swagger.annotations.*;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import springfox.documentation.annotations.ApiIgnore;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static com.ericsson.eiffel.remrem.generate.constants.RemremGenerateServiceConstants.*;

@RestController
@RequestMapping("/*")
@Api(value = "REMReM Generate Service", description = "REST API for generating Eiffel messages")
public class RemremGenerateController {

    static Logger log = (Logger) LoggerFactory.getLogger(RemremGenerateController.class);

    // regular expression that exclude "swagger-ui.html" from request parameter
    private static final String REGEX = ":^(?!swagger-ui.html).*$";

    @Autowired
    private List<MsgService> msgServices;

    private JsonParser parser = new JsonParser();

    @Autowired
    private ErLookUpConfig erlookupConfig;

    private static ResponseEntity<String> response;
    
    @Value("${lenientValidationEnabledToUsers:false}")
    private boolean lenientValidationEnabledToUsers;

    public void setLenientValidationEnabledToUsers(boolean lenientValidationEnabledToUsers) {
        this.lenientValidationEnabledToUsers = lenientValidationEnabledToUsers;
    }

    private static RestTemplate restTemplate = new RestTemplate();

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Returns event information as json element based on the message protocol,
     * taking message type and json body of string type as input because just to parse
     * the string in to JsonElement not using JsonElement directly here.
     *
     * <p>
     * Parameters: msgProtocol - The message protocol, which tells us which
     * service to invoke. msgType - The type of message that needs to be
     * generated. body - The content of the message which is used in
     * creating the event details.
     * <p>
     * Returns: The event information as a json element
     */

    @ApiOperation(value = "To generate eiffel event based on the message protocol", response = String.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Event sent successfully"),
            @ApiResponse(code = 400, message = "Malformed JSON"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 503, message = "Message protocol is invalid")})
    @RequestMapping(value = "/{mp" + REGEX + "}", method = RequestMethod.POST)
    public ResponseEntity<?> generate(@ApiParam(value = "message protocol", required = true) @PathVariable("mp") final String msgProtocol,
                                      @ApiParam(value = "message type", required = true) @RequestParam("msgType") final String msgType,
                                      @ApiParam(value = "ER lookup result multiple found, Generate will fail") @RequestParam(value = "failIfMultipleFound", required = false, defaultValue = "false") final Boolean failIfMultipleFound,
                                      @ApiParam(value = "ER lookup result none found, Generate will fail") @RequestParam(value = "failIfNoneFound", required = false, defaultValue = "false") final Boolean failIfNoneFound,
                                      @ApiParam(value = RemremGenerateServiceConstants.LOOKUP_IN_EXTERNAL_ERS) @RequestParam(value = "lookupInExternalERs", required = false, defaultValue = "false") final Boolean lookupInExternalERs,
                                      @ApiParam(value = RemremGenerateServiceConstants.LOOKUP_LIMIT) @RequestParam(value = "lookupLimit", required = false, defaultValue = "1") final int lookupLimit,
                                      @ApiParam(value = RemremGenerateServiceConstants.LenientValidation) @RequestParam(value = "okToLeaveOutInvalidOptionalFields", required = false, defaultValue = "false") final Boolean okToLeaveOutInvalidOptionalFields,
                                      @ApiParam(value = "JSON message", required = true) @RequestBody String body) {
        try {
            JsonFactory jsonFactory = JsonFactory.builder().build().enable(com.fasterxml.jackson.core.JsonParser
                    .Feature.STRICT_DUPLICATE_DETECTION);
            ObjectMapper mapper = new ObjectMapper(jsonFactory);
            JsonNode node = mapper.readTree(body);
            Gson gson = new Gson();
            JsonElement inputJson = gson.fromJson(node.toString(), JsonElement.class);
            return generate(msgProtocol, msgType, failIfMultipleFound, failIfNoneFound, lookupInExternalERs,
                    lookupLimit, okToLeaveOutInvalidOptionalFields, inputJson);
        } catch (JsonSyntaxException | JsonProcessingException e) {
            String exceptionMessage = e.getMessage();
            log.error("Invalid JSON parse data format due to", e.getMessage());
            return createResponseEntity(HttpStatus.BAD_REQUEST, "Invalid JSON parse data format due to: "
                    + exceptionMessage, JSON_FATAL_STATUS);
        }
    }

    /**
     * Returns event information as json element based on the message protocol,
     * taking message type and json body as input
     * Here we basically add this to handle if inputData is of jsonArray type as well
     * <p>
     * Parameters: msgProtocol - The message protocol, which tells us which
     * service to invoke. msgType - The type of message that needs to be
     * generated. inputData - The content of the message which is used in
     * creating the event details.
     * <p>
     * Returns: The event information as a json element
     */
    public ResponseEntity<?> generate(final String msgProtocol, final String msgType, final Boolean failIfMultipleFound,
                                      final Boolean failIfNoneFound, final Boolean lookupInExternalERs, final int lookupLimit,
                                      final Boolean okToLeaveOutInvalidOptionalFields, JsonElement inputData) {

        JsonArray generatedEventResults = new JsonArray();
        try {
            if (lookupLimit <= 0) {
                return new ResponseEntity<>("Parameter 'lookupLimit' must be > 0", HttpStatus.BAD_REQUEST);
            }
            if (inputData == null) {
                return createResponseEntity(HttpStatus.BAD_REQUEST, "Parameter 'inputData' must not be null",
                        JSON_ERROR_STATUS);
            }

            if (inputData.isJsonArray()) {
                JsonArray inputEventJsonArray = inputData.getAsJsonArray();
                for (JsonElement element : inputEventJsonArray) {
                    JsonObject generatedEvent = (processEvent(msgProtocol, msgType,
                            failIfMultipleFound, failIfNoneFound, lookupInExternalERs, lookupLimit,
                            okToLeaveOutInvalidOptionalFields, element.getAsJsonObject()));
                    generatedEventResults.add(generatedEvent);
                }
                boolean success = true;
                for (JsonElement result : generatedEventResults) {
                    JsonObject jsonObject = result.getAsJsonObject();
                    success &= jsonObject.has(META);
                }
                return new ResponseEntity<>(generatedEventResults, success ? HttpStatus.OK : HttpStatus.BAD_REQUEST);

            } else if (inputData.isJsonObject()) {
                JsonObject inputJsonObject = inputData.getAsJsonObject();
                JsonObject processedJson = processEvent(msgProtocol, msgType, failIfMultipleFound, failIfNoneFound,
                        lookupInExternalERs, lookupLimit, okToLeaveOutInvalidOptionalFields, inputJsonObject);
                HttpStatus status;
                String statusValue = null;
                if (processedJson.has(META)) {
                    status = HttpStatus.OK;
                    return new ResponseEntity<>(processedJson, status);
                } else if (processedJson.has(JSON_STATUS_CODE)) {
                    statusValue = processedJson.get(JSON_STATUS_CODE).toString();
                    try {
                        status = HttpStatus.resolve(Integer.parseInt(statusValue));
                        return new ResponseEntity<>(processedJson, status);
                    } catch (NumberFormatException e) {
                        log.error("Invalid status value: '" + statusValue + "' of response " + processedJson);
                        return createResponseEntity(HttpStatus.BAD_REQUEST, "Invalid status value: '"
                                + statusValue + "' of response " + processedJson, JSON_ERROR_STATUS);
                    }
                } else {
                    log.error("There is no status value: '" + statusValue + "' in the response" + processedJson);
                    return createResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "There is no status value: '"
                            + statusValue + "' in the response " + processedJson, JSON_ERROR_STATUS);
                }
            } else {
                return createResponseEntity(HttpStatus.BAD_REQUEST,
                        "Invalid JSON format,expected either single template or array of templates",
                        JSON_ERROR_STATUS);
            }
        } catch (REMGenerateException | JsonSyntaxException e) {
            return handleException(e);
        }
    }


    /**
     * To display response in browser or application
     * @param status          response code for the HTTP request
     * @param responseMessage the message according to response
     * @param resultMessage   whatever the result this message gives you idea about that
     * @param errorResponse   is to collect all the responses here.
     * @return ResponseEntity
     */
    public ResponseEntity<JsonObject> createResponseEntity(HttpStatus status, String responseMessage, String resultMessage,
                                                           JsonObject errorResponse) {
        initializeResponse(status, responseMessage, resultMessage, errorResponse);
        return new ResponseEntity<>(errorResponse, status);
    }
    public ResponseEntity<JsonObject> createResponseEntity(HttpStatus status, String responseMessage, String resultMessage) {
        return createResponseEntity(status, responseMessage, resultMessage, new JsonObject());
    }

    public void initializeResponse(HttpStatus status, String errorMessage, String resultMessage, JsonObject errorResponse) {
        errorResponse.addProperty(JSON_STATUS_CODE, status.value());
        errorResponse.addProperty(JSON_STATUS_RESULT, resultMessage);
        errorResponse.addProperty(JSON_ERROR_MESSAGE_FIELD, errorMessage);
    }

    /**
     * To handle the exception in one method
     * @param e taken general exception here
     * @return ResponseEntity
     */
    private ResponseEntity<JsonObject> handleException(Exception e) {
        String exceptionMessage = e.getMessage();
        if (e instanceof REMGenerateException) {
            List<HttpStatus> statuseList = List.of(
                    HttpStatus.NOT_ACCEPTABLE, HttpStatus.EXPECTATION_FAILED, HttpStatus.SERVICE_UNAVAILABLE,
                    HttpStatus.UNPROCESSABLE_ENTITY
            );
            for (HttpStatus status : statuseList) {
                if (exceptionMessage.contains(Integer.toString(status.value()))) {
                    return createResponseEntity(status, exceptionMessage, JSON_ERROR_STATUS);
                }
            }
            return createResponseEntity(HttpStatus.BAD_REQUEST, exceptionMessage, JSON_ERROR_STATUS);
        } else if (e instanceof JsonSyntaxException) {
            log.error("Failed to parse JSON", exceptionMessage);
            return createResponseEntity(HttpStatus.BAD_REQUEST, exceptionMessage, JSON_ERROR_STATUS);
        } else {
            log.error("Unexpected exception caught", exceptionMessage);
            return createResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, exceptionMessage, JSON_ERROR_STATUS);
        }
    }

    /**
     * This helper method basically generate or process single event
     * @param msgProtocol The message protocol, which tells us which service to invoke
     * @param msgType The type of message that needs to be generated. inputData
     * @param failIfMultipleFound
     * @param failIfNoneFound
     * @param lookupInExternalERs
     * @param lookupLimit
     * @param okToLeaveOutInvalidOptionalFields
     * @param jsonObject The content of the message which is used in creating the event details.
     * @return JsonObject generated event
     */

    public JsonObject processEvent(String msgProtocol, String msgType, Boolean failIfMultipleFound,
                                   Boolean failIfNoneFound, Boolean lookupInExternalERs, int lookupLimit,
                                   Boolean okToLeaveOutInvalidOptionalFields, JsonObject jsonObject) throws REMGenerateException, JsonSyntaxException {
        JsonElement parsedResponse;

        JsonObject event = erLookup(jsonObject, failIfMultipleFound, failIfNoneFound, lookupInExternalERs, lookupLimit);
        MsgService msgService = getMessageService(msgProtocol);

        if (msgService == null) {
            return createResponseEntity(HttpStatus.SERVICE_UNAVAILABLE,
                    "No protocol service has been found registered", JSON_ERROR_STATUS).getBody();
        }
        String response = msgService.generateMsg(msgType, event, isLenientEnabled(okToLeaveOutInvalidOptionalFields));
        parsedResponse = JsonParser.parseString(response);
        JsonObject parsedJson = parsedResponse.getAsJsonObject();

        if (parsedJson.has(JSON_ERROR_MESSAGE_FIELD)) {
            JsonObject eventResponse = new JsonObject();
            createResponseEntity(HttpStatus.BAD_REQUEST, JSON_ERROR_STATUS, TEMPLATE_ERROR, eventResponse);
            return eventResponse;
        } else {
            return parsedJson;
        }
    }

    private JsonObject erLookup(final JsonObject bodyJson, Boolean failIfMultipleFound, Boolean failIfNoneFound,
               final Boolean lookupInExternalERs, final int lookupLimit)
            throws REMGenerateException {
        // Checking ER lookup enabled or not
        if (erlookupConfig.getEventRepositoryEnabled() && bodyJson.toString().contains("%lookup%")) {
            // Checking ER lookup limit
            if(lookupLimit > 0) {
                JsonArray lookupLinks = bodyJson.get("eventParams").getAsJsonObject().get("links").getAsJsonArray();
                JsonArray links = new JsonArray();
                for (int i = 0; i < lookupLinks.size(); i++) {
                    if (lookupLinks.get(i).toString().contains("%lookup%")) {
                        String[] ids = null;

                        // prepare ER Query
                        String Query = ERLookupController.getQueryfromLookup(lookupLinks.get(i).getAsJsonObject());
                        String url = erlookupConfig.getErURL() + Query
                                + String.format("&shallow=%s&pageSize=%d", !lookupInExternalERs, lookupLimit);

                        // Execute ER Query
                        try {
                            response = restTemplate.getForEntity(url, String.class);
                            if (response.getStatusCode() == HttpStatus.OK) {
                                log.info("The result from Event Repository is: " + response.getStatusCodeValue());
                            }
                        } catch (Exception e) {
                            log.error("unable to connect configured Event Repository URL" + e.getMessage());
                            response = null;
                        }
                        if (response == null) {
                            throw new REMGenerateException(RemremGenerateServiceConstants.NO_ER);
                        }
                        String responseBody = response.getBody();
                        ids = ERLookupController.getIdsfromResponseBody(responseBody);
                        boolean failIfNoneFoundValue = failIfNoneFound;
                        boolean failIfMultipleFoundValue = failIfMultipleFound;

                        // Checking ER lookup has options field present or not
                        if (lookupLinks.get(i).toString().contains("options")) {
                            final JsonObject lookup = lookupLinks.get(i).getAsJsonObject().get("%lookup%")
                                    .getAsJsonObject();
                            for (Entry<String, JsonElement> options : lookup.get("options").getAsJsonObject()
                                    .entrySet()) {
                                final String optionKey = options.getKey();
                                final String optionValue = options.getValue().getAsString();
                                if (optionKey.equals("failIfNoneFound") && (optionValue.equalsIgnoreCase("true")
                                        || optionValue.equalsIgnoreCase("false"))) {
                                    failIfNoneFoundValue = Boolean.parseBoolean(optionValue);
                                } else if (optionKey.equals("failIfMultipleFound")
                                        && (optionValue.equalsIgnoreCase("true")
                                                || optionValue.equalsIgnoreCase("false"))) {
                                    failIfMultipleFoundValue = Boolean.parseBoolean(optionValue);
                                } else {
                                    throw new REMGenerateException(
                                            RemremGenerateServiceConstants.LOOKUP_OPTIONS_NOT_FULFILLED);
                                }
                            }
                        }

                        // Checking ER lookup result
                        if (failIfMultipleFoundValue && ids != null && ids.length > 1) {
                            throw new REMGenerateException(
                                    RemremGenerateServiceConstants.UNAVAILABLE_FOR_FAILIFMULTIPLEFOUND);
                        } else if (failIfNoneFoundValue && ids.length == 0) {
                            throw new REMGenerateException(
                                    RemremGenerateServiceConstants.UNAVAILABLE_FOR_FAILIFNONEFOUND);
                        }

                        // Replace lookup values
                        ERLookupController.convertbodyJsontoLookupJson(ids, lookupLinks.get(i).getAsJsonObject(),
                                links);
                    } else {
                        links.add(lookupLinks.get(i).getAsJsonObject());
                    }
                    bodyJson.get("eventParams").getAsJsonObject().add("links", links);
                }
            } else {
                log.error("Lookup limit must be greater than or equals to 1");
            }
        } else {
            return bodyJson;
        }
        return bodyJson;
    }

    /**
     * Used to display the versions of generate and all loaded protocols.
     *
     * @return json with version details.
     */
    @ApiOperation(value = "To get versions of generate and all loaded protocols", response = String.class)
    @RequestMapping(value = "/versions", method = RequestMethod.GET)
    public JsonElement getVersions() {
        Map<String, Map<String, String>> versions = new VersionService().getMessagingVersions();
        return parser.parse(versions.toString());
    }

    /**
     * Used to display the available message protocol list and their edition names.
     *
     * @return json with service names and respective edition details.
     */
    @ApiOperation(value = "To get the available message protocol list and edition names", response = String.class)
    @RequestMapping(value = "/message_protocols", method = RequestMethod.GET)
    public JsonElement getMessageProtocols() {
        JsonArray array = new JsonArray();
        for (MsgService service : msgServices) {
            JsonObject protocolObject = new JsonObject();
            protocolObject.addProperty("name", service.getServiceName());
            try {
                protocolObject.addProperty("edition", service.getProtocolEdition());
            } catch (NoSuchMethodError | AbstractMethodError e) {
                // Ignored intentionally in order to ensure compatibility with
                // eiffel-remrem-semantics:2.2.0 and older.
                log.error("An old library, without implementation of MsgService.getProtocolEdition() is used. Please, upgrade to a newer library implementing eiffel-remrem-protocol-interface:2.1.2 or higher.");
            }
            array.add(protocolObject);
        }
        return array;
    }
	
    /**
     * Returns available Eiffel event types as listed in EiffelEventType enum.
     *
     * @return string collection with event types.
     */
    @ApiOperation(value = "To get available eiffel event types based on the message protocol", response = String.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Event  types got successfully"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 503, message = "Message protocol is invalid") })
    @RequestMapping(value = "/event_types/{mp}", method = RequestMethod.GET)
    public ResponseEntity<?> getEventTypes(
            @ApiParam(value = "message protocol", required = true) @PathVariable("mp") final String msgProtocol,
            @ApiIgnore final RequestEntity requestEntity) {
        MsgService msgService = getMessageService(msgProtocol);
        try {
            if (msgService != null) {
                return presentResponse(msgService.getSupportedEventTypes(), HttpStatus.OK, requestEntity);
            } else {
                return presentResponse(parser.parse(RemremGenerateServiceConstants.NO_SERVICE_ERROR),
                        HttpStatus.SERVICE_UNAVAILABLE, requestEntity);
            }
        } catch (Exception e) {
            log.error("Unexpected exception caught", e);
            return presentResponse(parser.parse(RemremGenerateServiceConstants.INTERNAL_SERVER_ERROR),
                    HttpStatus.INTERNAL_SERVER_ERROR, requestEntity);
        }
    }

    /**
     * Returns an eiffel event template matching the type specified in the path.
     *
     * @return json containing eiffel event template.
     */
    @ApiOperation(value = "To get eiffel event template of specified event type", response = String.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Template got successfully"),
            @ApiResponse(code = 400, message = "Requested template is not available"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 503, message = "Message protocol is invalid") })
    @RequestMapping(value = "/template/{type}/{mp}", method = RequestMethod.GET)
    public ResponseEntity<?> getEventTypeTemplate(
            @ApiParam(value = "message type", required = true) @PathVariable("type") final String msgType,
            @ApiParam(value = "message protocol", required = true) @PathVariable("mp") final String msgProtocol,
            @ApiIgnore final RequestEntity requestEntity) {
        MsgService msgService = getMessageService(msgProtocol);
        try {
            if (msgService != null) {
                JsonElement template = msgService.getEventTemplate(msgType);
                if (template != null) {
                    return presentResponse(template, HttpStatus.OK, requestEntity);
                } else {
                    return presentResponse(parser.parse(RemremGenerateServiceConstants.NO_TEMPLATE_ERROR),
                            HttpStatus.NOT_FOUND, requestEntity);
                }
            } else {
                return presentResponse(parser.parse(RemremGenerateServiceConstants.NO_SERVICE_ERROR),
                        HttpStatus.SERVICE_UNAVAILABLE, requestEntity);
            }
        } catch (Exception e) {
            log.error("Unexpected exception caught", e);
            return presentResponse(parser.parse(RemremGenerateServiceConstants.INTERNAL_SERVER_ERROR),
                    HttpStatus.INTERNAL_SERVER_ERROR, requestEntity);
        }
    }

    private MsgService getMessageService(final String messageProtocol) {
        for (MsgService service : msgServices) {
            if (service.getServiceName().equals(messageProtocol)) {
                return service;
            }
        }
        return null;
    }

    /**
     * To display pretty formatted json in browser
     * 
     * @param rawJson
     *            json content
     * @return html formatted json string
     */
    private String buildHtmlReturnString(final String rawJson) {
        final String htmlHead = "<!DOCTYPE html><html><body><pre>";
        final String htmlTail = "</pre></body></html>";
        return htmlHead + rawJson + htmlTail;
    }

    /**
     * To display response in browser or application
     * 
     * @param message
     *            json content
     * @param status
     *            response code for the HTTP request
     * @param requestEntity
     *            entity of the HTTP request
     * @return entity to present response in browser or application
     */
    private ResponseEntity<?> presentResponse(final Object message, final HttpStatus status,
            final RequestEntity requestEntity) {
        if (requestEntity.getHeaders().getAccept().contains(MediaType.TEXT_HTML)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return new ResponseEntity<>(buildHtmlReturnString(gson.toJson(message)), status);
        } else {
            return new ResponseEntity<>(message, status);
        }
    }

    public boolean isLenientEnabled(final boolean okToLeaveOutInvalidOptionalFields) throws REMGenerateException {
        if(this.lenientValidationEnabledToUsers && okToLeaveOutInvalidOptionalFields) {
            return true;
        }
        else if(!this.lenientValidationEnabledToUsers && okToLeaveOutInvalidOptionalFields) {
            throw new REMGenerateException(RemremGenerateServiceConstants.NOT_ACCEPTABLE);
        }
        return false;
    }

    /**
     * To read the jasypt key from jasypt.key file
     *
     * @param jasyptKeyFilePath
     *            file path in which jasypt key is stored
     * @return jasypt key fetched from the file
     */
    public static String readJasyptKeyFile(final String jasyptKeyFilePath) {
        String jasyptKey = "";
        final FileInputStream file;
        try {
            if (StringUtils.isNotBlank(jasyptKeyFilePath)) {
                file = new FileInputStream(jasyptKeyFilePath);
                BufferedReader reader = new BufferedReader(new InputStreamReader(file));
                jasyptKey = reader.readLine();
                if(jasyptKey == null) {
                    return "";
                }
            }
        } catch (IOException e) {
            log.error("Could not read the jasypt key from the jasypt key file path: " + e.getMessage(), e);
        }
        return jasyptKey;
    }
}
