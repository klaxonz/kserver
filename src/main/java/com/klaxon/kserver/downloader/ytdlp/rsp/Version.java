package com.klaxon.kserver.downloader.ytdlp.rsp;

import com.fasterxml.jackson.annotation.JsonProperty;
public class Version {

    private String version;
    @JsonProperty("current_git_head")
    private String currentGitHead;
    @JsonProperty("release_git_head")
    private String releaseGitHead;
    private String repository;

    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }

    public void setCurrentGitHead(String currentGitHead) {
        this.currentGitHead = currentGitHead;
    }
    public String getCurrentGitHead() {
        return currentGitHead;
    }

    public void setReleaseGitHead(String releaseGitHead) {
        this.releaseGitHead = releaseGitHead;
    }
    public String getReleaseGitHead() {
        return releaseGitHead;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }
    public String getRepository() {
        return repository;
    }

}