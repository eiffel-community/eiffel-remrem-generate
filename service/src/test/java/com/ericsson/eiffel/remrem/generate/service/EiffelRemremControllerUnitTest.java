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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.ericsson.eiffel.remrem.generate.config.ErLookUpConfig;
import com.ericsson.eiffel.remrem.generate.controller.RemremGenerateController;
import com.ericsson.eiffel.remrem.protocol.MsgService;
import com.google.gson.JsonElement;

@RunWith(SpringRunner.class)
public class EiffelRemremControllerUnitTest {
    
    @InjectMocks
    RemremGenerateController unit = new RemremGenerateController();
    
    @Mock
    MsgService service;
    
    @Mock
    MsgService service2;

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
        Mockito.when(service2.getServiceName()).thenReturn("eiffel3");

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
                Mockito.eq("eiffelactivityfinished"),
                Mockito.any(), Mockito.anyBoolean())).thenReturn(successOutput);

        Mockito.when(service.generateMsg(
                Mockito.eq("EiffelActivityFinished"),
                Mockito.any(), Mockito.anyBoolean())).thenReturn(errorOutput);

        Mockito.when(service2.generateMsg(
                Mockito.eq("eiffelartifactnew"),
                Mockito.any(), Mockito.anyBoolean())).thenReturn(successOutput);

        Mockito.when(service2.generateMsg(
                Mockito.eq("eiffelartifactnewevent"),
                Mockito.any(), Mockito.anyBoolean())).thenReturn(errorOutput);

        Mockito.when(service.generateMsg(
                Mockito.eq("EiffelArtifactCreatedEvent"),
                Mockito.any(), Mockito.anyBoolean())).thenReturn(lv_successOutput);
    }

    @Test
    public void testSemanticsSuccessEvent() throws Exception {
        File file = new File("src/test/resources/ErlookupConfidenceLevelOutput.json");
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(new FileReader(file)).getAsJsonObject();
        ResponseEntity<?> elem = unit.generate("eiffelsemantics", "eiffelactivityfinished", false, false, true, 1, false, json);
        assertEquals(elem.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void testSemanticsSuccessArrayEvent() throws Exception {
        File file = new File("src/test/resources/ErlookupConfidenceLevelArrayOutput.json");
        JsonParser parser = new JsonParser();
        JsonArray json = parser.parse(new FileReader(file)).getAsJsonArray();
        ResponseEntity<?> elem = unit.generate("eiffelsemantics", "eiffelactivityfinished", false, false, true, 1, false, json);
        assertEquals(elem.getStatusCode(), HttpStatus.OK);
    }


    @Test
    public void testSemanticsFailureEvent() throws Exception {
        File file = new File("src/test/resources/ErlookupConfidenceLevelOutput.json");
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(new FileReader(file)).getAsJsonObject();
        ResponseEntity<?> elem = unit.generate("eiffelsemantics", "EiffelActivityFinished", false, false, true, 1, false, json);
        assertEquals(elem.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testSemanticsFailureEventArray() throws Exception {
        File file = new File("src/test/resources/ErlookupConfidenceLevelArrayOutput.json");
        JsonParser parser = new JsonParser();
        JsonArray json = parser.parse(new FileReader(file)).getAsJsonArray();
        ResponseEntity<?> elem = unit.generate("eiffelsemantics", "EiffelActivityFinished", false, false, true, 1, false, json);
        assertEquals(elem.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testEiffel3SuccessEvent() throws Exception {
        File file = new File("src/test/resources/ErlookupConfidenceLevelOutput.json");
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(new FileReader(file)).getAsJsonObject();
        ResponseEntity<?> elem = unit.generate("eiffel3", "eiffelartifactnew", false, false, true, 1, false, json);
        assertEquals(elem.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void testEiffel3SuccessEventArray() throws Exception {
        File file = new File("src/test/resources/ErlookupConfidenceLevelArrayOutput.json");
        JsonParser parser = new JsonParser();
        JsonArray json = parser.parse(new FileReader(file)).getAsJsonArray();
        ResponseEntity<?> elem = unit.generate("eiffel3", "eiffelartifactnew", false, false, true, 1, false, json);
        assertEquals(elem.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void testEiffel3FailureEvent() throws Exception {
        File file = new File("src/test/resources/ErlookupConfidenceLevelOutput.json");
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(new FileReader(file)).getAsJsonObject();
        ResponseEntity<?> elem = unit.generate("eiffel3", "eiffelartifactnewevent", false, false, true, 1, false, json);
        assertEquals(elem.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testEiffel3FailureEventArray() throws Exception {
        File file = new File("src/test/resources/ErlookupConfidenceLevelArrayOutput.json");
        JsonParser parser = new JsonParser();
        JsonArray json = parser.parse(new FileReader(file)).getAsJsonArray();
        ResponseEntity<?> elem = unit.generate("eiffel3", "eiffelartifactnewevent", false, false, true, 1, false, json);
        assertEquals(elem.getStatusCode(), HttpStatus.BAD_REQUEST);
    }
    @Test
    public void testMessageServiceUnavailableEvent() throws Exception {
        File file = new File("src/test/resources/ArtifactCreated.json");
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(new FileReader(file)).getAsJsonObject();
        ResponseEntity<?> elem = unit.generate("other", "EiffelActivityFinishedEvent", false, false, true, 1, false, json);
        assertEquals(elem.getStatusCode(), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @Test
    public void testlenientValidation() throws Exception {
        File file = new File("src/test/resources/ArtifactCreated.json");
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(new FileReader(file)).getAsJsonObject();
        unit.setLenientValidationEnabledToUsers(true);
        ResponseEntity<?> elem = unit.generate("eiffelsemantics", "EiffelArtifactCreatedEvent", false, false, true, 1, true, json);
        assertEquals(elem.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void testlenientValidationEventArray() throws Exception {
        File file = new File("src/test/resources/ArtifactCreatedEventArray.json");
        JsonParser parser = new JsonParser();
        JsonArray json = parser.parse(new FileReader(file)).getAsJsonArray();
        unit.setLenientValidationEnabledToUsers(true);
        ResponseEntity<?> elem = unit.generate("eiffelsemantics", "EiffelArtifactCreatedEvent", false, false, true, 1, true, json);
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
