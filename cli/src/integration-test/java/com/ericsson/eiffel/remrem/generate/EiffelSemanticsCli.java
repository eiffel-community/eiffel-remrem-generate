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
package com.ericsson.eiffel.remrem.generate;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;

import com.ericsson.eiffel.remrem.generate.cli.CLI;
import com.ericsson.eiffel.remrem.generate.cli.CLIExitCodes;
import com.ericsson.eiffel.remrem.generate.cli.CLIOptions;
import com.ericsson.eiffel.remrem.generate.config.PropertiesConfig;

@ContextConfiguration(initializers=ConfigFileApplicationContextInitializer.class, locations={"/EiffelSemanticsCli-context.xml"})
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
        URL url = getClass().getClassLoader().getResource(artifactPublishedFileName);
        String path = url.getPath().replace("%20"," ");
        File file = new File(path);
    	String filePath = file.getAbsolutePath();
    	
        String[] args = {"-t", "artifactpublishednone", "-f", filePath};
        CLIOptions.parse(args);
        cli.main(args);	
        String message = bytes.toString();
        boolean conditionTrue = message.contains("Unknown event type requested");
        assertTrue(conditionTrue);
    }
    
    @Test
    public void testIncompleteMessageContentFail() throws Exception {	
    	String jsonContent = "{\"msgParams\": {\"meta\":{\"fakseContent\":\"yes\"}}, \"eventParams\": {\"falseKey\" : \"none\"}}";
        String[] args = {"-t", "eiffelartifactpublished", "-json", jsonContent};
        CLIOptions.parse(args);
        cli.main(args);	
        String message = bytes.toString();
        boolean conditionTrue = message.contains("Cannot validate given JSON string");
        assertTrue(conditionTrue);
    }
    
    @Test
    public void testMalformedJsonFail() throws Exception {
    	String jsonContent = "{\"someKey\":\"someValue\"}";
        String[] args = {"-t", "eiffelactivityfinished", "-json", jsonContent};
        CLIOptions.parse(args);
        cli.main(args);
        String message = bytes.toString();
        int code = CLIExitCodes.HANDLE_JSON_STRING_FAILED;
        assertTrue(CLIOptions.getErrorCodes().contains(code));
    }
    
}