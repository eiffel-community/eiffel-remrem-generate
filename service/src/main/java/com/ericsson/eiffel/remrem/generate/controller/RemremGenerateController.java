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

import com.ericsson.eiffel.remrem.generate.constants.RemremGenerateServiceConstants;
import com.ericsson.eiffel.remrem.protocol.MsgService;
import com.ericsson.eiffel.remrem.semantics.EiffelEventType;
import com.ericsson.eiffel.remrem.semantics.factory.EiffelOutputValidatorFactory;
import com.ericsson.eiffel.remrem.semantics.validator.EiffelValidationException;
import com.ericsson.eiffel.remrem.semantics.validator.EiffelValidator;
import com.ericsson.eiffel.remrem.shared.VersionService;
import com.google.gson.*;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/*")
@Api(value = "REMReM Generate Service", description = "REST API for generating Eiffel messages")
public class RemremGenerateController {

    // regular expression that exclude "swagger-ui.html" from request parameter
    private static final String REGEX = ":^(?!swagger-ui.html).*$";

    @Autowired
    private List<MsgService> msgServices;
    private JsonParser parser = new JsonParser();

    /**
     * Returns event information as json element based on the message protocol, taking message type and json body as
     * input.
     * <p>
     * <p>
     * Parameters:
     * msgProtocol - The message protocol, which tells us which service to invoke.
     * msgType - The type of message that needs to be generated.
     * bodyJson - The content of the message which is used in creating the event details.
     * <p>
     * Returns:
     * The event information as a json element
     */
    @ApiOperation(value = "To generate eiffel event based on the message protocol", response = String.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Event sent successfully"),
            @ApiResponse(code = 400, message = "Malformed JSON"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 503, message = "Message protocol is invalid")})
    @RequestMapping(value = "/{mp" + REGEX + "}", method = RequestMethod.POST)
    public ResponseEntity<?> generate(@ApiParam(value = "message protocol", required = true) @PathVariable("mp") final String msgProtocol,
                                      @ApiParam(value = "message type", required = true) @RequestParam("msgType") final String msgType,
                                      @ApiParam(value = "JSON message", required = true) @RequestBody final JsonObject bodyJson) {
        MsgService msgService = getMessageService(msgProtocol);
        String response;
        try {
            if (msgService != null) {
                response = msgService.generateMsg(msgType, bodyJson);
                JsonElement parsedResponse = parser.parse(response);
                if (!parsedResponse.getAsJsonObject().has(RemremGenerateServiceConstants.JSON_ERROR_MESSAGE_FIELD)) {
                    return new ResponseEntity<>(parsedResponse, HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(parsedResponse, HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>(parser.parse(RemremGenerateServiceConstants.NO_SERVICE_ERROR),
                        HttpStatus.SERVICE_UNAVAILABLE);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(parser.parse(RemremGenerateServiceConstants.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
     * Returns available Eiffel event types as listed in EiffelEventType enum.
     *
     * @return string collection with event types.
     */
    @ApiOperation(value = "To get available eiffel event types based on the message protocol", response = String.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Event  types got successfully"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 503, message = "Message protocol is invalid")})
    @RequestMapping(value = "/event_types/{mp}", method = RequestMethod.GET)
    public ResponseEntity<?> getEventTypes(@ApiParam(value = "message protocol", required = true) @PathVariable("mp") final String msgProtocol,
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
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Template got successfully"),
            @ApiResponse(code = 400, message = "Requested template is not available"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 503, message = "Message protocol is invalid")})
    @RequestMapping(value = "/template/{type}/{mp}", method = RequestMethod.GET)
    public ResponseEntity<?> getEventTypeTemplate(@ApiParam(value = "message type", required = true) @PathVariable("type") final String msgType,
                                                  @ApiParam(value = "message protocol", required = true) @PathVariable("mp") final String msgProtocol,
                                                  @ApiIgnore final RequestEntity requestEntity) {
        MsgService msgService = getMessageService(msgProtocol);
        try {
            if (msgService != null) {
                JsonElement template = msgService.getEventTemplate(msgType);
                if (template != null) {
                    return presentResponse(template, HttpStatus.OK, requestEntity);
                } else {
                    return presentResponse(parser.parse(RemremGenerateServiceConstants.NO_TEMPLATE_ERROR), HttpStatus.NOT_FOUND, requestEntity);
                }
            } else {
                return presentResponse(parser.parse(RemremGenerateServiceConstants.NO_SERVICE_ERROR), HttpStatus.SERVICE_UNAVAILABLE, requestEntity);
            }
        } catch (Exception e) {
            return presentResponse(parser.parse(RemremGenerateServiceConstants.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR, requestEntity);
        }
    }


    /**
     * Returns message details about what is wrong in eiffel event
     *
     * @param msgType  event type
     * @param bodyJson eiffel event
     * @return string with details about exception
     */
    @ApiOperation(value = "To validate eiffel event", response = String.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Message is valid"),
            @ApiResponse(code = 400, message = "Not valid message"),
            @ApiResponse(code = 500, message = "Internal server error"),})
    @RequestMapping(value = "/validate/{msgType}/{mp}", method = RequestMethod.POST)
    public ResponseEntity<?> validate(@ApiParam(value = "message type", required = true) @PathVariable("msgType") final String msgType,
                                      @ApiParam(value = "message protocol", required = true) @PathVariable("mp") final String msgProtocol,
                                      @ApiParam(value = "JSON message", required = true) @RequestBody final JsonObject bodyJson) {
        try {
            if (msgProtocol.equals("eiffelsemantics")) {
                EiffelValidator validator = EiffelOutputValidatorFactory.getEiffelValidator(EiffelEventType.fromString(msgType));
                validator.validate(bodyJson.getAsJsonObject("msgParams"));
            }
            return new ResponseEntity<>(parser.parse(RemremGenerateServiceConstants.MESSAGE_VALID), HttpStatus.OK);
        } catch (EiffelValidationException e) {
            return new ResponseEntity<>(parser.parse(e.getCause().getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(parser.parse(RemremGenerateServiceConstants.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
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
     * @param rawJson json content
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
     * @param message       json content
     * @param status        response code for the HTTP request
     * @param requestEntity entity of the HTTP request
     * @return entity to present response in browser or application
     */
    private ResponseEntity<?> presentResponse(final Object message, final HttpStatus status, final RequestEntity requestEntity) {
        if (requestEntity.getHeaders().getAccept().contains(MediaType.TEXT_HTML)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return new ResponseEntity<>(buildHtmlReturnString(gson.toJson(message)), status);
        } else {
            return new ResponseEntity<>(message, status);
        }
    }
}
