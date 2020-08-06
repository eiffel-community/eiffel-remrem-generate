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
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.eiffel.remrem.generate.config.PropertiesConfig;


public class CliOptionsUnitTests {
    private PrintStream console;
    private ByteArrayOutputStream bytes;

    @Before public void setUp() throws Exception {
        String key = PropertiesConfig.TEST_MODE;
        System.setProperty(key, "true");
        bytes   = new ByteArrayOutputStream();		  
        console = System.out;
        System.setOut(new PrintStream(bytes));
    }

    @After
    public void tearDown() {
        System.clearProperty(PropertiesConfig.TEST_MODE);
        System.setOut(console);
        // reset error code since it is static
        CLIOptions.cleanErrorCodes();
    }	

    @Test
    public void testParseEmptyCLIOptionsFails() throws Exception {	
        String[] args = new String[0];	    

        CLIOptions.parse(args);
        int code = CLIExitCodes.CLI_MISSING_OPTION_EXCEPTION;
        assertTrue(CLIOptions.getErrorCodes().contains(code));
    }
		
    @Test
    public void testHelpOptionOnlyWorks() throws Exception {
        String[] args = {"-h"};
        CLIOptions.parse(args);
        assertTrue(CLIOptions.getErrorCodes().contains(0));
        assertTrue(CLIOptions.getErrorCodes().size() == 1);
    }
	
    @Test
    public void testHelpOptionWorks() throws Exception {
        String[] args = {"-h", "-r", "respons file"};
        CLIOptions.parse(args);
        assertTrue(CLIOptions.getErrorCodes().contains(0));
        assertTrue(CLIOptions.getErrorCodes().size() == 1);        	
    }
    
    @Test
    public void testMessageTypeOptionMissing() throws Exception {
        String[] args = {"-f", "input_file", "-r", "respons file"};
        CLIOptions.parse(args);
        int code = CLIExitCodes.CLI_MISSING_OPTION_EXCEPTION;
        assertTrue(CLIOptions.getErrorCodes().contains(code));
    }
    
    @Test
    public void testContentOptionMissing() throws Exception {
        String[] args = {"-t", "artifactpublished", "-r", "respons file"};
        CLIOptions.parse(args);
        int code = CLIExitCodes.CLI_MISSING_OPTION_EXCEPTION;
        assertTrue(CLIOptions.getErrorCodes().contains(code));
    }  
    
    @Test
    public void testRequiredOptionsGiven() throws Exception {
        String[] args = {"-f", "input_file", "-t", "artifactpublished"};
        CLIOptions.parse(args);
        assertTrue(CLIOptions.getErrorCodes().isEmpty());
    }
    
    @Test
    public void testIgnoreOptionalFieldValidationErrorsGiven() throws Exception {
        String[] args = {"-f", "input_file", "-t", "artifactpublished", "-iov", "true"};
        CLIOptions.parse(args);
        assertTrue(CLIOptions.getErrorCodes().isEmpty());
    }
    
}