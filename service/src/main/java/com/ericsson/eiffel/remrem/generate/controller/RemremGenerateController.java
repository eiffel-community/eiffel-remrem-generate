package com.ericsson.eiffel.remrem.generate.controller;

import java.util.List;

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
            	JsonElement resp = parser.parse(response);
                if(!resp.getAsJsonObject().has(RemremGenerateServiceConstants.MESSAGE)) {
                	return new ResponseEntity<>(resp,HttpStatus.OK);
                }
                else {
                	return new ResponseEntity<>(resp,HttpStatus.BAD_REQUEST);
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


    private MsgService getMessageService(String messageProtocol) {
        for (MsgService service : msgServices) {
            if (service.getServiceName().equals(messageProtocol)) {
                return service;
            }
        }
        return null;
    }
}
