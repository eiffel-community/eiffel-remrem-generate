/*
    Copyright 2018-2026 Ericsson AB.
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

import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ericsson.eiffel.remrem.semantics.SemanticsService;
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
public class EiffelRemERLookupControllerUnitWithMockTest {
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

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        unit.setRestTemplate(restTemplate);
        msgServices.add(service);
        msgServices.add(service2);
        Mockito.when(unit.isLenientEnabled(false)).thenReturn(false);
        Mockito.when(service.getServiceName()).thenReturn("eiffelsemantics");
        Mockito.when(service2.getServiceName()).thenReturn("eiffel3");
        Mockito.when(erLookupConfig.getEventRepositoryEnabled()).thenReturn(true);
        Mockito.when(erLookupConfig.getErURL()).thenReturn("");

        // Setting up mock responses per event type and test case scenario
        setupConfidenceLevelScenario();
        setupArtifactPublishedScenario();
        setupCompositionDefinedScenario();
        setupCompositionWithSCCreatedScenario();
        setupCompositionWithSCSubmittedScenario();
        setupSCSubmittedScenario();
        setupEnvironmentDefinedScenario();
        setupEnvironmentDefinedWithEmptyResponseScenario();
        setupOptionsWithNoneFoundScenario();
        setupFailedWithOptionsScenario();
    }

    private String readTestResource(String filename) throws Exception {
        return FileUtils.readFileToString(new File(TEST_RESOURCES_PATH + filename), ENCODING);
    }

    private void setupConfidenceLevelScenario() throws Exception {
        String generateOutput = readTestResource("ErlookupConfidenceLevelOutput.json");
        String erResponse = readTestResource("ErlookupConfidenceLevelResponse.json");

        Mockito.when(service.generateMsg(Mockito.eq("eiffelconfidencelevel"), Mockito.any(), (HashMap<String, Object>) Mockito.any()))
                .thenReturn(generateOutput);

        Mockito.when(restTemplate.getForEntity(
                Mockito.contains("/events?meta.type=EiffelArtifactCreatedEvent&data.identity=swdi.up/CXP102051_22@R21EK"),
                Mockito.eq(String.class)))
                .thenReturn(new ResponseEntity<>(erResponse, HttpStatus.OK));
    }

    private void setupArtifactPublishedScenario() throws Exception {
        String generateOutput = readTestResource("ErlookupArtifactPublishedOutput.json");

        Mockito.when(service.generateMsg(Mockito.eq("eiffelartifactpublished"), Mockito.any(), (HashMap<String, Object>) Mockito.any()))
                .thenReturn(generateOutput);
    }

    private void setupCompositionDefinedScenario() throws Exception {
        String generateOutput = readTestResource("ErlookupCompositionDefinedOutput.json");
        Mockito.when(service.generateMsg(Mockito.eq("eiffelcompositiondefined"), Mockito.any(), Mockito.anyBoolean()))
                .thenReturn(generateOutput);
        Mockito.when(service.generateMsg(Mockito.eq("eiffelCompositionDefinedEvent"), Mockito.any(), Mockito.anyBoolean()))
                .thenReturn(generateOutput);
    }

    private void setupCompositionWithSCCreatedScenario() throws Exception {
        String generateOutput = readTestResource("CompositionDefinedwith_SCCreatedOutput.json");
        String erResponse = readTestResource("CompositionDefinedwith_SCCreatedResponse.json");
        Mockito.when(service.generateMsg(Mockito.eq("eiffelCompositionDefinedEventWithSCC"), Mockito.any(), (HashMap<String, Object>) Mockito.any()))
                .thenReturn(generateOutput);
        Mockito.when(restTemplate.getForEntity(
                Mockito.contains("/events?meta.type=EiffelSourceChangeCreatedEvent&data.gitIdentifier.commitId=fd090b60a4aedc5161da9c035a49b14a319829b4&data.gitIdentifier.repoUri=https://github.com/johndoe/myPrivateRepo.git"),
                Mockito.eq(String.class)))
                .thenReturn(new ResponseEntity<>(erResponse, HttpStatus.OK));
    }

    private void setupCompositionWithSCSubmittedScenario() throws Exception {
        String generateOutput = readTestResource("CompositionDefinedwith_SCSubmittedOutput.json");
        String erResponse = readTestResource("CompositionDefinedwith_SCSubmittedResponse.json");

        Mockito.when(service.generateMsg(Mockito.eq("eiffelCompositionDefinedEvent"), Mockito.any(), Mockito.anyBoolean()))
                .thenReturn(generateOutput);
        Mockito.when(restTemplate.getForEntity(
                Mockito.contains("/events?meta.type=EiffelSourceChangeSubmittedEvent&data.gitIdentifier.commitId=fd090b60aedc535a49b14a&data.gitIdentifier.repoUri=https://github.com/johndoe/myPrivateRepo.git"),
                Mockito.eq(String.class)))
                .thenReturn(new ResponseEntity<>(erResponse, HttpStatus.OK));
    }

    private void setupSCSubmittedScenario() throws Exception {
        String generateOutput = readTestResource("ErlookupSCSubmittedOutput.json");
        String erResponse = readTestResource("ErlookupSCSubmittedResponse.json");

        Mockito.when(service.generateMsg(Mockito.eq("eiffelSCSubmitted"), Mockito.any(), (HashMap<String, Object>) Mockito.any()))
                .thenReturn(generateOutput);
        Mockito.when(restTemplate.getForEntity(
                Mockito.contains("/events?meta.type=EiffelSourceChangeCreatedEvent&data.gitIdentifier.commitId=fd090b60a4aedc5161da9c035a49b14a319829b4&data.gitIdentifier.repoUri=https://github.com/myPrivateRepo.git"),
                Mockito.eq(String.class)))
                .thenReturn(new ResponseEntity<>(erResponse, HttpStatus.OK));
    }

    private void setupEnvironmentDefinedScenario() throws Exception {
        String generateOutput = readTestResource("ErlookupWithOptionsOutput.json");
        String erResponse = readTestResource("ErlookupWithOptionsResponse.json");
        String erResponse2 = readTestResource("ErlookupWithOptionsResponse2.json");

        Mockito.when(service.generateMsg(Mockito.eq("eiffelEnvironmentDefined"), Mockito.any(), (HashMap<String, Object>) Mockito.any()))
                .thenReturn(generateOutput);
        Mockito.when(restTemplate.getForEntity(
                Mockito.contains("/events?meta.type=EiffelArtifactCreatedEvent&data.identity=pkg:maven/swdi.up/CXP102051_22@R21EK?type=xml&classifier=test"),
                Mockito.eq(String.class)))
                .thenReturn(new ResponseEntity<>(erResponse, HttpStatus.OK));
        Mockito.when(restTemplate.getForEntity(
                Mockito.contains("/events?meta.type=EiffelSourceChangeSubmittedEvent&data.gitIdentifier.commitId=ad090b60a4aedc5161da9c035a49b14a319829b4&data.gitIdentifier.repoUri=https://github.com/johndoe/myPrivateRepo.git"),
                Mockito.eq(String.class)))
                .thenReturn(new ResponseEntity<>(erResponse2, HttpStatus.OK));
    }

    private void setupEnvironmentDefinedWithEmptyResponseScenario() throws Exception {
        String generateOutput = readTestResource("ErlookupOptionsWithEmptyResponseOutput.json");
        String emptyErResponse = readTestResource("EmptyResponse.json");

        Mockito.when(service.generateMsg(Mockito.eq("eiffelEnvironmentDefinedEvent"), Mockito.any(), (HashMap<String, Object>) Mockito.any()))
                .thenReturn(generateOutput);
        Mockito.when(restTemplate.getForEntity(
                Mockito.contains("/events?meta.type=EiffelSourceChangeSubmittedEvent&data.gitIdentifier.commitId=ad090b60a4aedc5161da9c035a49b14a319829e1&data.gitIdentifier.repoUri=https://github.com/myPrivateRepo.git"),
                Mockito.eq(String.class)))
                .thenReturn(new ResponseEntity<>(emptyErResponse, HttpStatus.OK));
        Mockito.when(restTemplate.getForEntity(
                Mockito.contains("/events?meta.type=EiffelArtifactCreatedEvent&data.identity=pkg:maven/swdi.up/CXP102051_22@R21EK?classifier=test"),
                Mockito.eq(String.class)))
                .thenReturn(new ResponseEntity<>(emptyErResponse, HttpStatus.OK));
    }

    private void setupOptionsWithNoneFoundScenario() throws Exception {
        String generateOutput = readTestResource("ErlookupOptionsWithNoneFoundOutput.json");
        Mockito.when(service.generateMsg(Mockito.eq("eiffelConfidenceLevelModified"), Mockito.any(), Mockito.anyBoolean()))
                .thenReturn(generateOutput);
    }

    private void setupFailedWithOptionsScenario() throws Exception {
        String generateOutput = readTestResource("ErlookupFailedWithOptionsOutput.json");
        Mockito.when(service.generateMsg(Mockito.eq("eiffelTestCaseStarted"), Mockito.any(), Mockito.anyBoolean()))
                .thenReturn(generateOutput);
    }

    private HashMap<String, Object> newHashMap(String key, Object value) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put(key, value);
        return properties;
    }

    @Test
    public void testErLookupSuccessWithMultipleIds() throws Exception {
        File file = new File("src/test/resources/ErlookupConfidenceLevelInput.json");
        JsonObject json = JsonParser.parseReader(new FileReader(file, StandardCharsets.UTF_8)).getAsJsonObject();

        String msgProtocol = "eiffelsemantics";
        String msgType = "eiffelconfidencelevel";
        boolean okToLeaveOutInvalidOptionalFields = false;
        boolean markAsNonStandard = false;
        boolean lookupInExternalERs = true;
        int lookupLimit = 1;

        ResponseEntity<?> elem = unit.generate(msgProtocol, msgType, okToLeaveOutInvalidOptionalFields,
                markAsNonStandard, lookupInExternalERs, lookupLimit, newHashMap(SemanticsService.LENIENT_VALIDATION, false), json);
        assertEquals(HttpStatus.OK, elem.getStatusCode());
    }

    @Test
    public void testErLookupMultipleFound() throws Exception {
        File file = new File("src/test/resources/ErlookupCompositionDefinedInput.json");
        JsonObject json = JsonParser.parseReader(new FileReader(file, StandardCharsets.UTF_8)).getAsJsonObject();

        ResponseEntity<?> elem = unit.generate("eiffelsemantics", "eiffelcompositiondefined", true, false, true, 1, newHashMap(SemanticsService.LENIENT_VALIDATION, false), json);
        String response = """
        {
            "status code": 417,
             "message": {
                "status_code": 417,
                "result": "FAIL",
                "message": "Multiple event IDs found with ERLookup properties"
             },
             "result": "FAIL"
        }
        """;

        JsonElement expectedBody = JsonParser.parseString(response);
        assertEquals(expectedBody, elem.getBody());
        assertEquals(HttpStatus.EXPECTATION_FAILED, elem.getStatusCode());
    }

    @Test
    public void testErLookupMultipleTraces() throws Exception {
        File file = new File("src/test/resources/ErlookupArtifactPublishedInput.json");
        JsonObject json = JsonParser.parseReader(new FileReader(file, StandardCharsets.UTF_8)).getAsJsonObject();

        ResponseEntity<?> elem = unit.generate("eiffelsemantics", "eiffelartifactpublished", false, true, true, 1, newHashMap(SemanticsService.LENIENT_VALIDATION, false), json);
        String response = """
        {
            "status code": 400,
             "message": {
                "message": "Cannot validate given JSON string",
                "cause": "com.ericsson.eiffel.remrem.semantics.validator.EiffelValidationException: Multiple trace links are not allowed for link type ARTIFACT"
             },
             "result": "FAIL"
        }
        """;

        JsonElement expectedBody = JsonParser.parseString(response);
        assertEquals(expectedBody, elem.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, elem.getStatusCode());
    }

    @Test
    public void testErLookupSuccessWithOneId() throws Exception {
        File file = new File("src/test/resources/CompositionDefinedwith_SCCreatedInput.json");
        JsonObject json = JsonParser.parseReader(new FileReader(file, StandardCharsets.UTF_8)).getAsJsonObject();

        ResponseEntity<?> elem = unit.generate("eiffelsemantics", "eiffelCompositionDefinedEventWithSCC", true, true, true, 1, newHashMap(SemanticsService.LENIENT_VALIDATION, false), json);
        assertEquals(HttpStatus.OK, elem.getStatusCode());
    }

    @Test
    public void testErLookupNoneFound() throws Exception {
        File file = new File("src/test/resources/CompositionDefinedwith_SCSubmittedInput.json");
        JsonObject json = JsonParser.parseReader(new FileReader(file, StandardCharsets.UTF_8)).getAsJsonObject();

        ResponseEntity<?> elem = unit.generate("eiffelsemantics", "eiffelCompositionDefinedEvent", true, true, true, 1, newHashMap(SemanticsService.LENIENT_VALIDATION, false), json);
        String response = """
        {
            "status code": 406,
             "message": {
                "status_code": 406,
                "result": "FAIL",
                "message": "No event ID found with ERLookup properties"
             },
             "result": "FAIL"
        }
        """;

        JsonElement expectedBody = JsonParser.parseString(response);
        assertEquals(expectedBody, elem.getBody());
        assertEquals(HttpStatus.NOT_ACCEPTABLE, elem.getStatusCode());
    }

    @Test
    public void testErLookupMultipleTrace() throws Exception {
        File file = new File("src/test/resources/ErlookupSCSubmittedInput.json");
        JsonObject json = JsonParser.parseReader(new FileReader(file, StandardCharsets.UTF_8)).getAsJsonObject();

        ResponseEntity<?> elem = unit.generate("eiffelsemantics", "eiffelSCSubmitted", false, true, true, 1, newHashMap(SemanticsService.LENIENT_VALIDATION, false), json);
        String response = """
        {
            "status code": 400,
             "message": {
                "message": "Cannot validate given JSON string",
                "cause": "com.ericsson.eiffel.remrem.semantics.validator.EiffelValidationException: Multiple trace links are not allowed for link type CHANGE"
             },
             "result": "FAIL"
        }
        """;

        JsonElement expectedBody = JsonParser.parseString(response);
        assertEquals(expectedBody, elem.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, elem.getStatusCode());
    }

    @Test
    public void testErlookupOptions() throws Exception {
        File file = new File("src/test/resources/ErlookupWithOptionsInput.json");
        JsonObject json = JsonParser.parseReader(new FileReader(file, StandardCharsets.UTF_8)).getAsJsonObject();

        ResponseEntity<?> elem = unit.generate("eiffelsemantics", "eiffelEnvironmentDefined", false, false, true, 2, newHashMap(SemanticsService.LENIENT_VALIDATION, false), json);
        assertEquals(HttpStatus.OK, elem.getStatusCode());
    }

    @Test
    public void testErlookupOptionsWithEmptyResponse() throws Exception {
        File file = new File("src/test/resources/ErlookupOptionsWithEmptyResponseInput.json");
        JsonObject json = JsonParser.parseReader(new FileReader(file, StandardCharsets.UTF_8)).getAsJsonObject();

        ResponseEntity<?> elem = unit.generate("eiffelsemantics", "eiffelEnvironmentDefinedEvent", true, true, true, 2, newHashMap(SemanticsService.LENIENT_VALIDATION, false), json);
        assertEquals(HttpStatus.OK, elem.getStatusCode());
    }

    @Test
    public void testErlookupOptionsWithMultipleFound() throws Exception {
        File file = new File("src/test/resources/ErlookupOptionsWithMultipleFoundInput.json");
        JsonObject json = JsonParser.parseReader(new FileReader(file, StandardCharsets.UTF_8)).getAsJsonObject();

        ResponseEntity<?> elem = unit.generate("eiffelsemantics", "eiffelCompositionDefinedEvent", false, false, true, 2, newHashMap(SemanticsService.LENIENT_VALIDATION, false), json);
        String response = """
        {
            "status code": 417,
             "message": {
                "status_code": 417,
                "result": "FAIL",
                "message": "Multiple event IDs found with ERLookup properties"
             },
             "result": "FAIL"
        }
        """;

        JsonElement expectedBody = JsonParser.parseString(response);
        assertEquals(expectedBody, elem.getBody());
        assertEquals( HttpStatus.EXPECTATION_FAILED, elem.getStatusCode());
    }

    @Test
    public void testErlookupOptionsWithNoneFound() throws Exception {
        File file = new File("src/test/resources/ErlookupOptionsWithNoneFoundInput.json");
        JsonObject json = JsonParser.parseReader(new FileReader(file, StandardCharsets.UTF_8)).getAsJsonObject();

        ResponseEntity<?> elem = unit.generate("eiffelsemantics", "eiffelConfidenceLevelModified", false, false, true, 2, newHashMap(SemanticsService.LENIENT_VALIDATION, false), json);
        String response = """
        {
            "status code": 406,
             "message": {
                "status_code": 406,
                "result": "FAIL",
                "message": "No event ID found with ERLookup properties"
             },
             "result": "FAIL"
        }
        """;

        JsonElement expectedBody = JsonParser.parseString(response);
        assertEquals(expectedBody, elem.getBody());
        assertEquals(HttpStatus.NOT_ACCEPTABLE, elem.getStatusCode());
    }

    @Test
    public void testErlookupFailedWithOptions() throws Exception {
        File file = new File("src/test/resources/ErlookupFailedWithOptionsInput.json");
        JsonObject json = JsonParser.parseReader(new FileReader(file, StandardCharsets.UTF_8)).getAsJsonObject();

        ResponseEntity<?> elem = unit.generate("eiffelsemantics", "eiffelTestCaseStarted", false, false, true, 2, newHashMap(SemanticsService.LENIENT_VALIDATION, false), json);
        String response = """
        {
            "status code": 422,
             "message": {
                "status_code": 422,
                "result": "FAIL",
                "message": "Link specific lookup options could not be fulfilled"
             },
             "result": "FAIL"
        }
        """;

        JsonElement expectedBody = JsonParser.parseString(response);
        assertEquals(expectedBody, elem.getBody());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, elem.getStatusCode());
    }

    @Test
    public void testErLookUpLimitZero() throws Exception {
        File file = new File("src/test/resources/ErlookupFailedWithOptionsInput.json");
        JsonObject json = JsonParser.parseReader(new FileReader(file, StandardCharsets.UTF_8)).getAsJsonObject();

        ResponseEntity<?> elem = unit.generate("eiffelsemantics", "eiffelTestCaseStarted", false, false, true, 0, newHashMap(SemanticsService.LENIENT_VALIDATION, false), json);
        String response = """
        {
            "status code": 400,
             "message": "Parameter 'lookupLimit' must be > 0",
             "result": "FAIL"
        }
        """;

        JsonElement expectedBody = JsonParser.parseString(response);
        assertEquals(expectedBody, elem.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, elem.getStatusCode());
    }
}
