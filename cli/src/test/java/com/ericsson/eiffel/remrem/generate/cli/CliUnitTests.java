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
package com.ericsson.eiffel.remrem.generate.cli;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.ericsson.eiffel.remrem.generate.config.PropertiesConfig;
import com.ericsson.eiffel.remrem.protocol.MsgService;

public class CliUnitTests {
    private PrintStream console;
    private ByteArrayOutputStream bytes;
	
    @Mock
    private MsgService msgService;	

    private CLI cli;
	
    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        String key = PropertiesConfig.TEST_MODE;
        System.setProperty(key, "true");
        // Switch std out to another stream in case
        // we need to check output
        bytes   = new ByteArrayOutputStream();		  
        console = System.out;
        System.setOut(new PrintStream(bytes));
        List<MsgService> msgServices = new ArrayList<MsgService>();
        msgServices.add(msgService);
        cli = new CLI(msgServices);
        
        Mockito.when(msgService.generateMsg(
	                Mockito.anyString(),
	                Mockito.anyObject()
	        )).thenReturn("{ \"service\":\"msgService\" }");
    }
	
    @After
    public void tearDown() {
        System.clearProperty(PropertiesConfig.TEST_MODE);
        System.setOut(console);
        // reset error code since it is static
        CLIOptions.cleanErrorCodes();
    }

    @Test
    public void testHandleFileArgsFail() throws Exception {	
        String[] args = {"-t", "eiffelactivityfinished", "-f", "filename"};
        CLIOptions.parse(args);
        cli.run(args);		
        int code = CLIExitCodes.CLI_READ_FILE_FAILED;
        assertTrue(CLIOptions.getErrorCodes().contains(code));		
    }

    @Test
    public void testHandleFileArgsPass() throws Exception {
        URL url = getClass().getClassLoader().getResource("jsonTest.json");
        String path = url.getPath().replace("%20"," ");
        File file = new File(path);
        String filePath = file.getAbsolutePath();

        String[] args = {"-t", "eiffelactivityfinished", "-f", filePath};
        CLIOptions.parse(args);
        cli.run(args);		
        assertTrue(CLIOptions.getErrorCodes().isEmpty());		
    }
	
    @Test
    public void testHandleJsonArgsPass() throws Exception {
        String[] args = {"-t", "eiffelactivityfinished", "-json", "{someKey:someValue}"};
        CLIOptions.parse(args);
        cli.run(args);		
        assertTrue(CLIOptions.getErrorCodes().isEmpty());		
    }
	
    @Test
    public void testHandleJsonArgsFail() throws Exception {
        String[] args = {"-t", "eiffelactivityfinished", "-json", "filename"};
        CLIOptions.parse(args);
        cli.run(args);		
        int code = CLIExitCodes.HANDLE_JSON_STRING_FAILED;
        assertTrue(CLIOptions.getErrorCodes().contains(code));
    }
    
    @Test
    public void testHandleMsgTypeEventArgsPass() throws Exception {
        String[] args = {"-t", "eiffelactivityfinished", "-json", "{someKey:someValue}"};
        CLIOptions.parse(args);
        cli.run(args);		
        assertTrue(CLIOptions.getErrorCodes().isEmpty());		
    }
    
    @Test
    public void testHandlelenientValidationArgsPass() throws Exception {
        URL url = getClass().getClassLoader().getResource("jsonTest.json");
        String path = url.getPath().replace("%20"," ");
        File file = new File(path);
        String filePath = file.getAbsolutePath();

        String[] args = {"-t", "eiffelactivityfinished", "-f", filePath, "-lv" , "true"};
        CLIOptions.parse(args);
        cli.run(args);		
        assertTrue(CLIOptions.getErrorCodes().isEmpty());		
    }
    
}