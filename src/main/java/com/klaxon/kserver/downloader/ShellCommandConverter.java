package com.klaxon.kserver.downloader;

public class ShellCommandConverter {


    public static String[] convert(String command, String ...var) {
        String[] commands = command.split("\\s+");
        if (var.length == 0) {
            return commands;
        }

        String[] replacedCommands = new String[commands.length];
        int index = 0;
        for (int i = 0; i < commands.length; i++) {
            String cmd = commands[i];
            if (cmd.startsWith("$")) {
                replacedCommands[i] = var[index++];
            } else {
                replacedCommands[i] = cmd;
            }
        }

        return replacedCommands;
    }

}
