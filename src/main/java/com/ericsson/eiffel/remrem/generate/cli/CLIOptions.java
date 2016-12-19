package com.ericsson.eiffel.remrem.generate.cli;

import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

import com.ericsson.eiffel.remrem.generate.config.PropertiesConfig;
import com.ericsson.eiffel.remrem.shared.JarLoaderUtility;
import com.ericsson.eiffel.remrem.shared.VersionService;


public class CLIOptions {
    private static CommandLine commandLine = null;
    private static Options options = null;
    private static OptionGroup typeGroup = null;
    private static OptionGroup contentGroup = null;
    
    //Used for testing purposes
    private static ArrayList<Integer> testErrorCodes = new ArrayList<>();

    public static ArrayList getErrorCodes() {
		return testErrorCodes;
	}

	public static void addErrorCode(int errorCode) {
		testErrorCodes.add(errorCode);
	}

	public static void cleanErrorCodes() {
		testErrorCodes.clear();
	}

	public static CommandLine getCommandLine() {
        return commandLine;
    }

    /**
     * Creates the options needed by command line interface
     * 
     * @return the options this CLI can handle
     */
    public static Options createCLIOptions() {
        options = new Options();
        typeGroup = new OptionGroup();
        Option msgTypeOpt = new Option("t", "message_type", true, "message type");
        typeGroup.addOption(msgTypeOpt);        
        options.addOptionGroup(typeGroup);
               
        options.addOption("h", "help", false, "show help.");
        options.addOption("r", "response_file", true, "file to store the response in, optional");
        options.addOption("d", "debug", false, "enable debug traces");
        options.addOption("mp", "messaging_protocol", true,
                "name of messaging protocol to be used, e.g. eiffel3, semantics");

        options.addOption("jp", "jar_path", true,
                "path to find protocol definition jar files, e.g. C:/Users/xyz/Desktop/eiffel3messaging.jar");
        contentGroup = new OptionGroup();
        contentGroup.addOption(new Option("f", "content_file", true, "message content file"));
        contentGroup.addOption(new Option("json", "json_content", true, "json content"));              
        options.addOptionGroup(contentGroup);

        options.addOption("lv", "list_versions", false, "list the version and all loaded protocols");
        return options;
    }

    /**
     * Prints the help for this application and exits.
     */
    public static void help(int errorCode) {
        // This prints out some help
        HelpFormatter formater = new HelpFormatter();
        formater.printHelp("java -jar", options);     
        exit(errorCode);
    }

    /**
     * Wrapper to call system exit making class easier to test.
     * @param errorCode
     */
    public static void exit(int errorCode) {
    	boolean testMode = Boolean.getBoolean(PropertiesConfig.TEST_MODE);
    	if (testMode)
    		addErrorCode(errorCode);
    	else
    		System.exit(errorCode);
    }
    
    /**
     * Parse the given arguments and act on them
     * 
     * @param args
     *            command line arguments
     */
    public static void parse(String[] args) {
        createCLIOptions();
        CommandLineParser parser = new DefaultParser();
        try {
            commandLine = parser.parse(options, args);
            afterParseChecks();            
        } catch (Exception e) {
            System.out.println(e.getMessage());            
            help(CLIExitCodes.getExceptionCode(e));
        }
    }
    
    public static void afterParseChecks() throws MissingOptionException{
    	 if (commandLine.hasOption("h")) {
             System.out.println("You passed help flag.");
             help(0);
         } else if (commandLine.hasOption("lv")) {
             printVersions();
         } else {
    		 checkRequiredOptions();
    	 }
    }

    /**
     * Lists the version and all loaded protocols  
     */
    private static void printVersions() {
        Map versions = VersionService.getMessagingVersions();
        Map<String, String> endpointVersions = (Map<String, String>) versions.get("endpointVersions");
        Map<String, String> serviceVersion = (Map<String, String>) versions.get("serviceVersion");

        if(serviceVersion != null) {
            System.out.print("REMREM Generate version ");
            for (String version: serviceVersion.values()) {
                System.out.println(version);
            }
        }
        if(endpointVersions != null) {
            System.out.println("Available endpoints");
            for (Map.Entry<String, String> entry : endpointVersions.entrySet()) {
                System.out.println(entry);
            }
        }
        exit(0);
    }

    public static void checkRequiredOptions() throws MissingOptionException {
    	OptionGroup[] groups = {typeGroup, contentGroup};
    	for(OptionGroup group : groups) {
    		ArrayList<Option> groupOptions = new ArrayList<Option>(group.getOptions());
    		boolean groupIsGiven = false;
    		for (Option option : groupOptions){
    			if (commandLine.hasOption(option.getOpt())) {
    				groupIsGiven = true;
    				break;
    			}
    		}
    		if (!groupIsGiven){
    			throw new MissingOptionException(groupOptions);
    		}
    	}
    }

    /**
     * Check if the CLI contain any options
     * 
     * @return true if options exists otherwise false
     */
    public static boolean hasParsedOptions() {
        if (commandLine == null)
            return false;
        return commandLine.getOptions().length > 0;
    }    
    
    public static void handleJarPath(){
        if (commandLine.hasOption("jp")) {
            String jarPath = commandLine.getOptionValue("jp");
            System.out.println("JarPath :: "+ jarPath);
            try {
            	JarLoaderUtility.loadJar(jarPath);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error while loading jars from the path mentioned, MESSAGE :: " + e.getMessage());
            }
        }
    }
}
