/*
   @from 2023 w3xue.com 
*/
package com.klaxon.kserver.downloader;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoInfo {

    private String id;
    private List<Formats> formats;

    private String title;
    private String description;
    @JsonProperty("view_count")
    private int viewCount;
    private String uploader;
    @JsonProperty("uploader_id")
    private String uploaderId;
    @JsonProperty("like_count")
    private int likeCount;
    @JsonProperty("comment_count")
    private int commentCount;
    private List<String> tags;
    private String thumbnail;
    private int timestamp;
    private double duration;
    private List<Chapter> chapters;
    private Subtitles subtitles;
    @JsonProperty("http_headers")
    private HttpHeaders httpHeaders;
    @JsonProperty("webpage_url")
    private String webpageUrl;
    @JsonProperty("original_url")
    private String originalUrl;
    @JsonProperty("webpage_url_basename")
    private String webpageUrlBasename;
    @JsonProperty("webpage_url_domain")
    private String webpageUrlDomain;
    private String extractor;
    @JsonProperty("extractor_key")
    private String extractorKey;
    private String playlist;
    @JsonProperty("playlist_index")
    private Integer playlistIndex;
    private List<Thumbnails> thumbnails;
    @JsonProperty("display_id")
    private String displayId;
    private String fulltitle;
    @JsonProperty("duration_string")
    private String durationString;
    @JsonProperty("upload_date")
    private String uploadDate;
    @JsonProperty("requested_subtitles")
    private String requestedSubtitles;
    @JsonProperty("requested_formats")
    private List<RequestedFormats> requestedFormats;
    private String format;
    @JsonProperty("format_id")
    private String formatId;
    private String ext;
    private String protocol;
    private String language;
    @JsonProperty("format_note")
    private String formatNote;
    @JsonProperty("filesize_approx")
    private int filesizeApprox;
    private double tbr;
    private int width;
    private int height;
    private String resolution;
    private double fps;
    @JsonProperty("dynamic_range")
    private String dynamicRange;
    private String vcodec;
    private double vbr;
    @JsonProperty("stretched_ratio")
    private String stretchedRatio;
    @JsonProperty("aspect_ratio")
    private double aspectRatio;
    private String acodec;
    private double abr;
    private String asr;
    @JsonProperty("audio_channels")
    private String audioChannels;
    private int epoch;
    private String filename;
    private String urls;
    @JsonProperty("_type")
    private String Type;
    @JsonProperty("_version")
    private Version Version;
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setFormats(List<Formats> formats) {
        this.formats = formats;
    }
    public List<Formats> getFormats() {
        return formats;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }
    public int getViewCount() {
        return viewCount;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }
    public String getUploader() {
        return uploader;
    }

    public void setUploaderId(String uploaderId) {
        this.uploaderId = uploaderId;
    }
    public String getUploaderId() {
        return uploaderId;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
    public int getLikeCount() {
        return likeCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
    public int getCommentCount() {
        return commentCount;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    public List<String> getTags() {
        return tags;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
    public String getThumbnail() {
        return thumbnail;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }
    public int getTimestamp() {
        return timestamp;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }
    public double getDuration() {
        return duration;
    }

    public List<Chapter> getChapters() {
        return chapters;
    }
    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }

    public void setSubtitles(Subtitles subtitles) {
        this.subtitles = subtitles;
    }
    public Subtitles getSubtitles() {
        return subtitles;
    }

    public void setHttpHeaders(HttpHeaders httpHeaders) {
        this.httpHeaders = httpHeaders;
    }
    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public void setWebpageUrl(String webpageUrl) {
        this.webpageUrl = webpageUrl;
    }
    public String getWebpageUrl() {
        return webpageUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }
    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setWebpageUrlBasename(String webpageUrlBasename) {
        this.webpageUrlBasename = webpageUrlBasename;
    }
    public String getWebpageUrlBasename() {
        return webpageUrlBasename;
    }

    public void setWebpageUrlDomain(String webpageUrlDomain) {
        this.webpageUrlDomain = webpageUrlDomain;
    }
    public String getWebpageUrlDomain() {
        return webpageUrlDomain;
    }

    public void setExtractor(String extractor) {
        this.extractor = extractor;
    }
    public String getExtractor() {
        return extractor;
    }

    public void setExtractorKey(String extractorKey) {
        this.extractorKey = extractorKey;
    }
    public String getExtractorKey() {
        return extractorKey;
    }

    public void setPlaylist(String playlist) {
        this.playlist = playlist;
    }
    public String getPlaylist() {
        return playlist;
    }

    public Integer getPlaylistIndex() {
        return playlistIndex;
    }
    public void setPlaylistIndex(Integer playlistIndex) {
        this.playlistIndex = playlistIndex;
    }

    public void setFulltitle(String fulltitle) {
        this.fulltitle = fulltitle;
    }

    public void setThumbnails(List<Thumbnails> thumbnails) {
        this.thumbnails = thumbnails;
    }
    public List<Thumbnails> getThumbnails() {
        return thumbnails;
    }

    public void setDisplayId(String displayId) {
        this.displayId = displayId;
    }
    public String getDisplayId() {
        return displayId;
    }

    public void set_fulltitle(String fulltitle) {
        this.fulltitle = fulltitle;
    }
    public String getFulltitle() {
        return fulltitle;
    }

    public void setDurationString(String durationString) {
        this.durationString = durationString;
    }
    public String getDurationString() {
        return durationString;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }
    public String getUploadDate() {
        return uploadDate;
    }

    public void setRequestedSubtitles(String requestedSubtitles) {
        this.requestedSubtitles = requestedSubtitles;
    }
    public String getRequestedSubtitles() {
        return requestedSubtitles;
    }

    public void setRequestedFormats(List<RequestedFormats> requestedFormats) {
        this.requestedFormats = requestedFormats;
    }
    public List<RequestedFormats> getRequestedFormats() {
        return requestedFormats;
    }

    public void setFormat(String format) {
        this.format = format;
    }
    public String getFormat() {
        return format;
    }

    public void setFormatId(String formatId) {
        this.formatId = formatId;
    }
    public String getFormatId() {
        return formatId;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }
    public String getExt() {
        return ext;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    public String getProtocol() {
        return protocol;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
    public String getLanguage() {
        return language;
    }

    public void setFormatNote(String formatNote) {
        this.formatNote = formatNote;
    }
    public String getFormatNote() {
        return formatNote;
    }

    public void setFilesizeApprox(int filesizeApprox) {
        this.filesizeApprox = filesizeApprox;
    }
    public int getFilesizeApprox() {
        return filesizeApprox;
    }

    public void setTbr(double tbr) {
        this.tbr = tbr;
    }
    public double getTbr() {
        return tbr;
    }

    public void setWidth(int width) {
        this.width = width;
    }
    public int getWidth() {
        return width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
    public int getHeight() {
        return height;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }
    public String getResolution() {
        return resolution;
    }

    public void setFps(double fps) {
        this.fps = fps;
    }
    public double getFps() {
        return fps;
    }

    public void setDynamicRange(String dynamicRange) {
        this.dynamicRange = dynamicRange;
    }
    public String getDynamicRange() {
        return dynamicRange;
    }

    public void setVcodec(String vcodec) {
        this.vcodec = vcodec;
    }
    public String getVcodec() {
        return vcodec;
    }

    public void setVbr(double vbr) {
        this.vbr = vbr;
    }
    public double getVbr() {
        return vbr;
    }

    public void setStretchedRatio(String stretchedRatio) {
        this.stretchedRatio = stretchedRatio;
    }
    public String getStretchedRatio() {
        return stretchedRatio;
    }

    public void setAspectRatio(double aspectRatio) {
        this.aspectRatio = aspectRatio;
    }
    public double getAspectRatio() {
        return aspectRatio;
    }

    public void setAcodec(String acodec) {
        this.acodec = acodec;
    }
    public String getAcodec() {
        return acodec;
    }

    public void setAbr(double abr) {
        this.abr = abr;
    }
    public double getAbr() {
        return abr;
    }

    public void setAsr(String asr) {
        this.asr = asr;
    }
    public String getAsr() {
        return asr;
    }

    public void setAudioChannels(String audioChannels) {
        this.audioChannels = audioChannels;
    }
    public String getAudioChannels() {
        return audioChannels;
    }

    public void setEpoch(int epoch) {
        this.epoch = epoch;
    }
    public int getEpoch() {
        return epoch;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
    public String getFilename() {
        return filename;
    }

    public void setUrls(String urls) {
        this.urls = urls;
    }
    public String getUrls() {
        return urls;
    }

    public void setType(String Type) {
        this.Type = Type;
    }
    public String getType() {
        return Type;
    }

    public void setVersion(Version Version) {
        this.Version = Version;
    }
    public Version getVersion() {
        return Version;
    }

}