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

import com.ericsson.eiffel.remrem.OpenApiApplication;
import com.ericsson.eiffel.remrem.generate.config.ErLookUpConfig;
import com.ericsson.eiffel.remrem.generate.controller.RemremGenerateController;
import com.ericsson.eiffel.remrem.protocol.MsgService;
import com.ericsson.eiffel.remrem.semantics.EiffelEventType;
import com.ericsson.eiffel.remrem.semantics.SemanticsService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


// Simulate enabled ER for lookup.
@TestPropertySource(properties = {
        "event-repository.enabled: true",
        "event-repository.url: http://localhost:" + EiffelRemERLookupControllerUnitTestWithOpenAPI.ER_REST_API_PORT
})

// Start service
@SpringBootTest(/*classes = OpenApiApplication.class,*/ webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class EiffelRemERLookupControllerUnitTestWithOpenAPI {
    public static final String RESOURCES_DIR = "src/test/resources";
    public static final String JSON_SUFFIX = ".json";
    public static final String EIFFEL_SEMANTICS = "eiffelsemantics";

    // Ideally, a dynamic port should have been used, but I don't know how to do that
    // in this case.
    public static final int ER_REST_API_PORT = 8765;

    @Spy
    ErLookUpConfig erLookupConfig;

    @Autowired
    RemremGenerateController unit = new RemremGenerateController();

    MsgService service = new SemanticsService();
    @Mock
    MsgService service2;

    @Spy
    private List<MsgService> msgServices = new ArrayList<>();

    static private SpringApplication erService;
    static private int erServicePort = 0;

    public void initErService() throws Exception {
        if (erService != null)
            return;

        // Set to constant. Hopefully this will be randomly generated one day...
        erServicePort = ER_REST_API_PORT;

        // Create an OpenAPI having interface defined for ER.
        erService = new SpringApplicationBuilder(OpenApiApplication.class).build();

        Properties properties = new Properties();
        properties.put("server.port", erServicePort);
        ConfigurableEnvironment env = new StandardEnvironment();
        env.getPropertySources().addFirst(new PropertiesPropertySource("initProps", properties));

        erService.setEnvironment(env);
        erService.run();
    }

    @SuppressWarnings("resource")
    @Before
    public void setUp() throws Exception {
        initErService();

        MockitoAnnotations.openMocks(this);

        msgServices.add(service);
        msgServices.add(service2);
    }

    /**
     * Returns content of given event as an JsonObject.
     *
     * @param fileName Name of an event. Corresponding file named RESOURCE_DIR + "/" + fileName + JSON_SUFFIX
     *                  is expected to contain the event data.
     *
     * @return A JsonObject representing given event.
     *
     * @throws IOException
     */
    private JsonObject inputAsJsonObject(String fileName) throws IOException {
        File file = new File(RESOURCES_DIR + "/" + "event-repository/inputs/" + fileName + JSON_SUFFIX);
        JsonObject json = JsonParser.parseReader(new FileReader(file)).getAsJsonObject();

        return json;
    }

    @Test
    public void testErLookupSuccessWithMultipleIds() throws Exception {
        JsonObject json = inputAsJsonObject("success-lookup-confidence-level");
        ResponseEntity<?> response = unit.generate(EIFFEL_SEMANTICS,
            EiffelEventType.CONFIDENCELEVEL_MODIFIED.getEventName(),
            false, false, true, 1, false, json);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testErLookupMultipleFound() throws Exception {
        JsonObject json = inputAsJsonObject("fail-lookup-composition-defined");
        ResponseEntity<?> response = unit.generate(EIFFEL_SEMANTICS,
            EiffelEventType.COMPOSITION_DEFINED.getEventName(),
            true, false, true, 1, false, json);
        assertEquals(HttpStatus.EXPECTATION_FAILED, response.getStatusCode());
    }

    @Test
    public void testErLookupMultipleTraces() throws Exception {
        JsonObject json = inputAsJsonObject("fail-lookup-artifact-published");
        ResponseEntity<?> response = unit.generate(EIFFEL_SEMANTICS,
            EiffelEventType.ARTIFACT_PUBLISHED.getEventName(),
            false, true, true, 1, false, json);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testErLookupSuccessWithOneId() throws Exception {
        JsonObject json = inputAsJsonObject("success-lookup-with-one-id");
        ResponseEntity<?> result = unit.generate(EIFFEL_SEMANTICS,
                EiffelEventType.COMPOSITION_DEFINED.getEventName(), true, true,
                true, 1, false, json);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void testErLookupNoneFound() throws Exception {
        JsonObject json = inputAsJsonObject("fail-lookup-none-found");
        ResponseEntity<?> result = unit.generate(EIFFEL_SEMANTICS,
            EiffelEventType.COMPOSITION_DEFINED.getEventName(), true, true,
            true, 1, false, json);
        assertEquals(HttpStatus.NOT_ACCEPTABLE, result.getStatusCode());
    }

    @Test
    public void testErLookupMultipleTrace() throws Exception {
        JsonObject json = inputAsJsonObject("fail-lookup-multiple-trace");
        ResponseEntity<?> response = unit.generate(EIFFEL_SEMANTICS,
            EiffelEventType.SOURCECHANGE_SUBMITTED.getEventName(),
            false, true, true,
            1, false, json);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testErLookupOptions() throws Exception {
        JsonObject json = inputAsJsonObject("success-lookup-multiple-with-options");
        ResponseEntity<?> response = unit.generate(EIFFEL_SEMANTICS,
            EiffelEventType.ENVIRONMENT_DEFINED.getEventName(),
            false, false, true, 2, false, json);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
    
    @Test
    public void testErLookupOptionsWithEmptyResponse() throws Exception {
        JsonObject json = inputAsJsonObject("success-lookup-options-with-empty-response");
        ResponseEntity<?> response = unit.generate(EIFFEL_SEMANTICS,
                EiffelEventType.ENVIRONMENT_DEFINED.getEventName(), true, true, true, 2, false, json);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testErLookupOptionsWithMultipleFound() throws Exception {
        JsonObject json = inputAsJsonObject("fail-lookup-options-with-multiple-found");
        ResponseEntity<?> response = unit.generate(EIFFEL_SEMANTICS,
            EiffelEventType.ENVIRONMENT_DEFINED.getEventName(),
            false, false, true, 2, false, json);
        assertEquals(HttpStatus.EXPECTATION_FAILED, response.getStatusCode());
    }
    
    @Test
    public void testErLookupOptionsWithNoneFound() throws Exception {
        JsonObject json = inputAsJsonObject("fail-lookup-options-with-none-found");
        ResponseEntity<?> response = unit.generate(EIFFEL_SEMANTICS,
            EiffelEventType.ENVIRONMENT_DEFINED.getEventName(),
            false, false, true, 2, false, json);
        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
    }

    @Test
    public void testErLookupFailedWithOptions() throws Exception {
        JsonObject json = inputAsJsonObject("fail-lookup-with-options");
        ResponseEntity<?> response = unit.generate(EIFFEL_SEMANTICS,
            EiffelEventType.TESTCASE_STARTED.getEventName(),
            false, false, true, 2, false, json);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    }

    @Test
    public void testErLookUpLimitZero() throws Exception {
        JsonObject json = inputAsJsonObject("fail-lookup-with-options");
        ResponseEntity<?> response = unit.generate(EIFFEL_SEMANTICS,
            EiffelEventType.TESTCASE_STARTED.getEventName(),
            false, false, true, 0, false, json);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}