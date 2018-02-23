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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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


    @SuppressWarnings("resource")
	@Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
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

        Mockito.when(service.generateMsg(
                Mockito.eq("eiffelactivityfinished"),
                Mockito.anyObject())).thenReturn(successOutput);
        
        Mockito.when(service.generateMsg(
                Mockito.eq("EiffelActivityFinished"),
                Mockito.anyObject())).thenReturn(errorOutput);
        
        Mockito.when(service2.generateMsg(
                Mockito.eq("eiffelartifactnew"),
                Mockito.anyObject())).thenReturn(successOutput);
        
        Mockito.when(service2.generateMsg(
                Mockito.eq("eiffelartifactnewevent"),
                Mockito.anyObject())).thenReturn(errorOutput);
     
    }
    
    @Test
    public void testSemanticsSuccessEvent() throws Exception {        
        ResponseEntity<?> elem = unit.generate("eiffelsemantics", "eiffelactivityfinished", body.getAsJsonObject());
        assertEquals(elem.getStatusCode(), HttpStatus.OK);
    }
    
    @Test
    public void testSemanticsFailureEvent() throws Exception {        
        ResponseEntity<?> elem = unit.generate("eiffelsemantics", "EiffelActivityFinished", body.getAsJsonObject());
        assertEquals(elem.getStatusCode(), HttpStatus.BAD_REQUEST);
    }
    
    @Test
    public void testEiffel3SuccessEvent() throws Exception {        
        ResponseEntity<?> elem = unit.generate("eiffel3", "eiffelartifactnew", body.getAsJsonObject());
        assertEquals(elem.getStatusCode(), HttpStatus.OK);
    }
    
    @Test
    public void testEiffel3FailureEvent() throws Exception {        
        ResponseEntity<?> elem = unit.generate("eiffel3", "eiffelartifactnewevent", body.getAsJsonObject());
        assertEquals(elem.getStatusCode(), HttpStatus.BAD_REQUEST);
    }
    
    @Test
    public void testMessageServiceUnavailableEvent() throws Exception {        
        ResponseEntity<?> elem = unit.generate("other", "EiffelActivityFinishedEvent", body.getAsJsonObject());
        assertEquals(elem.getStatusCode(), HttpStatus.SERVICE_UNAVAILABLE);
    }
     
}
