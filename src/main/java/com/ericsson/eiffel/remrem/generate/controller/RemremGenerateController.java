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
	
	@RequestMapping(value="/{mp}", method = RequestMethod.POST)
	public JsonElement generate(@PathVariable String mp,@RequestParam("msgType") String msgType,
           @RequestBody JsonObject bodyJson) {
		MsgService msgService = getMessageService(mp);
        if(msgService!=null){
        	return parser.parse(msgService.generateMsg(msgType, bodyJson)); 
        }else{
        	return null;
        }
    }
	
	
    public MsgService getMessageService(String messageProtocol) {
        for (MsgService service : msgServices) {
            if (service.getServiceName().equals(messageProtocol)) {
                return service;
            }
        }
        return null;
    }
}
