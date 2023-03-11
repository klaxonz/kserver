package com.klaxon.kserver.util;

import java.io.*;
import java.nio.file.Files;

public class FileUtil {

    public static void mergeFile(File saveFile, File[] mergeFiles) throws IOException {
        byte[] buffer = new byte[16 * 1024];

        BufferedOutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(saveFile.toPath()));
        for (File file : mergeFiles) {
            BufferedInputStream inputStream = new BufferedInputStream(Files.newInputStream(file.toPath()));
            int readcount;
            while ((readcount = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, readcount);
            }
            inputStream.close();
        }
        outputStream.flush();
        outputStream.close();
    }

}
