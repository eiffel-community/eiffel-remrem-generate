package com.ericsson.eiffel.remrem.generate.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ericsson.eiffel.remrem.protocol.MsgService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


@RestController @RequestMapping("/generate") 
public class RemremGenerateController {
	
	private MsgService msgService;
	private JsonParser parser = new JsonParser();
	
	@RequestMapping(value = "", method = RequestMethod.POST)
    public JsonElement generateMsg(@RequestParam("mp") String mp,@RequestParam("msgType") String msgType,
           @RequestBody JsonObject bodyJson) {
        msgService = getMessageService(mp);
        if(msgService!=null){
        	return parser.parse(msgService.generateMsg(msgType, bodyJson));
        }else{
        	return null;
        }
        
    }
	
	
	public MsgService getMessageService(String messageProtocol){
	    String className = System.getProperty("eiffel.protocol");
	    try {
		    MsgService service = (MsgService) Class.forName(className).newInstance();
		    if(service.getServiceName().equals(messageProtocol))
		        return service;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			System.out.println("**************** EXCEPTION ********************* ");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

}
