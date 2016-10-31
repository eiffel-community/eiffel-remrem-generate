package com.ericsson.eiffel.remrem.generate.cli;

import java.util.ArrayList;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

import com.ericsson.eiffel.remrem.generate.config.PropertiesConfig;

public class CLIOptions {
    private static CommandLine commandLine = null;
    private static Options options = null;
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
        OptionGroup typeGroup = new OptionGroup();
        Option msgTypeOpt = new Option("t", "message_type", true, "message type");
        Option helpOpt = new Option("h", "help", false, "show help.");
        typeGroup.addOption(helpOpt);
        typeGroup.addOption(msgTypeOpt);
        typeGroup.setRequired(true);
        options.addOption(msgTypeOpt);        
        options.addOption("r", "response_file", true, "file to store the response in, optional");
        options.addOption("d", "debug", false, "enable debug traces");
        options.addOption("mp", "messaging_protocol", true,
                "name of messaging protocol to be used, e.g. eiffel3, semantics");

        OptionGroup group = new OptionGroup();
        group.addOption(new Option("f", "content_file", true, "message content file"));
        group.addOption(new Option("json", "json_content", true, "json content"));
        group.addOption(helpOpt);
        group.setRequired(true);
        options.addOptionGroup(group);

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
        } catch (Exception e) {
            System.out.println(e.getMessage());            
            help(CLIExitCodes.getExceptionCode(e));
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
}
