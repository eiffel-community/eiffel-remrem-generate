package com.ericsson.eiffel.remrem.generate.cli;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.tomcat.util.http.fileupload.FileUtils;

import com.ericsson.eiffel.remrem.semantics.SemanticsService;
import com.ericsson.eiffel.remrem.shared.MsgService;
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
	 * @return
	 */
	private static Options createCLIOptions() {
		Options options = new Options();
		options.addOption("h", "help", false, "show help.");
		options.addOption("f", "content_file", true, "message content file");
		options.addOption("t", "message_type", true, "message type, mandatory if -f is given");
//		options.addOption("r", "response_file", true, "file to store the response in");
		return options;
	}

	/**
	 * Prints the help for this application and exits.
	 * @param options
	 */
	private static void help(Options options) {
		// This prints out some help
		HelpFormatter formater = new HelpFormatter();
		formater.printHelp("java -jar", options);
		System.exit(0);
	}

	/**
	 * Parse the given arguments
	 * @param args
	 * @return
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
				String msgType = commandLine.getOptionValue("t");
				handleContentFile(msgType, filePath);
			}
		} catch (Exception e) {
			help(options);
		}
		return startService;
	}
	
	public void handleContentFile(String msgType, String filePath) {
		JsonParser parser = new JsonParser();
		MsgService msgService = new SemanticsService();
		try {
			byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
			String fileContent = new String(fileBytes);
			JsonObject bodyJson = parser.parse(fileContent).getAsJsonObject();
			JsonElement returnJson = parser.parse(msgService.generateMsg(msgType, bodyJson));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
