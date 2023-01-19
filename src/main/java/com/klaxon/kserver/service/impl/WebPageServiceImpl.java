package com.klaxon.kserver.service.impl;

import cn.hutool.core.net.url.UrlBuilder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.kiulian.downloader.Config;
import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.YoutubeCallback;
import com.github.kiulian.downloader.downloader.YoutubeProgressCallback;
import com.github.kiulian.downloader.downloader.request.RequestVideoFileDownload;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.downloader.response.Response;
import com.github.kiulian.downloader.model.Extension;
import com.github.kiulian.downloader.model.Filter;
import com.github.kiulian.downloader.model.videos.VideoDetails;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.github.kiulian.downloader.model.videos.formats.AudioFormat;
import com.github.kiulian.downloader.model.videos.formats.Format;
import com.github.kiulian.downloader.model.videos.formats.VideoFormat;
import com.github.kiulian.downloader.model.videos.formats.VideoWithAudioFormat;
import com.klaxon.kserver.entity.dao.Group;
import com.klaxon.kserver.entity.dao.Tag;
import com.klaxon.kserver.entity.dao.WebPage;
import com.klaxon.kserver.entity.dao.WebPageTag;
import com.klaxon.kserver.entity.dto.WebPageDTO;
import com.klaxon.kserver.entity.vo.WebPageDetail;
import com.klaxon.kserver.entity.vo.WebPageTagVO;
import com.klaxon.kserver.exception.BizCodeEnum;
import com.klaxon.kserver.exception.BizException;
import com.klaxon.kserver.mapper.GroupMapper;
import com.klaxon.kserver.mapper.TagMapper;
import com.klaxon.kserver.mapper.WebPageMapper;
import com.klaxon.kserver.mapper.WebPageTagMapper;
import com.klaxon.kserver.service.IWebPageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;


@Service("webPageService")
@Slf4j
public class WebPageServiceImpl implements IWebPageService {
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private WebPageMapper webPageMapper;
    @Autowired
    private WebPageTagMapper webPageTagMapper;

    private final static String faviconBaseUrl = "https://www.google.com/s2/favicons?domain=";

    @Override
    public void add(WebPageDTO webPageDTO) {
        String defaultGroupName = "默认";
        Long groupId = webPageDTO.getGroupId();
        if (Objects.isNull(groupId)) {
            Group defaultGroup = groupMapper.selectOne(new LambdaQueryWrapper<Group>().eq(Group::getGroupName, defaultGroupName));
            groupId = defaultGroup.getId();
        } else {
            Group group = groupMapper.selectById(groupId);
            if (Objects.isNull(group)) {
                throw new BizException(BizCodeEnum.GROUP_0020002);
            }
        }

        String url = webPageDTO.getUrl();
        Document document = null;
        String title = Strings.EMPTY;
        String description = Strings.EMPTY;
        try {
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            log.error("请求异常", e);
            title = url;
        }

        if (!Objects.isNull(document)) {
            title = document.title();
            Elements elements = document.select("meta[name=description]");
            if (elements.size() > 0) {
                description = elements.get(0).attr("content");
            }
        }
        UrlBuilder urlBuilder = UrlBuilder.of(url);
        String source = String.join("://", urlBuilder.getScheme(), urlBuilder.getHost());

        // 视频解析
        if (urlBuilder.getHost().equals("www.youtube.com")) {
            downloadVideo(urlBuilder);

        }

        WebPage webPage = new WebPage();
        webPage.setUrl(url);
        webPage.setGroupId(groupId);
        webPage.setIsStar("0");
        webPage.setTitle(title);
        webPage.setSource(source);
        webPage.setFavicon(faviconBaseUrl + url);
        webPage.setDescription(description);
        webPageMapper.insert(webPage);
    }

