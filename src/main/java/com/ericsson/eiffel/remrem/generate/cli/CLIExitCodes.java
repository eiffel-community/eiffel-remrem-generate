package com.ericsson.eiffel.remrem.generate.cli;

import org.apache.commons.cli.MissingOptionException;

public class CLIExitCodes {
    public static int CLI_EXCEPTION=1;
    public static int CLI_MISSING_OPTION_EXCEPTION=2;
    public static int CLI_READ_FILE_FAILED=3;
    public static int READ_JSON_FROM_CONSOLE_FAILED=4;
    public static int HANDLE_JSON_STRING_FAILED=5;
    public static int MESSAGE_PROTOCOL_NOT_FOUND=6;
	
    public static int getExceptionCode(Exception e) {
        if (e instanceof MissingOptionException) {
            return CLI_MISSING_OPTION_EXCEPTION;
        }
        return 1;
    }
}
