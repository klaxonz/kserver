package com.klaxon.kserver.downloader;

import cn.hutool.core.lang.Assert;
import org.junit.jupiter.api.Test;

class ShellCommandConverterTest {

    @Test
    public void testNoVarShell() {
        String[] commands = {
                "ls -a",
                "ls  -a",
                "ls -a "
        };
        for (String command : commands) {
            String[] convert = ShellCommandConverter.convert(command);
            Assert.equals(convert.length, 2);
            Assert.equals(convert[0], "ls");
            Assert.equals(convert[1], "-a");
        }
    }

    @Test
    public void testVarShell() {
        String command = "ls -a | grep $0";
        String[] convert = ShellCommandConverter.convert(command, "hello");
        Assert.equals(convert.length, 5);
        Assert.equals(convert[0], "ls");
        Assert.equals(convert[4], "hello");
    }

}