    private void downloadVideo(UrlBuilder urlBuilder) {
        // init downloader with default config
        YoutubeDownloader downloader = new YoutubeDownloader();
        // or configure after init
        Config config = downloader.getConfig();
        config.setMaxRetries(0);

        String videoId = urlBuilder.getQueryStr().split("=")[1];


        // async parsing
        RequestVideoInfo request = new RequestVideoInfo(videoId)
                .callback(new YoutubeCallback<VideoInfo>() {
                    @Override
                    public void onFinished(VideoInfo videoInfo) {
                        System.out.println("Finished parsing");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        System.out.println("Error: " + throwable.getMessage());
                    }
                })
                .async();
        Response<VideoInfo> response = downloader.getVideoInfo(request);
        VideoInfo video = response.data(); // will block thread

        // video details
        VideoDetails details = video.details();
        System.out.println(details.title());
        System.out.println(details.viewCount());
        details.thumbnails().forEach(image -> System.out.println("Thumbnail: " + image));

        // HLS url only for live videos and streams
        if (video.details().isLive()) {
            System.out.println("Live Stream HLS URL: " + video.details().liveUrl());
        }

        // get videos formats only with audio
        List<VideoWithAudioFormat> videoWithAudioFormats = video.videoWithAudioFormats();
        videoWithAudioFormats.forEach(it -> {
            System.out.println(it.audioQuality() + ", " + it.videoQuality() + " : " + it.url());
        });

        // get all videos formats (may contain better quality but without audio)
        List<VideoFormat> videoFormats = video.videoFormats();
        videoFormats.forEach(it -> {
            System.out.println(it.videoQuality() + " : " + it.url());
        });

        // get audio formats
        List<AudioFormat> audioFormats = video.audioFormats();
        audioFormats.forEach(it -> {
            System.out.println(it.audioQuality() + " : " + it.url());
        });

        // get best format
        video.bestVideoWithAudioFormat();
        video.bestVideoFormat();
        video.bestAudioFormat();

        // filtering formats
        List<Format> formats = video.findFormats(new Filter<Format>() {
            @Override
            public boolean test(Format format) {
                return format.extension() == Extension.WEBM;
            }
        });

        // itags can be found here - https://gist.github.com/sidneys/7095afe4da4ae58694d128b1034e01e2
        Format formatByItag = video.findFormatByItag(18); // return null if not found
        if (formatByItag != null) {
            System.out.println(formatByItag.url());
        }
        Format format = videoFormats.get(0);

        // async downloading with callback
        RequestVideoFileDownload downloadRequest = new RequestVideoFileDownload(format)
                .callback(new YoutubeProgressCallback<File>() {
                    @Override
                    public void onDownloading(int progress) {
                        System.out.printf("Downloaded %d%%\n", progress);
                    }

                    @Override
                    public void onFinished(File videoInfo) {
                        System.out.println("Finished file: " + videoInfo);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        System.out.println("Error: " + throwable.getLocalizedMessage());
                    }
                })
                .saveTo(new File("D:\\Temp\\youtube"))
                .async();
        Response<File> downloadResponse = downloader.downloadVideoFile(downloadRequest);
        File data = downloadResponse.data(); // will block current thread
        System.out.println(data);
    }

    @Override
    @Transactional
    public void addTags(WebPageTagVO webPageTagVO) {
        Long webPageId = webPageTagVO.getId();
        WebPage webPage = webPageMapper.selectById(webPageId);
        if (Objects.isNull(webPage)) {
            throw new BizException(BizCodeEnum.WEBPAGE_0010001);
        }
        List<WebPageTagVO.WebPageTagItem> tags = webPageTagVO.getTags();
        for (WebPageTagVO.WebPageTagItem webPageTagItem : tags) {
            Long tagId = webPageTagItem.getTagId();
            String tagName = webPageTagItem.getTagName();

            LambdaQueryWrapper<Tag> tagLambdaQueryWrapper = new LambdaQueryWrapper<>();
            if (!Objects.isNull(tagId)) {
                tagLambdaQueryWrapper.eq(Tag::getId, tagId);
            }
            if (StringUtils.isNotBlank(tagName)) {
                tagLambdaQueryWrapper.eq(Tag::getTagName, tagName);
            }
            Tag targetTag = tagMapper.selectOne(tagLambdaQueryWrapper);
            if (Objects.isNull(targetTag)) {
                if (Objects.isNull(tagId) && StringUtils.isNotBlank(tagName)) {
                    Tag sourceTarget = new Tag();
                    sourceTarget.setTagName(tagName);
                    tagMapper.insert(sourceTarget);
                    tagId = sourceTarget.getId();
                } else {
                    continue;
                }
            } else {
                tagId = targetTag.getId();
            }

            LambdaQueryWrapper<WebPageTag> webPageTagLambdaQueryWrapper = new LambdaQueryWrapper<>();
            webPageTagLambdaQueryWrapper.eq(WebPageTag::getWebpageId, webPageId).eq(WebPageTag::getTagId, tagId);
            WebPageTag webPageTag = webPageTagMapper.selectOne(webPageTagLambdaQueryWrapper);
            if (!Objects.isNull(webPageTag)) {
                continue;
            }
            WebPageTag sourceWebPageTag = new WebPageTag();
            sourceWebPageTag.setTagId(tagId);
            sourceWebPageTag.setWebpageId(webPageId);
            webPageTagMapper.insert(sourceWebPageTag);
        }
    }

