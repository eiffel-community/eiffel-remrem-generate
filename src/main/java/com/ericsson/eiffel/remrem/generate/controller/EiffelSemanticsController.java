package com.ericsson.eiffel.remrem.generate.controller;

import com.ericsson.eiffel.remrem.protocol.MsgService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/eiffelsemantics")
public class EiffelSemanticsController {
    @Autowired @Qualifier("eiffel-semantics") private MsgService msgService;

    JsonParser parser = new JsonParser();

    @RequestMapping(value = "", method = RequestMethod.POST)
    public JsonElement generateMsg(@RequestParam("msgType") String msgType,
        @RequestBody JsonObject bodyJson) {
        assert msgService != null;
        return parser.parse(msgService.generateMsg(msgType, bodyJson));
    }
}
