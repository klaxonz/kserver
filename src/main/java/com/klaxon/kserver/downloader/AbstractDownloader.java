package com.klaxon.kserver.downloader;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

public abstract class AbstractDownloader {

    abstract void download(String url);

    public abstract void download(String url, File destination) throws IOException;

}
