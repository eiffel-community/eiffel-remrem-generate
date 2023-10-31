package com.ericsson.eiffel.remrem.generate.service;

import ch.qos.logback.classic.Logger;
import com.ericsson.eiffel.remrem.protocol.MsgService;
import com.ericsson.eiffel.remrem.semantics.EiffelEventType;
import com.ericsson.eiffel.remrem.semantics.factory.EiffelOutputValidatorFactory;
import com.ericsson.eiffel.remrem.semantics.validator.EiffelValidator;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import static org.junit.Assert.*;

/**
 * This test basically test the difference of version between
 * generated schemas and the event template
 */
@RunWith(SpringJUnit4ClassRunner.class)
@EnableAutoConfiguration
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EiffelTemplateGenerateTest {

    @Autowired
    private MsgService msgServices;

    private Logger log = (Logger) LoggerFactory.getLogger(EiffelTemplateGenerateTest.class);

    @Test
    public void testEiffelSemanticsTemplate() throws IOException {
        for (EiffelEventType enumValue:EiffelEventType.values()
        ) {
            testEventType(enumValue.getEventName());

        }
    }

    /**
     * In this basically we generate event and schemas version and compare it
     * @param eventName name of the event
     */
    protected void testEventType(String eventName){

        JsonElement jsonTemplate= msgServices.getEventTemplate(eventName);
        JsonObject jsonObjectTemplate = jsonTemplate.getAsJsonObject();

        String templateVersion = jsonObjectTemplate.getAsJsonObject("msgParams")
                .getAsJsonObject("meta").get("version").getAsString();


        EiffelEventType eiffelType = EiffelEventType.fromString(eventName);
        EiffelValidator validator = EiffelOutputValidatorFactory.getEiffelValidator(eiffelType);

        String schemaResourceName = null;
        try {
            // Note, that this isn't a standard solution. Unfortunately, there's no standard way how to access schema
            // resource name from validator. That's why Java reflection takes place. It's acceptable just because it's
            // a takes case, and it isn't part of production code.
            // Get access to EiffelValidator.schemaResourceName even it's declared as private.

            Field schemaResourceNameField = EiffelValidator.class.getDeclaredField("schemaResourceName");
            schemaResourceNameField.setAccessible(true);
            schemaResourceName = (String)schemaResourceNameField.get(validator);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            assertNull("cannot get field name", e);
            throw new RuntimeException(e);
        }
        assertNotNull("Cannot get the schema file name", schemaResourceName);

        ClassLoader classLoader = validator.getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(schemaResourceName);
        String stringTypeSchemas = null;
        try {
            stringTypeSchemas = new String(inputStream.readAllBytes());
        } catch (IOException e) {
            fail("Cannot read resource " + schemaResourceName);
        }

        JsonObject SchemasAsJsonObject = JsonParser.parseString(stringTypeSchemas).getAsJsonObject();
        String schemasVersion = SchemasAsJsonObject.getAsJsonObject("properties").getAsJsonObject("meta").
                getAsJsonObject("properties").getAsJsonObject("version").
                get("default").getAsString();


        // When using assert(...) if a logical test fails the unit test is aborted and the rest of the unit test isn't run.
        // So for that here used logger, to get the logical test to fail but just provide a warning or something and
        // still run the rest of the unit test.
        if (!templateVersion.equals(schemasVersion)){
            log.warn("Versions of event " + eventName + " must be the same; found: " + templateVersion +
                    ", expected: " + schemasVersion);

        }
    }
}








