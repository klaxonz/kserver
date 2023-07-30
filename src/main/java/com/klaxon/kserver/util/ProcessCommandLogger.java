package com.klaxon.kserver.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ProcessCommandLogger {

    private static final Logger log = LoggerFactory.getLogger(ProcessCommandLogger.class);

    public static void printCommand(List<String> commands)  {
        String commandOutput = String.join(" ", commands);
        log.info("Execute command: {}", commandOutput);
    }
}
