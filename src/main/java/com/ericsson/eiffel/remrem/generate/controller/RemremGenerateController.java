package com.ericsson.eiffel.remrem.generate.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ericsson.eiffel.remrem.protocol.MsgService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
    public JsonElement generate(@PathVariable String mp, @RequestParam("msgType") String msgType,
            @RequestBody JsonObject bodyJson) {
        MsgService msgService = getMessageService(mp);
        if (msgService != null) {
            return parser.parse(msgService.generateMsg(msgType, bodyJson));
        } else {
            return null;
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
