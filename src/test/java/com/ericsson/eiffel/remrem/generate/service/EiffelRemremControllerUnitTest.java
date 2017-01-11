package com.ericsson.eiffel.remrem.generate.service;

import static com.jayway.restassured.RestAssured.given;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Base64;
import java.util.Scanner;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.jayway.restassured.RestAssured;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class EiffelRemremControllerUnitTest {
    
    @Value("${local.server.port}")
    int port;

    private String credentials = "Basic " + Base64.getEncoder().encodeToString("user:secret".getBytes());
    static String sampleFile = "samplesemantics.json";
    static String sampleBody;
    
    @Before
    public void  setUp() throws FileNotFoundException {
        RestAssured.port = port;
        try{
            URL url = getClass().getClassLoader().getResource(sampleFile);
            String path = url.getPath().replace("%20"," ");
            File file = new File(path);
            sampleBody = new Scanner(file).useDelimiter("\\A").next();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    @Test 
    public void testSendSingle() throws Exception {
        given()
                .header("Authorization", credentials)
                .contentType("application/json")
                .body(sampleBody)
                .when()
                    .post("/generate/eiffelsemantics?msgType=eiffelactivityfinished")
                .then()
                    .statusCode(HttpStatus.SC_OK);
        
    }

}
