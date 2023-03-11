package com.klaxon.kserver.util;

import cn.hutool.core.io.FileUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class FilePathUtil {


    @NotNull
    public static String getTempFilePath(File targetFile, int index) {
        String extName = "." + FileUtil.extName(targetFile);
        String fileName = targetFile.getName().substring(0, targetFile.getName().lastIndexOf(extName));
        String tempFileName = fileName + "_" + index + extName;
        return targetFile.getParent() + "\\" + tempFileName;
    }

    @NotNull
    public static String getFileNameByTitle(String title) {
        return title.replaceAll("[/\\\\:*?|]", " ").replaceAll("[\"<>]", "'");
    }

}
