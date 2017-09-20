/*
    Copyright 2017 Ericsson AB.
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

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ericsson.eiffel.remrem.generate.constants.RemremGenerateServiceConstants;
import com.ericsson.eiffel.remrem.protocol.MsgService;
import com.ericsson.eiffel.remrem.shared.VersionService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;;

@RestController @RequestMapping("/*")
public class RemremGenerateController {

    @Autowired
    private List<MsgService> msgServices;
    private JsonParser parser = new JsonParser();

    /**
    *  Returns event information as json element based on the message protocol, taking message type and json body as input.
    *   
    * 
    *  Parameters:
    *  mp - The message protocol , which tells us which service to invoke.
    *  msgType - The type of message that needs to be generated.
    *  bodyJson - The content of the message which is used in creating the event details.
    *  
    *  Returns:
    *  The event information as a json element
    * 
    */
    @RequestMapping(value = "/{mp}", method = RequestMethod.POST)
    public ResponseEntity<?> generate(@PathVariable String mp, @RequestParam("msgType") String msgType,
            @RequestBody JsonObject bodyJson) {
        MsgService msgService = getMessageService(mp);
        String response= "";
        try{
            if (msgService != null) {
            	response = msgService.generateMsg(msgType, bodyJson);
            	JsonElement parsedResponse = parser.parse(response);
                if(!parsedResponse.getAsJsonObject().has(RemremGenerateServiceConstants.JSON_ERROR_MESSAGE_FIELD)) {
                	return new ResponseEntity<>(parsedResponse,HttpStatus.OK);
                }
                else {
                	return new ResponseEntity<>(parsedResponse,HttpStatus.BAD_REQUEST);
                }
            }
            else {
            	return new ResponseEntity<>(parser.parse(RemremGenerateServiceConstants.NO_SERVICE_ERROR),HttpStatus.SERVICE_UNAVAILABLE);
            }
        }        
        catch(Exception e) {
        	return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * this method is used to display the versions of generate and all loaded protocols.
     *
     * @return this method returns a json with version details. 
     */
      @RequestMapping(value = "/versions", method = RequestMethod.GET)
    public JsonElement getVersions() {
        Map<String, Map<String, String>> versions = new VersionService().getMessagingVersions();
        return parser.parse(versions.toString());
    }


    private MsgService getMessageService(String messageProtocol) {
        for (MsgService service : msgServices) {
            if (service.getServiceName().equals(messageProtocol)) {
                return service;
            }
        }
        return null;
    }
}
