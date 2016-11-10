package com.ericsson.eiffel.remrem.generate;

import com.google.gson.JsonParser;
import com.jayway.restassured.RestAssured;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import static com.jayway.restassured.RestAssured.given;

@ActiveProfiles("integration-test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class EiffelSemanticsController {
    JsonParser parser = new JsonParser();

    @Value("${local.server.port}")
    int port;
    static String artifactPublishedFileName = "ArtifactPublished.json";
    static String artifactPublishedBody;

    static String activityFinishedFileName = "ActivityFinished.json";
    static String activityFinishedBody;
    
    static String version = null;

    private String credentials = "Basic " + Base64.getEncoder().encodeToString("user:secret".getBytes());

    @Before
    public void  setUp() throws FileNotFoundException {
        RestAssured.port = port;

        File file = new File(getClass().getClassLoader().getResource(artifactPublishedFileName).getFile());
        artifactPublishedBody = new Scanner(file)
            .useDelimiter("\\A").next();

        file = new File(getClass().getClassLoader().getResource(activityFinishedFileName).getFile());
        activityFinishedBody = new Scanner(file)
            .useDelimiter("\\A").next();
        
        if (version == null) {
            version = getMessagingVersion();
        }
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
                            String version = mainAttribs.getValue("Semantics-Version");
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
                .body(artifactPublishedBody)
                .when()
                    .post("/eiffelsemantics?msgType=eiffelartifactpublished")
                .then()
                    .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test public void testSendArtifactPublished() throws Exception {
        given()
                .header("Authorization", credentials)
                .contentType("application/json")
                .body(artifactPublishedBody)
                .when()
                    .post("/eiffelsemantics?msgType=eiffelartifactpublished")
                .then()
                    .statusCode(HttpStatus.SC_OK)
                    .body("meta.type", Matchers.is("eiffelartifactpublished"))
                    .body("meta.version", Matchers.is(version));
    }

    @Test public void testSendActivityFinished() throws Exception {
        given()
                .header("Authorization", credentials)
                .contentType("application/json")
                .body(activityFinishedBody)
                .when()
                    .post("/eiffelsemantics?msgType=eiffelactivityfinished")
                .then()
                    .statusCode(HttpStatus.SC_OK)
                    .body("meta.type", Matchers.is("eiffelactivityfinished"))
                    .body("meta.version", Matchers.is(version));
    }

}
