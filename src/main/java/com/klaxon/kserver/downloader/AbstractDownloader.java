package com.klaxon.kserver.downloader;

import java.io.File;
import java.io.IOException;

public abstract class AbstractDownloader {

    public abstract void download(String url) throws IOException;

    public abstract void download(String url, File destination) throws IOException;

    public abstract void parse();

}
