/*
    Copyright 2018 Ericsson AB.
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
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.ericsson.eiffel.remrem.generate.config.ErLookUpConfig;
import com.ericsson.eiffel.remrem.generate.controller.RemremGenerateController;
import com.ericsson.eiffel.remrem.protocol.MsgService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RunWith(SpringRunner.class)
public class EiffelRemERLookupControllerUnitTest {
    @Mock
    RestTemplate restTemplate;

    @Spy
    ErLookUpConfig erLookupConfig;

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

    private static final String TEST_RESOURCES_PATH = "src/test/resources/";
    private static final String ENCODING = "UTF-8";

    @SuppressWarnings("resource")
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        unit.setRestTemplate(restTemplate);

        msgServices.add(service);
        msgServices.add(service2);
        Mockito.when(service.getServiceName()).thenReturn("eiffelsemantics");
        Mockito.when(service2.getServiceName()).thenReturn("eiffel3");

        Mockito.when(erLookupConfig.getEventRepositoryEnabled()).thenReturn(true);
        Mockito.when(erLookupConfig.getErURL()).thenReturn("");

        String confidenceLevelOutput = FileUtils
                .readFileToString(new File(TEST_RESOURCES_PATH+"ErlookupConfidenceLevelOutput.json"), ENCODING);

        String artifactPublishedOutput = FileUtils
                .readFileToString(new File(TEST_RESOURCES_PATH+"ErlookupArtifactPublishedOutput.json"), ENCODING);

        String response = FileUtils
                .readFileToString(new File(TEST_RESOURCES_PATH+"ErlookupConfidenceLevelResponse.json"), ENCODING);

        String compositionResponse = FileUtils.readFileToString(
                new File(TEST_RESOURCES_PATH+"CompositionDefinedwith_SCCreatedResponse.json"), ENCODING);

        String compositionDefinedInput = FileUtils
                .readFileToString(new File(TEST_RESOURCES_PATH+"ErlookupCompositionDefinedInput.json"), ENCODING);

        String compositionDefinedOutput = FileUtils
                .readFileToString(new File(TEST_RESOURCES_PATH+"ErlookupCompositionDefinedOutput.json"), ENCODING);

        String compositionDefinedSCCreatedOutput = FileUtils
                .readFileToString(new File(TEST_RESOURCES_PATH+"CompositionDefinedwith_SCCreatedOutput.json"), ENCODING);

        String compositionDefinedSCSubmittedOutput = FileUtils.readFileToString(
                new File(TEST_RESOURCES_PATH+"CompositionDefinedwith_SCSubmittedOutput.json"), ENCODING);

        String compositionSCSubmittedResponse = FileUtils.readFileToString(
                new File(TEST_RESOURCES_PATH+"CompositionDefinedwith_SCSubmittedResponse.json"), ENCODING);

        String SCSubmittedOutput = FileUtils
                .readFileToString(new File(TEST_RESOURCES_PATH+"ErlookupSCSubmittedOutput.json"), ENCODING);

        String SCSubmittedResponse = FileUtils
                .readFileToString(new File(TEST_RESOURCES_PATH+"ErlookupSCSubmittedResponse.json"), ENCODING);

        String erLookupWithOptionsOutput = FileUtils
                .readFileToString(new File(TEST_RESOURCES_PATH+"ErlookupWithOptionsOutput.json"), ENCODING);

        String erLookupWithOptionsResponse = FileUtils
                .readFileToString(new File(TEST_RESOURCES_PATH+"ErlookupWithOptionsResponse.json"), ENCODING);

        String ErlookupFailedWithOptionsOutput = FileUtils
                .readFileToString(new File(TEST_RESOURCES_PATH+"ErlookupFailedWithOptionsOutput.json"), ENCODING);

        Mockito.when(service.generateMsg(Mockito.eq("eiffelconfidencelevel"), Mockito.anyObject()))
                .thenReturn(confidenceLevelOutput);

        Mockito.when(service.generateMsg(Mockito.eq("eiffelartifactpublished"), Mockito.anyObject()))
                .thenReturn(artifactPublishedOutput);

        Mockito.when(service.generateMsg(Mockito.eq("eiffelcompositiondefined"), Mockito.anyObject()))
                .thenReturn(compositionDefinedOutput);

        Mockito.when(service.generateMsg(Mockito.eq("eiffelCompositionDefined"), Mockito.anyObject()))
                .thenReturn(compositionDefinedSCCreatedOutput);

        Mockito.when(service.generateMsg(Mockito.eq("eiffelCompositionDefinedEvent"), Mockito.anyObject()))
                .thenReturn(compositionDefinedSCSubmittedOutput);

        Mockito.when(service.generateMsg(Mockito.eq("eiffelSCSubmitted"), Mockito.anyObject()))
                .thenReturn(SCSubmittedOutput);

        Mockito.when(service.generateMsg(Mockito.eq("eiffelEnvironmentDefined"), Mockito.anyObject()))
        .thenReturn(erLookupWithOptionsOutput);

        Mockito.when(service.generateMsg(Mockito.eq("eiffelCompositionDefinedEventt"), Mockito.anyObject()))
        .thenReturn(compositionDefinedOutput);

        Mockito.when(service.generateMsg(Mockito.eq("eiffelTestCaseStarted"), Mockito.anyObject()))
        .thenReturn(ErlookupFailedWithOptionsOutput);

        ResponseEntity erresponse = new ResponseEntity(response, HttpStatus.OK);
        when(restTemplate.getForEntity(
                Mockito.contains("/events?meta.type=EiffelArtifactCreatedEvent&data.identity=swdi.up/CXP102051_22@R21EK"),
                Mockito.eq(String.class))).thenReturn(erresponse);

        ResponseEntity compositionErResponse = new ResponseEntity(compositionResponse, HttpStatus.OK);
        Mockito.when(restTemplate.getForEntity(
                Mockito.contains("/events?meta.type=EiffelSourceChangeCreatedEvent&data.gitIdentifier.commitId=fd090b60a4aedc5161da9c035a49b14a319829b4&data.gitIdentifier.repoUri=https://github.com/johndoe/myPrivateRepo.git"),
                Mockito.eq(String.class))).thenReturn(compositionErResponse);

        ResponseEntity compositionEr1Response = new ResponseEntity(compositionSCSubmittedResponse, HttpStatus.OK);
        Mockito.when(restTemplate.getForEntity(
                Mockito.contains("/events?meta.type=EiffelSourceChangeSubmittedEvent&data.gitIdentifier.commitId=fd090b60aedc535a49b14a&data.gitIdentifier.repoUri=https://github.com/johndoe/myPrivateRepo.git"),
                Mockito.eq(String.class))).thenReturn(compositionEr1Response);

        ResponseEntity SCSubmittedErResponse = new ResponseEntity(SCSubmittedResponse, HttpStatus.OK);
        Mockito.when(restTemplate.getForEntity(
                Mockito.contains("/events?meta.type=EiffelSourceChangeCreatedEvent&data.gitIdentifier.commitId=fd090b60a4aedc5161da9c035a49b14a319829b4&data.gitIdentifier.repoUri=https://github.com/myPrivateRepo.git"),
                Mockito.eq(String.class))).thenReturn(SCSubmittedErResponse);

        ResponseEntity erLookupWithOptionsErResponse = new ResponseEntity(erLookupWithOptionsResponse, HttpStatus.OK);
        Mockito.when(restTemplate.getForEntity(
                Mockito.contains("/events?meta.type=EiffelArtifactCreatedEvent&data.identity=pkg:maven/swdi.up/CXP102051_22@R21EK?type=xml&classifier=test"),
                Mockito.eq(String.class))).thenReturn(erLookupWithOptionsErResponse);
    }

    @Test
    public void testErlookupSuccesswithMultipleIds() throws Exception {

        File file = new File("src/test/resources/ErlookupConfidenceLevelInput.json");
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(new FileReader(file)).getAsJsonObject();

        ResponseEntity<?> elem = unit.generate("eiffelsemantics", "eiffelconfidencelevel", false, false, true, 1, json);
        assertEquals(elem.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void testErlookupMultipleFound() throws Exception {

        File file = new File("src/test/resources/ErlookupCompositionDefinedInput.json");
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(new FileReader(file)).getAsJsonObject();

        ResponseEntity<?> elem = unit.generate("eiffelsemantics", "eiffelcompositiondefined", true, false, true, 1, json);
        assertEquals(elem.getStatusCode(), HttpStatus.EXPECTATION_FAILED);
    }

    @Test
    public void testErlookupMultipleTraces() throws Exception {
        File file = new File("src/test/resources/ErlookupArtifactPublishedInput.json");
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(new FileReader(file)).getAsJsonObject();

        ResponseEntity<?> elem = unit.generate("eiffelsemantics", "eiffelartifactpublished", false, true, true, 1, json);
        assertEquals(elem.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testErlookupSuccesswithOneId() throws Exception {
        File file = new File("src/test/resources/CompositionDefinedwith_SCCreatedInput.json");
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(new FileReader(file)).getAsJsonObject();

        ResponseEntity<?> elem = unit.generate("eiffelsemantics", "eiffelCompositionDefined", true, true, true, 1, json);
        assertEquals(elem.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void testErlookupNoneFound() throws Exception {
        File file = new File("src/test/resources/CompositionDefinedwith_SCSubmittedInput.json");
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(new FileReader(file)).getAsJsonObject();

        ResponseEntity<?> elem = unit.generate("eiffelsemantics", "eiffelCompositionDefinedEvent", true, true, true, 1, json);
        assertEquals(elem.getStatusCode(), HttpStatus.NOT_ACCEPTABLE);
    }

    @Test
    public void testErlookupMultipleTrace() throws Exception {
        File file = new File("src/test/resources/ErlookupSCSubmittedInput.json");
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(new FileReader(file)).getAsJsonObject();

        ResponseEntity<?> elem = unit.generate("eiffelsemantics", "eiffelSCSubmitted", false, true, true, 1, json);
        assertEquals(elem.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testErlookupOptions() throws Exception {
        File file = new File("src/test/resources/ErlookupWithOptionsInput.json");
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(new FileReader(file)).getAsJsonObject();

        ResponseEntity<?> elem = unit.generate("eiffelsemantics", "eiffelEnvironmentDefined", false, false, true, 2, json);
        assertEquals(elem.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void testErlookupOptionsWithMultipleFound() throws Exception {
        File file = new File("src/test/resources/ErlookupOptionsWithMultipleFoundInput.json");
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(new FileReader(file)).getAsJsonObject();

        ResponseEntity<?> elem = unit.generate("eiffelsemantics", "eiffelCompositionDefinedEventt", false, false, true, 2, json);
        assertEquals(elem.getStatusCode(), HttpStatus.EXPECTATION_FAILED);
    }

    @Test
    public void testErlookupFailedWithOptions() throws Exception {
        File file = new File("src/test/resources/ErlookupFailedWithOptionsInput.json");
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(new FileReader(file)).getAsJsonObject();

        ResponseEntity<?> elem = unit.generate("eiffelsemantics", "eiffelTestCaseStarted", false, false, true, 2, json);
        assertEquals(elem.getStatusCode(), HttpStatus.UNPROCESSABLE_ENTITY);
    }
}