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
import com.google.gson.Gson;
import com.google.gson.JsonElement;
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
        options.addOption("t", "message_type", true, "message type, mandatory if -f is given");
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

            if (commandLine.hasOption("h")) {
                help(options);
            }
            
            if (commandLine.hasOption("f") && commandLine.hasOption("t")) {
                String filePath = commandLine.getOptionValue("f");
                String responseFilePath = null; 
                if (commandLine.hasOption("r"))
                    responseFilePath = commandLine.getOptionValue("r");
                String msgType = commandLine.getOptionValue("t");
                handleContentFile(msgType, filePath, responseFilePath);
            }
        } catch (Exception e) {
            help(options);
        }
        return startService;
    }
    
    /**
     * Handle message from file
     * @param msgType the Eiffel message type
     * @param filePath the file path where the message content resides
     * @param responseFilePath the file path where to store the prepared message, stdout if null
     */
    public void handleContentFile(String msgType,
                                  String filePath,
                                  String responseFilePath) {
        JsonParser parser = new JsonParser();
        MsgService msgService = new SemanticsService();
        try {
            byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
            String fileContent = new String(fileBytes);
            JsonObject bodyJson = parser.parse(fileContent).getAsJsonObject();
            JsonElement returnJson = parser.parse(msgService.generateMsg(msgType, bodyJson));
            Gson gson = new Gson();
            String returnJsonStr = gson.toJson(returnJson);
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