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
package com.ericsson.eiffel.remrem.generate.integrationtest;

import com.google.gson.JsonParser;

import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@ActiveProfiles("integration-test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class EiffelRemremControllerIT {
    JsonParser parser = new JsonParser();

    @Value("${local.server.port}")
    private int port;
    private String artifactCreatedFileName = "ArtifactCreated.json";
    private String artifactCreatedBody;

    private String activityFinishedFileName = "ActivityFinished.json";
    private String activityFinishedBody;

    private String activityFinishedDuplicateKeysFileName = "ActivityFinishedEventDuplicatedKeys.json";
    private String activityFinishedDuplicateKeysBody;

    private String version = "3.0.0";
    @Value("${event-repository.enabled}")
    private String eventRepositoryEnabled;
    @Value("${event-repository.url}")
    private String erURL;

    private boolean eventRepositoryCheck;
    private String credentials = "Basic " + Base64.getEncoder().encodeToString("user:secret".getBytes());

    @Before
    public void  setUp() throws IOException {
        RestAssured.port = port;
        activityFinishedBody = loadEventBody(activityFinishedFileName);
        artifactCreatedBody =  loadEventBody(artifactCreatedFileName);

        if (version == null) {
            version = getMessagingVersion();
        }
    }

    private String loadEventBody(final String fileName) throws IOException {
        URL url = getClass().getClassLoader().getResource(fileName);
        assert url != null;
        String path = url.getPath().replace("%20"," ");
        File file = new File(path);
        final byte[] bytes = Files.readAllBytes(file.toPath());
        return new String(bytes);
    }

    public static String getMessagingVersion() {
        Enumeration resEnum;
        try {
            resEnum = Thread.currentThread().getContextClassLoader().getResources(JarFile.MANIFEST_NAME);
            while (resEnum.hasMoreElements()) {
                try {
                    URL url = (URL)resEnum.nextElement();
                    if(url.getPath().contains("eiffel-remrem-semantics")) {
                        InputStream is = url.openStream();
                        if (is != null) {
                            Manifest manifest = new Manifest(is);
                            Attributes mainAttribs = manifest.getMainAttributes();
                            String version = mainAttribs.getValue("semanticsVersion");
                            if(version != null) {
                                return version;
                            }
                        }
                    }
                }
                catch (Exception e) {
                    // Silently ignore wrong manifests on classpath?
                }
            }
        } catch (IOException e1) {
            // Silently ignore wrong manifests on classpath?
        }
        return null; 
    }

    @Test
    public void testUnauthenticatedNotAllowed() throws Exception {
        given()
                .contentType("application/json")
                .body(artifactCreatedBody)
                .when()
                    .post("/eiffelsemantics?msgType=eiffelartifactpublished")
                .then()
                    .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    public void testSendArtifactCreated() throws Exception {
        given()
                .header("Authorization", credentials)
                .contentType("application/json")
                .body(artifactCreatedBody)
                .when()
                    .post("/eiffelsemantics?msgType=EiffelArtifactCreatedEvent")
                .then()
                    .statusCode(HttpStatus.SC_OK)
                    .body("meta.type", Matchers.is("EiffelArtifactCreatedEvent"))
                    .body("meta.version", Matchers.is(version));
    }

    @Test
    public void testSendActivityFinished() throws Exception {
        given()
                .header("Authorization", credentials)
                .contentType("application/json")
                .body(activityFinishedBody)
                .when()
                    .post("/eiffelsemantics?msgType=EiffelActivityFinishedEvent")
                .then()
                    .statusCode(HttpStatus.SC_OK)
                    .body("meta.type", Matchers.is("EiffelActivityFinishedEvent"))
                    .body("meta.version", Matchers.is(version));
    }

    @Test
    public void testDuplicateKeyInBody() throws IOException {
        activityFinishedDuplicateKeysBody = loadEventBody(activityFinishedDuplicateKeysFileName);

        given()
                .header("Authorization", credentials)
                .contentType("application/json")
                .body(activityFinishedDuplicateKeysBody)
                .when()
                    .post("/eiffelsemantics?msgType=EiffelActivityFinishedEvent")
                .then()
                    .statusCode(HttpStatus.SC_BAD_REQUEST);
    }
    
    @Test
    public void testGetEventTypes() throws Exception {
        given()
                .header("Authorization", credentials)
                .when()
                    .get("/event_types/eiffelsemantics")
                .then()
                    .statusCode(HttpStatus.SC_OK)
                    .body(Matchers.containsString("EiffelArtifactPublishedEvent"))
                    .body(Matchers.containsString("EiffelActivityFinishedEvent"))
                    .body(Matchers.containsString("EiffelActivityStartedEvent"));
    }
    
    @Test
    public void testGetEventTypeTemplateFileExists() {
        given()
                .header("Authorization", credentials)
                .when()
                    .get("/template/EiffelArtifactPublishedEvent/eiffelsemantics")
                .then()
                    .statusCode(HttpStatus.SC_OK);
    }
    
    @Test
    public void testGetEventTypeTemplateNoFileExists() {
        given()
                .header("Authorization", credentials)
                .when()
                    .get("/template/EiffelNotAnEvent/eiffelsemantics")
                .then()
                    .statusCode(HttpStatus.SC_NOT_FOUND);
    }
    @Test
    public void testErLookUpConfigurations(){
        if(eventRepositoryCheck){
            if(!erURL.isEmpty())
                assertTrue(erURL,true);
            else
                assertNull(erURL,null);
            }
        else{
            assertFalse(eventRepositoryCheck);
        }
    }
}