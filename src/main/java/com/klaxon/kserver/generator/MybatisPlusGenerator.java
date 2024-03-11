package com.klaxon.kserver.generator;

import java.io.IOException;

public class MybatisPlusGenerator {

    public static void main(String[] args) throws IOException {
        CodeGenerator.generate("account", "account");
        CodeGenerator.generate("media", "media_library");
        CodeGenerator.generate("webpage", "web_page");
    }

}
