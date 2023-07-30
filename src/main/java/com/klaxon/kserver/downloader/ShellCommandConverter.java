package com.klaxon.kserver.downloader;

import java.util.List;
import java.util.Objects;

public class ShellCommandConverter {


    public static String[] convert(String command, List<String> var) {
        String[] commands = command.split("\\s+");
        if (Objects.isNull(var) || var.isEmpty()) {
            return commands;
        }

        String[] replacedCommands = new String[commands.length];
        int index = 0;
        for (int i = 0; i < commands.length; i++) {
            String cmd = commands[i];
            if (cmd.startsWith("$")) {
                replacedCommands[i] = var.get(index++);
            } else {
                replacedCommands[i] = cmd;
            }
        }

        return replacedCommands;
    }

}
