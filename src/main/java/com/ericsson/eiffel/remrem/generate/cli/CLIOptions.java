package com.ericsson.eiffel.remrem.generate.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

public class CLIOptions {
    private static CommandLine commandLine = null;
    private static Options options = null;

    public static CommandLine getCommandLine() {
        return commandLine;
    }

    public static void setCommandLine(CommandLine commandLine) {
        CLIOptions.commandLine = commandLine;
    }

    /**
     * Creates the options needed by command line interface
     * 
     * @return the options this CLI can handle
     */
    public static Options createCLIOptions() {
        options = new Options();
        Option msgTypeOpt = new Option("t", "message_type", true, "message type");
        msgTypeOpt.setRequired(true);
        options.addOption(msgTypeOpt);
        options.addOption("h", "help", false, "show help.");
        options.addOption("r", "response_file", true, "file to store the response in, optional");
        options.addOption("d", "debug", false, "enable debug traces");
        options.addOption("mp", "messaging_protocol", true,
                "name of messaging protocol to be used, e.g. eiffel3, semantics");

        OptionGroup group = new OptionGroup();
        group.addOption(new Option("f", "content_file", true, "message content file"));
        group.addOption(new Option("json", "json_content", true, "json content"));
        group.setRequired(true);
        options.addOptionGroup(group);

        return options;
    }

    /**
     * Prints the help for this application and exits.
     */
    public static void help() {
        // This prints out some help
        HelpFormatter formater = new HelpFormatter();
        formater.printHelp("java -jar", options);
        System.exit(1);
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
            help();
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
