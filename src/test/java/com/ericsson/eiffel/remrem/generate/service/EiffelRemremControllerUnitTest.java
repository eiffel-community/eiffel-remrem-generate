package com.ericsson.eiffel.remrem.generate.service;

<<<<<<< HEAD
import java.util.ArrayList;
import java.util.List;
=======
import java.io.File;
import java.net.URL;
import java.util.Scanner;
>>>>>>> 36166bca759b8fa5e8f19a581e22a819347bc97a

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
<<<<<<< HEAD
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.ComponentScan;
=======
>>>>>>> 36166bca759b8fa5e8f19a581e22a819347bc97a
import org.springframework.test.context.junit4.SpringRunner;

import com.ericsson.eiffel.remrem.generate.controller.RemremGenerateController;
import com.ericsson.eiffel.remrem.protocol.MsgService;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import junit.framework.Assert;

@RunWith(SpringRunner.class)
public class EiffelRemremControllerUnitTest {
    
    @InjectMocks
    RemremGenerateController unit = new RemremGenerateController();
    
    @Mock
    MsgService service;
    

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
        
        Mockito.when(service.generateMsg(
            Mockito.eq("eiffelactivityfinished"),
            Mockito.anyObject()
        )).thenReturn("{ \"result\":\"SUCCESS\" }");
        
        Mockito.when(service.generateMsg(
                Mockito.eq("eiffeljobfinished"),
                Mockito.anyObject()
        )).thenReturn("{ \"result\":\"FAILURE\" }");
    }
    
    @SuppressWarnings("deprecation")
    @Test 
    public void testSemanticsEvent() throws Exception {        
        JsonElement elem = unit.generateMsg("eiffelactivityfinished", body.getAsJsonObject());
        Assert.assertEquals(elem.getAsJsonObject().get("result").getAsString(), "SUCCESS");
    }
    
    @SuppressWarnings("deprecation")
    @Test
    public void testSemanticsFailureEvent() throws Exception{
        JsonElement elem = unit.generateMsg("eiffeljobfinished", body.getAsJsonObject());
        Assert.assertEquals(elem.getAsJsonObject().get("result").getAsString(), "FAILURE");
    }
    

}
