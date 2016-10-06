package com.ericsson.eiffel.remrem.generate.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.ericsson.eiffel.remrem.generate.controller.Eiffel3Controller;
import com.ericsson.eiffel.remrem.shared.MsgService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit4.SpringRunner;

//@RunWith(SpringJUnit4ClassRunner.class)
@RunWith(SpringRunner.class)
//@SpringApplicationConfiguration(FakeConfig.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class Eiffel3ControllerUnitTest {

    @InjectMocks
    Eiffel3Controller unit = new Eiffel3Controller();

    @Mock
    MsgService service;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Mockito.when(service.generateMsg(
                Mockito.anyString(),
                Mockito.anyObject()
        )).thenReturn("{ \"service\":\"Eiffel3Controller\" }");
    }

    @Test public void sendNormal() throws Exception {
        JsonElement result = unit.generateMsg("test", new JsonObject());
        System.out.println(result);
    }

}
