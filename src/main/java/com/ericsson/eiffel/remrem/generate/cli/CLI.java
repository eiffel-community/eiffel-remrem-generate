package com.ericsson.eiffel.remrem.generate.cli;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import com.ericsson.eiffel.remrem.semantics.SemanticsService;
import com.ericsson.eiffel.remrem.shared.MsgService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author evasiba
 *
 */
public class CLI {
    private Options options=null;

    public CLI() {
        options = createCLIOptions();
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
        System.exit(0);
    }

    /**
     * Parse the given arguments and act on them
     * @param args command line arguments
     * @return if the service should start or not
     */
    public boolean parse(String[] args) {
        CommandLineParser parser = new DefaultParser(); 
        boolean startService = true;
        try {
            CommandLine commandLine = parser.parse(options, args);
            Option[] existingOptions = commandLine.getOptions(); 
            if (existingOptions.length > 0) {
                startService = false;
            }

            if (commandLine.hasOption("f") && commandLine.hasOption("t")) {
            	handleFileArgs(commandLine);
            } else if (commandLine.hasOption("json") && commandLine.hasOption("t")) {
            	handleJsonArgs(commandLine);
            }else {
                help(options);
            }
        } catch (Exception e) {
            help(options);
        }
        return startService;
    }
    
    private void handleFileArgs(CommandLine commandLine) {
    	String filePath = commandLine.getOptionValue("f");       
        String jsonContent = readJsonContent(filePath);
        handleJsonString(jsonContent, commandLine);
    }
    
    private String readJsonContent(String filePath) {
		try {
			JsonParser parser = new JsonParser();
			byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
	        return new String(fileBytes);	    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }
    
    private void handleJsonArgs(CommandLine commandLine) {
    	String jsonContent = commandLine.getOptionValue("json");
    	handleJsonString(jsonContent, commandLine);
    }
    
    /**
     * Handle message from file
     * @param msgType the Eiffel message type
     * @param filePath the file path where the message content resides
     * @param responseFilePath the file path where to store the prepared message, stdout if null
     */
    private void handleJsonString(String jsonString,
    							  CommandLine commandLine) {
        
        MsgService msgService = new SemanticsService();
        String responseFilePath = null; 
        if (commandLine.hasOption("r"))
            responseFilePath = commandLine.getOptionValue("r");
        String msgType = commandLine.getOptionValue("t");
        try {
        	JsonParser parser = new JsonParser();
        	JsonObject jsonContent = parser.parse(jsonString).getAsJsonObject();
        	String returnJsonStr = msgService.generateMsg(msgType, jsonContent);
            if (responseFilePath != null) {
                try(  PrintWriter out = new PrintWriter( responseFilePath )  ){
                    out.println( returnJsonStr );
                }
            } else {
                System.out.println( returnJsonStr );
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}