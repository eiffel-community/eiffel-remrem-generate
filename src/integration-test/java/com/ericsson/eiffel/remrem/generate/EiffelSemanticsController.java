package com.ericsson.eiffel.remrem.generate;

import com.google.gson.JsonParser;

import com.jayway.restassured.RestAssured;

import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static com.jayway.restassured.RestAssured.given;

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

    @Before
    public void  setUp() throws FileNotFoundException {
        RestAssured.port = port;

        File file = new File(getClass().getClassLoader().getResource(artifactPublishedFileName).getFile());
        artifactPublishedBody = new Scanner(file)
            .useDelimiter("\\A").next();

        file = new File(getClass().getClassLoader().getResource(activityFinishedFileName).getFile());
        activityFinishedBody = new Scanner(file)
            .useDelimiter("\\A").next();
    }

    @Test public void sendArtifactPublished() throws Exception {
        given().contentType("application/json").body(artifactPublishedBody).
            when().
            post("/eiffelsemantics?msgType=eiffelartifactpublished").
            then().
            statusCode(HttpStatus.SC_OK)
            .body("meta.type", Matchers.is("eiffelartifactpublished"))
            .body("meta.version", Matchers.is("0.1.6"));
    }

    @Test public void sendActivityFinished() throws Exception {
        given().contentType("application/json").body(activityFinishedBody).
            when().
            post("/eiffelsemantics?msgType=eiffelactivityfinished").
            then().
            statusCode(HttpStatus.SC_OK)
            .body("meta.type", Matchers.is("eiffelactivityfinished"))
            .body("meta.version", Matchers.is("0.1.6"));
    }

}
