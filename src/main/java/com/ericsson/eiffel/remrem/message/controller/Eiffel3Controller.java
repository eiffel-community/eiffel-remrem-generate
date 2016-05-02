package com.ericsson.eiffel.remrem.message.controller;

import com.ericsson.eiffel.remrem.message.services.MsgService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Profile("eiffel3")
@RestController @RequestMapping("/eiffel3") public class Eiffel3Controller {

    @Autowired
    @Qualifier("eiffel3")
    private MsgService msgService;

    JsonParser parser = new JsonParser();
    Gson gson = new Gson();

    @RequestMapping(value = "", method = RequestMethod.POST)
    public JsonElement generateMsg(@RequestParam("msgType") String msgType,
        @RequestBody JsonObject bodyJson) {
        Map testMap = gson.fromJson(bodyJson.get("eventParams"), Map.class);
        return parser.parse(msgService
            .generateMsg(msgType, bodyJson.get("msgParams").getAsJsonObject(), testMap));
    }
}
