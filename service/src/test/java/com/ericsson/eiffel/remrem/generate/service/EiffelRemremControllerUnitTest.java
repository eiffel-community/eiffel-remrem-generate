package com.ericsson.eiffel.remrem.generate.service;


import static org.junit.Assert.assertEquals;

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
import org.springframework.test.context.junit4.SpringRunner;

import com.ericsson.eiffel.remrem.generate.controller.RemremGenerateController;
import com.ericsson.eiffel.remrem.protocol.MsgService;
import com.google.gson.JsonElement;
import org.springframework.http.ResponseEntity;

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


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        msgServices.add(service);
        msgServices.add(service2);
        Mockito.when(service.getServiceName()).thenReturn("eiffelsemantics");
        Mockito.when(service2.getServiceName()).thenReturn("eiffel3");
        
        String successOutput = "{\"meta\":{\"id\":\"646cb3ff-30b5-42f7-b686-220131287b2d\",\"type\":\"EiffelActivityFinishedEvent\",\"version\":\"1.0.0\",\"time\":1487851528091,\"tags\":[\"tag1\",\"tag2\"],\"source\":{\"domainId\":\"domainID\",\"host\":\"host\",\"name\":\"name\",\"serializer\":{\"groupId\":\"G\",\"artifactId\":\"A\",\"version\":\"V\"},\"uri\":\"http://java.sun.com/j2se/1.3/\"}},\"data\":{\"outcome\":{\"conclusion\":\"TIMED_OUT\",\"description\":\"Compilation timed out.\"},\"persistentLogs\":[{\"name\":\"firstLog\",\"uri\":\"http://myHost.com/firstLog\"},{\"name\":\"otherLog\",\"uri\":\"http://myHost.com/firstLog33\"}],\"customData\":[]},\"links\":[{\"type\":\"LinkTargetType\",\"target\":\"LinkTarget\"}]}";
        String failureOutput = "{\"message\":\"Unknown message type requested\",\"cause\":\"\u0027EiffelActivityFinis\u0027 is not in the vocabulary of this service\"}";

        
        Mockito.when(service.generateMsg(
                Mockito.eq("EiffelActivityFinishedEvent"),
                Mockito.anyObject())).thenReturn(successOutput);
        

        Mockito.when(service.generateMsg(
                Mockito.eq("EiffelActivityFinished"),
                Mockito.anyObject())).thenReturn(failureOutput);
        
        Mockito.when(service2.generateMsg(
                Mockito.eq("eiffelartifactnew"),
                Mockito.anyObject())).thenReturn(successOutput);
        
        Mockito.when(service2.generateMsg(
                Mockito.eq("eiffelartifactnewevent"),
                Mockito.anyObject())).thenReturn(failureOutput);
     
    }
    
    @Test
    public void testSemanticsSuccessEvent() throws Exception {        
        ResponseEntity<?> elem = unit.generate("eiffelsemantics", "EiffelActivityFinishedEvent", body.getAsJsonObject());
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
