package com.ericsson.eiffel.remrem.generate.service;


import java.util.ArrayList;
import java.util.List;

import java.io.File;
import java.net.URL;
import java.util.Scanner;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.mockito.Spy;
import org.springframework.test.context.junit4.SpringRunner;

import com.ericsson.eiffel.remrem.generate.controller.RemremGenerateController;
import com.ericsson.eiffel.remrem.protocol.MsgService;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;


import static org.junit.Assert.assertEquals;

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
    static String testInputName = "samplesemantics.json";
    static JsonElement body;


    @SuppressWarnings("resource")
    @Before
    public void setUp() throws Exception {
        URL url = getClass().getClassLoader().getResource(testInputName);
        String path = url.getPath().replace("%20"," ");
        File file = new File(path);
        body = new JsonParser().parse(new Scanner(file)
            .useDelimiter("\\A").next());
        MockitoAnnotations.initMocks(this);
        msgServices.add(service);
        msgServices.add(service2);
        Mockito.when(service.getServiceName()).thenReturn("eiffelsemantics");
        Mockito.when(service2.getServiceName()).thenReturn("eiffel3");
        
        Mockito.when(service.generateMsg(
            Mockito.eq("eiffelactivityfinished"),
            Mockito.anyObject()
        )).thenReturn("{ \"result\":\"SUCCESS\" }");
        
        Mockito.when(service.generateMsg(
                Mockito.eq("eiffeljobfinished"),
                Mockito.anyObject()
        )).thenReturn("{ \"result\":\"FAILURE\" }");
        
        Mockito.when(service2.generateMsg(
                Mockito.eq("eiffeljobfinished"),
                Mockito.anyObject()
        )).thenReturn("{ \"result\":\"SUCCESS\" }");
        
        
        Mockito.when(service2.generateMsg(
                Mockito.eq("eiffelactivityfinished"),
                Mockito.anyObject()
        )).thenReturn("{ \"result\":\"FAILURE\" }");
        
        
    }
    
    @Test 
    public void testSemanticsEvent() throws Exception {        
        JsonElement elem = unit.generate("eiffelsemantics", "eiffelactivityfinished", body.getAsJsonObject());
        assertEquals(elem.getAsJsonObject().get("result").getAsString(), "SUCCESS");
    }
    
    @Test
    public void testSemanticsFailureEvent() throws Exception{
        JsonElement elem = unit.generate("eiffelsemantics", "eiffeljobfinished", body.getAsJsonObject());
        assertEquals(elem.getAsJsonObject().get("result").getAsString(), "FAILURE");
    }
    
    @Test 
    public void testEiffel3Event() throws Exception {        
        JsonElement elem = unit.generate("eiffel3", "eiffeljobfinished", body.getAsJsonObject());
        assertEquals(elem.getAsJsonObject().get("result").getAsString(), "SUCCESS");
    }
    
    @Test
    public void testEiffel3FailureEvent() throws Exception{
        JsonElement elem = unit.generate("eiffel3", "eiffelactivityfinished", body.getAsJsonObject());
        assertEquals(elem.getAsJsonObject().get("result").getAsString(), "FAILURE");
    }   
}
