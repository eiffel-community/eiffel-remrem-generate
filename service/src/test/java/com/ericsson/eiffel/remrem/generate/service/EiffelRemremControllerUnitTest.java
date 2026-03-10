/*
    Copyright 2019 Ericsson AB.
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
package com.ericsson.eiffel.remrem.generate.service;


import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.ericsson.eiffel.remrem.protocol.ValidationResult;
import com.ericsson.eiffel.remrem.semantics.SemanticsService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.ericsson.eiffel.remrem.generate.config.ErLookUpConfig;
import com.ericsson.eiffel.remrem.generate.controller.RemremGenerateController;
import com.ericsson.eiffel.remrem.protocol.MsgService;
import com.google.gson.JsonElement;

class Eiffel3MsgService implements MsgService {

    public String successOutput = null;
    public String errorOutput = null;

    @Override
    public String generateMsg(String s, JsonObject jsonObject) {
        return "";
    }

    @Override
    public String generateMsg(String s, JsonObject jsonObject, Boolean aBoolean) {
        if (s.equals("eiffelartifactnew")) {
            return successOutput;
        } else if (s.equals("eiffelartifactnewevent")) {
            return errorOutput;
        }

        return "";
    }

    @Override
    public String generateMsg(String s, JsonObject jsonObject, HashMap<String, Object> hashMap) {
        throw new AbstractMethodError("Not implemented by " + getClass());
    }

    @Override
    public String getEventId(JsonObject jsonObject) {
        return "";
    }

    @Override
    public String getEventType(JsonObject jsonObject) {
        return "";
    }

    @Override
    public Collection<String> getSupportedEventTypes() {
        return List.of();
    }

    @Override
    public JsonElement getEventTemplate(String s) {
        return null;
    }

    @Override
    public String getProtocolEdition() {
        return "";
    }

    @Override
    public String getServiceName() {
        return "eiffel3";
    }

    @Override
    public ValidationResult validateMsg(String s, JsonObject jsonObject) {
        return null;
    }

    @Override
    public ValidationResult validateMsg(String s, JsonObject jsonObject, Boolean aBoolean) {
        return null;
    }

    @Override
    public ValidationResult validateMsg(String s, JsonObject jsonObject, HashMap<String, Object> hashMap) {
        return null;
    }

    @Override
    public String generateRoutingKey(JsonObject jsonObject, String s, String s1, String s2) {
        return "";
    }

    @Override
    public String generateRoutingKey(JsonObject jsonObject, String s, String s1, String s2, String s3) {
        return "";
    }
}

@RunWith(SpringRunner.class)
public class EiffelRemremControllerUnitTest {
    
    @InjectMocks
    RemremGenerateController unit = new RemremGenerateController();

    
    @Mock
    MsgService service;
    
//    @Mock
    Eiffel3MsgService service2 = new Eiffel3MsgService();

    @Spy
    private List<MsgService> msgServices = new ArrayList<MsgService>();
    @Mock
    JsonElement body;

    @Spy
    ErLookUpConfig erLookupConfig;

    @SuppressWarnings("resource")
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        msgServices.add(service);
        msgServices.add(service2);
        Mockito.when(service.getServiceName()).thenReturn("eiffelsemantics");

        URL jsonInputURL = getClass().getClassLoader().getResource("successInput.json");
        String inputFilePath = jsonInputURL.getPath().replace("%20"," ");
        File jsonFile = new File(inputFilePath);
        String successOutput = new BufferedReader(new FileReader(jsonFile)).readLine();

        jsonInputURL = getClass().getClassLoader().getResource("errorInput.json");
        inputFilePath = jsonInputURL.getPath().replace("%20"," ");
        jsonFile = new File(inputFilePath);
        String errorOutput = new BufferedReader(new FileReader(jsonFile)).readLine();

        URL lv_jsonInputURL = getClass().getClassLoader().getResource("lv_successInput.json");
        String lv_inputFilePath = lv_jsonInputURL.getPath().replace("%20"," ");
        File lv_jsonFile = new File(lv_inputFilePath);
        String lv_successOutput = new BufferedReader(new FileReader(lv_jsonFile)).readLine();

        Mockito.when(service.generateMsg(
                eq("eiffelactivityfinished"),
                Mockito.any(), (HashMap<String, Object>) Mockito.any())).thenReturn(successOutput);

        Mockito.when(service.generateMsg(
                eq("EiffelActivityFinished"),
                Mockito.any(), (HashMap<String, Object>) Mockito.any())).thenReturn(errorOutput);

        service2.successOutput = successOutput;
        service2.errorOutput = errorOutput;

        Mockito.when(service.generateMsg(
                eq("EiffelArtifactCreatedEvent"),
                Mockito.any(), (HashMap<String, Object>) Mockito.any())).thenReturn(lv_successOutput);
    }

    private HashMap<String, Object> newHashMap(String key, Object value) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put(key, value);
        return properties;
    }

    @Test
    public void testSemanticsSuccessEvent() throws Exception {
        File file = new File("src/test/resources/ErlookupConfidenceLevelOutput.json");
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(new FileReader(file)).getAsJsonObject();
        ResponseEntity<?> elem = unit.generate("eiffelsemantics", "eiffelactivityfinished",
           false, false, true, 1,
            newHashMap(SemanticsService.LENIENT_VALIDATION, false), json);
        assertEquals(elem.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void testSemanticsSuccessArrayEvent() throws Exception {
        File file = new File("src/test/resources/ErlookupConfidenceLevelArrayOutput.json");
        JsonParser parser = new JsonParser();
        JsonArray json = parser.parse(new FileReader(file)).getAsJsonArray();
        ResponseEntity<?> elem = unit.generate("eiffelsemantics", "eiffelactivityfinished",
            false, false, true, 1,
             newHashMap(SemanticsService.LENIENT_VALIDATION, false), json);
        assertEquals(elem.getStatusCode(), HttpStatus.OK);
    }


    @Test
    public void testSemanticsFailureEvent() throws Exception {
        File file = new File("src/test/resources/ErlookupConfidenceLevelOutput.json");
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(new FileReader(file)).getAsJsonObject();
        ResponseEntity<?> elem = unit.generate("eiffelsemantics", "EiffelActivityFinished",
            false, false, true, 1,
            newHashMap(SemanticsService.LENIENT_VALIDATION, false), json);
        assertEquals(elem.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testSemanticsFailureEventArray() throws Exception {
        File file = new File("src/test/resources/ErlookupConfidenceLevelArrayOutput.json");
        JsonParser parser = new JsonParser();
        JsonArray json = parser.parse(new FileReader(file)).getAsJsonArray();
        ResponseEntity<?> elem = unit.generate("eiffelsemantics", "EiffelActivityFinished",
            false, false, true, 1,
            newHashMap(SemanticsService.LENIENT_VALIDATION, false), json);
        assertEquals(elem.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testEiffel3SuccessEvent() throws Exception {
        File file = new File("src/test/resources/ErlookupConfidenceLevelOutput.json");
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(new FileReader(file)).getAsJsonObject();
        ResponseEntity<?> elem = unit.generate("eiffel3", "eiffelartifactnew",
            false, false, true, 1,
            newHashMap(SemanticsService.LENIENT_VALIDATION, false), json);
        assertEquals(elem.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void testEiffel3SuccessEventArray() throws Exception {
        File file = new File("src/test/resources/ErlookupConfidenceLevelArrayOutput.json");
        JsonParser parser = new JsonParser();
        JsonArray json = parser.parse(new FileReader(file)).getAsJsonArray();
        ResponseEntity<?> elem = unit.generate("eiffel3", "eiffelartifactnew",
            false, false, true, 1,
            newHashMap(SemanticsService.LENIENT_VALIDATION, false), json);
        assertEquals(elem.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void testEiffel3FailureEvent() throws Exception {
        File file = new File("src/test/resources/ErlookupConfidenceLevelOutput.json");
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(new FileReader(file)).getAsJsonObject();
        ResponseEntity<?> elem = unit.generate("eiffel3", "eiffelartifactnewevent",
            false, false, true, 1,
            newHashMap(SemanticsService.LENIENT_VALIDATION, false), json);
        assertEquals(elem.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testEiffel3FailureEventArray() throws Exception {
        File file = new File("src/test/resources/ErlookupConfidenceLevelArrayOutput.json");
        JsonParser parser = new JsonParser();
        JsonArray json = parser.parse(new FileReader(file)).getAsJsonArray();
        ResponseEntity<?> elem = unit.generate("eiffel3", "eiffelartifactnewevent",
            false, false, true, 1,
            newHashMap(SemanticsService.LENIENT_VALIDATION, false), json);
        assertEquals(elem.getStatusCode(), HttpStatus.BAD_REQUEST);
    }
    @Test
    public void testMessageServiceUnavailableEvent() throws Exception {
        File file = new File("src/test/resources/ArtifactCreated.json");
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(new FileReader(file)).getAsJsonObject();
        ResponseEntity<?> elem = unit.generate("other", "EiffelActivityFinishedEvent",
            false, false, true, 1,
            newHashMap(SemanticsService.LENIENT_VALIDATION, false), json);
        assertEquals(elem.getStatusCode(), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @Test
    public void testlenientValidation() throws Exception {
        File file = new File("src/test/resources/ArtifactCreated.json");
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(new FileReader(file)).getAsJsonObject();
        unit.setLenientValidationEnabledToUsers(true);
        ResponseEntity<?> elem = unit.generate("eiffelsemantics", "EiffelArtifactCreatedEvent",
            false, false, true, 1,
            newHashMap(SemanticsService.LENIENT_VALIDATION, false), json);
        assertEquals(elem.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void testlenientValidationEventArray() throws Exception {
        File file = new File("src/test/resources/ArtifactCreatedEventArray.json");
        JsonParser parser = new JsonParser();
        JsonArray json = parser.parse(new FileReader(file)).getAsJsonArray();
        unit.setLenientValidationEnabledToUsers(true);
        ResponseEntity<?> elem = unit.generate("eiffelsemantics", "EiffelArtifactCreatedEvent",
            false, false, true, 1,
            newHashMap(SemanticsService.LENIENT_VALIDATION, false), json);
        assertEquals(elem.getStatusCode(), HttpStatus.OK);
    }
    @Test
    public void testJasyptFileSuccess() throws IOException {
        String jasyptPath = "src/test/resources/jasypt.key";
        String jasyptKey = RemremGenerateController.readJasyptKeyFile(jasyptPath);
        assertEquals("docker", jasyptKey);
    }

    @Test
    public void testJasyptFileWithEmptyKey() {
        String jasyptPath = "src/test/resources/emptyJasypt.key";
        String jasyptKey = RemremGenerateController.readJasyptKeyFile(jasyptPath);
        assertEquals("", jasyptKey);
    }

    @Test
    public void testJasyptFileFailure() throws IOException {
        String jasyptPath = "src/test/jasypt.key";
        String jasyptKey = RemremGenerateController.readJasyptKeyFile(jasyptPath);
        assertEquals("", jasyptKey);
    }
}
