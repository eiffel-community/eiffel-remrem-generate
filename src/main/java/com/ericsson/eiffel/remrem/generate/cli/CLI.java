package com.ericsson.eiffel.remrem.generate.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;
import com.ericsson.eiffel.remrem.generate.config.PropertiesConfig;
import com.ericsson.eiffel.remrem.protocol.MsgService;
import com.ericsson.eiffel.remrem.semantics.SemanticsService;
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

    public CLI(MsgService[] msgServices) {
        super();
        this.msgServices = msgServices;
	}
    
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
        CLIOptions.handleJarPath();
        if (commandLine.hasOption("h")) {
            System.out.println("You passed help flag.");
            CLIOptions.help(1);
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
     * @return file contents if successful 
     */
    private String readFileContent(String filePath) {
        try {
            byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
            return new String(fileBytes);
        } catch (IOException e) {
            System.out.println("Unable to read File content from file path " + filePath);
            CLIOptions.exit(CLIExitCodes.CLI_READ_FILE_FAILED);
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
                e.printStackTrace(System.out);
                CLIOptions.exit(CLIExitCodes.READ_JSON_FROM_CONSOLE_FAILED);
                // In unit tests we do not do system exit but it still needs
                // to return here.
                return;
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
            e.printStackTrace(System.out);
            CLIOptions.exit(CLIExitCodes.HANDLE_JSON_STRING_FAILED);
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

        boolean testMode = Boolean.getBoolean(PropertiesConfig.TEST_MODE);
    	if (testMode && msgServices.length>0)
    		return msgServices[0];

    	System.out.println("No protocol service has been found registered.");        
        CLIOptions.exit(CLIExitCodes.MESSAGE_PROTOCOL_NOT_FOUND);
        return null;
    }
}