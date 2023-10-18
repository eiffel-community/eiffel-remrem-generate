package com.ericsson.eiffel.remrem.generate.service;

import com.ericsson.eiffel.remrem.generate.controller.RemremGenerateController;
import com.ericsson.eiffel.remrem.protocol.MsgService;
import com.ericsson.eiffel.remrem.semantics.EiffelEventType;
import com.ericsson.eiffel.remrem.semantics.config.LinksConfiguration;
import com.ericsson.eiffel.remrem.semantics.factory.EiffelOutputValidatorFactory;
import com.ericsson.eiffel.remrem.semantics.validator.EiffelValidator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.tree.BaseJsonTree;
import com.github.fge.jsonschema.core.tree.BaseSchemaTree;
import com.github.fge.jsonschema.core.tree.JsonTree;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.jsonschema.processors.data.SchemaContext;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.RequestEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

//import static com.ericsson.eiffel.remrem.semantics;
import javax.xml.validation.SchemaFactoryLoader;
import java.io.Reader;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableAutoConfiguration
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EiffelTemplateControllerTest {

    @Autowired
    private MsgService msgServices;
    @Autowired
    RemremGenerateController remremGenerateController = new RemremGenerateController();

   LinksConfiguration linksConfiguration = new LinksConfiguration();

    private JsonSchema jsonSchemas;
    RequestEntity requestEntity;

    @Test
    public void testEiffel3SuccessEvent() throws Exception, ProcessingException {
        //ResponseEntity res= remremGenerateController.getEventTypeTemplate("eiffelartifactnew",
                //"eiffel3", requestEntity);
        //Thread.sleep(10000000);
        JsonElement jsn= msgServices.getEventTemplate("EiffelArtifactPublishedEvent");
        JsonObject jsonObject = jsn.getAsJsonObject();
        //if (jsonObject!= null) {
            String version = jsonObject.getAsJsonObject("msgParams").getAsJsonObject("meta").get("version").getAsString();
        String msgType = "EiffelArtifactPublishedEvent";
        EiffelEventType eiffelType = EiffelEventType.fromString(msgType);
        EiffelValidator validator = EiffelOutputValidatorFactory.getEiffelValidator(eiffelType);
        JsonObject eiffelValidator1 = validator.validate(jsonObject);
        EiffelValidator eiffelValidator = new EiffelValidator("schemas/input/EiffelArtifactPublishedEvent.json",
                msgType, linksConfiguration.getRequiredLinkTypes(msgType),
                linksConfiguration.getOptionalLinkTypes(msgType), linksConfiguration.getAllLinkTypes());
        JsonArray vls= eiffelValidator.getCustomData(jsonObject);
        ObjectMapper mapper = new ObjectMapper();
        JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
        ClassLoader classLoader = getClass().getClassLoader();
        jsonSchemas = factory.getJsonSchema(
                mapper.readTree(classLoader.getResourceAsStream("schemas/input/EiffelArtifactPublishedEvent.json")));
        String schema =jsonSchemas.toString();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonObject.toString());


        JsonArray vl = validator.getCustomData(jsonObject);
        try {
            ProcessingReport processingReport = jsonSchemas.validate(jsonNode);
        }
        catch (Exception e) {
            String info = "not do it" + e;

        }
        //assertEquals(elem.getStatusCode(), HttpStatus.OK);
    }

}
