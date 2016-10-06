package com.ericsson.eiffel.remrem.generate.cli;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;

import com.ericsson.eiffel.remrem.semantics.SemanticsService;
import com.ericsson.eiffel.remrem.shared.MsgService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

/**
 * Class for interpreting the passed arguments from command line.
 * Parse method returns true, meaning we need to start the service afterwards, if no argument
 * is given. The same method returns false, meaning we do not start the service afterwards, if any
 * argument is given. If an argument is given that it is not recognized we print help.
 * @author evasiba
 *
 */
@Component
@ComponentScan(basePackages = "com.ericsson.eiffel.remrem")
public class CLI implements CommandLineRunner{
    private Options options=null;
    @Autowired 
    private MsgService[] msgServices;

    public CLI() {       	
    	options = createCLIOptions();
    }

    @Override
	public void run(String... args) throws Exception {
		parse(args);
	}
    
    /**
     * Creates the options needed by command line interface
     * @return the options this CLI can handle
     */
    private static Options createCLIOptions() {
        Options options = new Options();
        options.addOption("h", "help", false, "show help.");
        options.addOption("f", "content_file", true, "message content file");
        options.addOption("json", "json_content", true, "json content");
        options.addOption("t", "message_type", true, "message type, mandatory if -f or -json is given");
        options.addOption("r", "response_file", true, "file to store the response in, optional");
        options.addOption("d", "debug", false, "enable debug traces");
        options.addOption("mp", "messaging_protocol", true, "name of messaging protocol to be used, e.g. eiffel3, semantics");
        return options;
    }

    /**
     * Prints the help for this application and exits.
     * @param options the options to print usage help for
     */
    private static void help(Options options) {
        // This prints out some help
        HelpFormatter formater = new HelpFormatter();
        formater.printHelp("java -jar", options);
        System.exit(1);
    }

    /**
     * Parse the given arguments and act on them
     * @param args command line arguments
     * @return if the service should start or not
     */
    public boolean parse(String[] args) {
    	Logger log = (Logger) LoggerFactory.getLogger("ROOT");
        CommandLineParser parser = new DefaultParser(); 
        boolean startService = true;
        try {
            CommandLine commandLine = parser.parse(options, args);
            Option[] existingOptions = commandLine.getOptions(); 
            if (existingOptions.length > 0) {
                startService = false;
                handleOptions(commandLine);
            }
        } catch (Exception e) {
        	e.printStackTrace();
            help(options);
        }
        return startService;
    }
    
    /**
     * @param commandLine
     */
    private void handleLogging(CommandLine commandLine) {
    	if (!commandLine.hasOption("-d")) { 
    		//Eiffel 3 messaging logs to stdout but since we also write
    		//to stdout we need to turn off logging unless specified by the user
    		System.setProperty("logging.level.root", "OFF");
    		Logger log = (Logger) LoggerFactory.getLogger("ROOT");
    		log.setLevel(Level.OFF);
    	}
    }
    
    /**
     * Delegates actions depending on the passed arguments
     * @param commandLine command line arguments
     */
    private void handleOptions(CommandLine commandLine) {
    	handleLogging(commandLine);
    	if (commandLine.hasOption("h")) {
    		System.out.println("You passed help flag.");
    		help(options);
    	} else if (commandLine.hasOption("f") && commandLine.hasOption("t")) {
        	handleFileArgs(commandLine);
        } else if (commandLine.hasOption("json") && commandLine.hasOption("t")) {
        	handleJsonArgs(commandLine);
        }else {
        	System.out.println("Nothing to do with the options you passed.");
            help(options);
        }
    }
    
    /**
     * Reads the content from the given file and sends it to message service
     * @param commandLine
     */
    private void handleFileArgs(CommandLine commandLine) {
    	String filePath = commandLine.getOptionValue("f");       
        String jsonContent = readFileContent(filePath);
        handleJsonString(jsonContent, commandLine);
    }
    
    /**
     * Read file content as String
     * @param filePath
     * @return
     */
    private String readFileContent(String filePath) {
		try {
			byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
	        return new String(fileBytes);	    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-2);
		}
		return null;
    }
    
    /**
     * Read passed json string from command line and sends it to message service
     * @param commandLine
     */
    private void handleJsonArgs(CommandLine commandLine) {
    	String jsonContent = commandLine.getOptionValue("json");
    	handleJsonString(jsonContent, commandLine);
    }
    
    /**
     * Send the given json string to message service
     * @param msgType the Eiffel message type
     * @param filePath the file path where the message content resides
     * @param responseFilePath the file path where to store the prepared message, stdout if null
     */
    private void handleJsonString(String jsonString,
    							  CommandLine commandLine) {
        
        String responseFilePath = null; 
        if (commandLine.hasOption("r"))
            responseFilePath = commandLine.getOptionValue("r");
        String msgType = commandLine.getOptionValue("t").toLowerCase(Locale.ROOT);
        try {
        	JsonParser parser = new JsonParser();
        	JsonObject jsonContent = parser.parse(jsonString).getAsJsonObject();
        	MsgService msgService = getMessageService(commandLine);
        	String returnJsonStr = msgService.generateMsg(msgType, jsonContent);
        	returnJsonStr = "[" + returnJsonStr + "]";
            if (responseFilePath != null) {
                try(  PrintWriter out = new PrintWriter( responseFilePath )  ){
                    out.println( returnJsonStr );
                }
            } else {
                System.out.println( returnJsonStr );
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(-1);
        }
    }
    
    private MsgService getMessageService(CommandLine commandLine) {
    	if (commandLine.hasOption("mp")) {
    		String protocol = commandLine.getOptionValue("mp");
    		for (MsgService service : msgServices) {
    			boolean isEiffel3 = (protocol.equals("eiffel3"));
    			boolean isEiffel3Service = service.getClass().getName().endsWith("Eiffel3Service"); 
    			if (isEiffel3 && isEiffel3Service)
    				return service;
    			
    		}
    	} else {
    		for (MsgService service : msgServices) {
    			if (service instanceof SemanticsService)
    				return service;
    		}
    	}
    	
    	System.out.println( "No protocol service has been found registered.");
    	System.exit(-3);
    	return null;
    }
}