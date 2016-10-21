package com.ericsson.eiffel.remrem.generate.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.DefaultApplicationArguments;

import com.ericsson.eiffel.remrem.semantics.SemanticsService;
import com.ericsson.eiffel.remrem.shared.MsgService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

/**
 * Class for interpreting the passed arguments from command line. Parse method
 * returns true, meaning we need to start the service afterwards, if no argument
 * is given. The same method returns false, meaning we do not start the service
 * afterwards, if any argument is given. If an argument is given that it is not
 * recognized we print help.
 * 
 * @author evasiba
 *
 */
@Component
@ComponentScan(basePackages = "com.ericsson.eiffel.remrem")
public class CLI implements CommandLineRunner {
    @Autowired
    private MsgService[] msgServices;

    @Override
    public void run(String... args) throws Exception {
        if (CLIOptions.hasParsedOptions())
            handleOptions();
    }
    /**
     * @param commandLine
     */
    private void handleLogging(CommandLine commandLine) {
        if (!commandLine.hasOption("-d")) {
            // Eiffel 3 messaging logs to stdout but since we also write
            // to stdout we need to turn off logging unless specified by the
            // user
            System.setProperty("logging.level.root", "OFF");
            Logger log = (Logger) LoggerFactory.getLogger("ROOT");
            log.setLevel(Level.OFF);
        }
    }

    /**
     * Delegates actions depending on the passed arguments
     * 
     * @param commandLine
     *            command line arguments
     */
    private void handleOptions() {
        CommandLine commandLine = CLIOptions.getCommandLine();
        handleLogging(commandLine);
        if (commandLine.hasOption("h")) {
            System.out.println("You passed help flag.");
            CLIOptions.help();
        } else if (commandLine.hasOption("f")) {
            handleFileArgs(commandLine);
        } else if (commandLine.hasOption("json")) {
            handleJsonArgs(commandLine);
        }
    }

    /**
     * Reads the content from the given file and sends it to message service
     * 
     * @param commandLine
     */
    private void handleFileArgs(CommandLine commandLine) {
        String filePath = commandLine.getOptionValue("f");
        String jsonContent = readFileContent(filePath);
        handleJsonString(jsonContent, commandLine);
    }

    /**
     * Read file content as String
     * 
     * @param filePath
     * @return file contents if not successful 
     */
    private String readFileContent(String filePath) {
        try {
            byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
            return new String(fileBytes);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-2);
        }
        return null;
    }

    /**
     * Read passed json string from command line and sends it to message service
     * 
     * @param commandLine
     */
    private void handleJsonArgs(CommandLine commandLine) {
        String jsonContent = commandLine.getOptionValue("json");
        if (jsonContent.equals("-")) {
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(System.in));
            try {
                jsonContent = bufferReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(5);
            }

        }
        handleJsonString(jsonContent, commandLine);
    }

    /**
     * Send the given json string to message service
     * 
     * @param msgType
     *            the Eiffel message type
     * @param filePath
     *            the file path where the message content resides
     * @param responseFilePath
     *            the file path where to store the prepared message, stdout if
     *            null
     */
    private void handleJsonString(String jsonString, CommandLine commandLine) {
        String responseFilePath = null;
        if (commandLine.hasOption("r"))
            responseFilePath = commandLine.getOptionValue("r");
        String msgType = handleMsgTypeArgs(commandLine);

        try {
            JsonParser parser = new JsonParser();
            JsonObject jsonContent = parser.parse(jsonString).getAsJsonObject();
            MsgService msgService = getMessageService(commandLine);
            String returnJsonStr = msgService.generateMsg(msgType, jsonContent);
            returnJsonStr = "[" + returnJsonStr + "]";
            if (responseFilePath != null) {
                try (PrintWriter out = new PrintWriter(responseFilePath)) {
                    out.println(returnJsonStr);
                }
            } else {
                System.out.println(returnJsonStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private String handleMsgTypeArgs(CommandLine commandLine) {
        String msgType = commandLine.getOptionValue("t").toLowerCase(Locale.ROOT);
        Pattern p = Pattern.compile("(.*)event");
        Matcher m = p.matcher(msgType);
        if (m.matches()) {
            return m.group(1);
        }
        return msgType;
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

        System.out.println("No protocol service has been found registered.");
        System.exit(-3);
        return null;
    }
}