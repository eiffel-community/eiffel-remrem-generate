package com.ericsson.eiffel.remrem.generate;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;

import com.ericsson.eiffel.remrem.generate.cli.CLI;
import com.ericsson.eiffel.remrem.generate.cli.CLIExitCodes;
import com.ericsson.eiffel.remrem.generate.cli.CLIOptions;
import com.ericsson.eiffel.remrem.generate.config.PropertiesConfig;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations={"/EiffelSemanticsCli-context.xml"})
public class EiffelSemanticsCli {
	private PrintStream console;
    private ByteArrayOutputStream bytes;
    
    static String artifactPublishedFileName = "ArtifactPublished.json";
    File temp ;
		
	@Autowired
    private CLI cli;
    
    @Before public void setUp() throws Exception {
    	String key = PropertiesConfig.TEST_MODE;
        System.setProperty(key, "true");
        // Switch std out to another stream in case
        // we need to check output
        bytes   = new ByteArrayOutputStream();		  
        console = System.out;
        System.setOut(new PrintStream(bytes));
        
        try{
     	   //create a temp file
     	   temp = File.createTempFile("outputMessage", ".tmp");     	   
     	}catch(IOException e){
     	   e.printStackTrace();
     	}
    }
    
    @After
    public void tearDown() {
    	 System.clearProperty(PropertiesConfig.TEST_MODE);
         System.setOut(console);
         // reset error code since it is static
         CLIOptions.cleanErrorCodes();
    }
    
    @Test
    public void testWrongMessageTypeFail() throws Exception {	
    	File file = new File(getClass().getClassLoader().getResource(artifactPublishedFileName).getFile());
    	String filePath = file.getAbsolutePath();
    	
        String[] args = {"-t", "artifactpublishednone", "-f", filePath};
        CLIOptions.parse(args);
        cli.run(args);	
        String message = bytes.toString();
        boolean conditionTrue = message.contains("Unknown message type requested");
        assertTrue(conditionTrue);
    }
    
    @Test
    public void testIncompleteMessageContentFail() throws Exception {	
    	String jsonContent = "{\"msgParams\": {\"meta\":{\"fakseContent\":\"yes\"}}, \"eventParams\": {\"falseKey\" : \"none\"}}";
        String[] args = {"-t", "eiffelartifactpublished", "-json", jsonContent};
        CLIOptions.parse(args);
        cli.run(args);	
        String message = bytes.toString();
        boolean conditionTrue = message.contains("Cannot validate given JSON string");
        assertTrue(conditionTrue);
    }
    
    @Test
    public void testMalformedJsonFail() throws Exception {
    	String jsonContent = "{\"someKey\":\"someValue\"}";
        String[] args = {"-t", "eiffelartifactpublished", "-json", jsonContent};
        CLIOptions.parse(args);
        cli.run(args);
        String message = bytes.toString();
        int code = CLIExitCodes.HANDLE_JSON_STRING_FAILED;
        assertTrue(CLIOptions.getErrorCodes().contains(code));
    }
}
