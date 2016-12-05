package com.ericsson.eiffel.remrem.generate.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ericsson.eiffel.remrem.shared.VersionService;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

@RestController @RequestMapping("/versions")
public class VersionsController {
	
	JsonParser parser = new JsonParser();
	
	@RequestMapping(value = "", method = RequestMethod.POST)
    public JsonElement getVersions() {
		Map versions = VersionService.getMessagingVersions();
		return parser.parse(versions.toString());
	}
}
