package com.ericsson.eiffel.remrem.generate.service;

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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import com.ericsson.eiffel.remrem.generate.controller.RemremGenerateController;
import com.ericsson.eiffel.remrem.protocol.MsgService;
import com.google.gson.JsonObject;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
@ComponentScan("com.ericsson.eiffel.remrem")
public class EiffelRemremControllerUnitTest {
    @InjectMocks
    RemremGenerateController unit = new RemremGenerateController();

    @Mock
    MsgService service;
    
    @Spy
    private List<MsgService> msgServices = new ArrayList<MsgService>();

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        msgServices.add(service);
        Mockito.when(service.generateMsg(
                Mockito.anyString(),
                Mockito.anyObject()
        )).thenReturn("{ \"service\":\"Eiffel3Controller\" }");
    }

    @Test public void sendNormal() throws Exception {
        String result = service.generateMsg( "test", new JsonObject());
        System.out.println(result);
    }
    

}
