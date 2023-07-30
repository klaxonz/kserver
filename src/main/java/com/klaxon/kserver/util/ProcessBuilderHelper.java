package com.klaxon.kserver.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ProcessBuilderHelper {

    public static BufferedReader getReader(Process process) {
        InputStream inputStream = process.getInputStream();
        return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    }

}