    @Override
    public void removeTags(WebPageTagVO webPageTagVO) {
        Long webPageId = webPageTagVO.getId();
        WebPage webPage = webPageMapper.selectById(webPageId);
        if (Objects.isNull(webPage)) {
            throw new BizException(BizCodeEnum.WEBPAGE_0010001);
        }

        List<WebPageTagVO.WebPageTagItem> tags = webPageTagVO.getTags();
        for (WebPageTagVO.WebPageTagItem webPageTagItem : tags) {
            Long tagId = webPageTagItem.getTagId();
            if (Objects.isNull(tagId)) {
                continue;
            }
            LambdaQueryWrapper<Tag> tagLambdaQueryWrapper = new LambdaQueryWrapper<Tag>().eq(Tag::getId, tagId);
            Tag targetTag = tagMapper.selectOne(tagLambdaQueryWrapper);
            if (Objects.isNull(targetTag)) {
                continue;
            }
            tagId = targetTag.getId();
            LambdaQueryWrapper<WebPageTag> webPageTagLambdaQueryWrapper = new LambdaQueryWrapper<>();
            webPageTagLambdaQueryWrapper.eq(WebPageTag::getWebpageId, webPageId).eq(WebPageTag::getTagId, tagId);
            webPageTagMapper.delete(webPageTagLambdaQueryWrapper);
        }
    }

    @Override
    public void changeGroup(Long webpageId, Long groupId) {
        WebPage webPage = webPageMapper.selectById(webpageId);
        if (Objects.isNull(webPage)) {
            throw new BizException(BizCodeEnum.WEBPAGE_0010001);
        }
        Group group = groupMapper.selectById(groupId);
        if (Objects.isNull(group)) {
            throw new BizException(BizCodeEnum.GROUP_0020002);
        }
        webPage.setGroupId(groupId);
        webPageMapper.updateById(webPage);
    }

    @Override
    public IPage<WebPage> search(String type, String question, Integer page) {
        LambdaQueryWrapper<WebPage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(WebPage::getCreateTime);
        if (StringUtils.equals(type, "title")) {
            queryWrapper.like(WebPage::getTitle, question);
        } else if (StringUtils.equals(type, "content")) {
            queryWrapper.like(WebPage::getDescription, question);
        } else {
            queryWrapper.like(WebPage::getTitle, question)
                    .or()
                    .like(WebPage::getDescription, question);
        }
        IPage<WebPage> searchPage = new Page<>(page, 50);

        return webPageMapper.selectPage(searchPage, queryWrapper);
    }

    @Override
    public WebPage getOne(Long id) {
        return webPageMapper.selectById(id);
    }

    @Override
    public WebPageDetail detail() {
        // all count
        LambdaQueryWrapper<WebPage> allQueryWrapper = new LambdaQueryWrapper<>();
        allQueryWrapper.orderByDesc(WebPage::getCreateTime);
        Long allTotal = webPageMapper.selectCount(allQueryWrapper);

        // star count
        LambdaQueryWrapper<WebPage> starQueryWrapper = new LambdaQueryWrapper<>();
        starQueryWrapper
                .eq(WebPage::getIsStar, "1")
                .orderByDesc(WebPage::getCreateTime);
        Long starTotal = webPageMapper.selectCount(starQueryWrapper);

        // today count
        LambdaQueryWrapper<WebPage> todayQueryWrapper = new LambdaQueryWrapper<>();
        todayQueryWrapper
                .apply("to_days(create_time) = to_days(now())")
                .orderByDesc(WebPage::getCreateTime);
        Long todayTotal = webPageMapper.selectCount(todayQueryWrapper);

        WebPageDetail webPageDetail = new WebPageDetail();
        webPageDetail.setAllTotal(allTotal);
        webPageDetail.setStarTotal(starTotal);
        webPageDetail.setTodayTotal(todayTotal);

        return webPageDetail;
    }

    @Override
    public IPage<WebPage> getAll(Integer page) {
        IPage<WebPage> searchPage = new Page<>(page, 50);
        LambdaQueryWrapper<WebPage> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByDesc(WebPage::getCreateTime);
        return webPageMapper.selectPage(searchPage, lambdaQueryWrapper);
    }

    @Override
    public IPage<WebPage> getStar(Integer page) {
        IPage<WebPage> searchPage = new Page<>(page, 50);
        LambdaQueryWrapper<WebPage> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(WebPage::getIsStar, "1")
                .orderByDesc(WebPage::getCreateTime);
        return webPageMapper.selectPage(searchPage, lambdaQueryWrapper);
    }

    @Override
    public IPage<WebPage> getToday(Integer page) {
        IPage<WebPage> searchPage = new Page<>(page, 50);
        LambdaQueryWrapper<WebPage> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .apply("to_days(create_time) = to_days(now())")
                .orderByDesc(WebPage::getCreateTime);
        return webPageMapper.selectPage(searchPage, lambdaQueryWrapper);
    }

    @Override
    public void remove(Long id) {
        webPageMapper.deleteById(id);
    }

    @Override
    public void batchRemove(List<Long> webpageIds) {
        webPageMapper.deleteBatchIds(webpageIds);
    }
}
