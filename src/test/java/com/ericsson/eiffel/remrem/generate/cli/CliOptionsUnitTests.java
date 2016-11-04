package com.ericsson.eiffel.remrem.generate.cli;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import org.springframework.test.context.junit4.SpringRunner;

import com.ericsson.eiffel.remrem.generate.config.PropertiesConfig;

@RunWith(SpringRunner.class)
public class CliOptionsUnitTests {
    private PrintStream console;
    private ByteArrayOutputStream bytes;

    @Before public void setUp() throws Exception {
        String key = PropertiesConfig.TEST_MODE;
        System.setProperty(key, "true");
        //Switch std out to another stream
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
}
