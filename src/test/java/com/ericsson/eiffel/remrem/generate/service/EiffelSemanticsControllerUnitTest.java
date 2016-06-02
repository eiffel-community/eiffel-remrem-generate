package com.ericsson.eiffel.remrem.generate.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.ericsson.eiffel.remrem.generate.controller.EiffelSemanticsController;
import com.ericsson.eiffel.remrem.shared.MsgService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(FakeConfig.class)
public class EiffelSemanticsControllerUnitTest {

    @InjectMocks
    EiffelSemanticsController unit = new EiffelSemanticsController();

    @Mock
    MsgService service;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Mockito.when(service.generateMsg(
                Mockito.anyString(),
                Mockito.anyObject()
        )).thenReturn("{ \"service\":\"EiffelSemanticsController\" }");
    }

    @Test
    public void sendNormal() throws Exception {
        JsonElement result = unit.generateMsg("test", new JsonObject());
        System.out.println(result);
    }

}
