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
import com.klaxon.kserver.constants.WebPageConstant;
import com.klaxon.kserver.downloader.HttpDownloader;
import com.klaxon.kserver.entity.dao.Group;
import com.klaxon.kserver.entity.dao.Tag;
import com.klaxon.kserver.entity.dao.WebPage;
import com.klaxon.kserver.entity.dao.WebPageTag;
import com.klaxon.kserver.entity.dto.WebPageDto;
import com.klaxon.kserver.entity.vo.WebPageDetail;
import com.klaxon.kserver.entity.vo.WebPageTagVo;
import com.klaxon.kserver.exception.BizCodeEnum;
import com.klaxon.kserver.exception.BizException;
import com.klaxon.kserver.mapper.GroupMapper;
import com.klaxon.kserver.mapper.TagMapper;
import com.klaxon.kserver.mapper.WebPageMapper;
import com.klaxon.kserver.mapper.WebPageTagMapper;
import com.klaxon.kserver.service.IWebPageService;
import com.klaxon.kserver.util.ThreadLocalHolder;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Service("webPageService")
@Slf4j
public class WebPageServiceImpl implements IWebPageService {

    private static final String DEFAULT_FILE_STORE_DIRECTORY = "/Users/kail/Downloads";

    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private WebPageMapper webPageMapper;
    @Autowired
    private WebPageTagMapper webPageTagMapper;

    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Override
    public void add(WebPageDto webPageDto) {
        String defaultGroupName = "??????";
        Long groupId = webPageDto.getGroupId();
        if (Objects.isNull(groupId)) {
            Group defaultGroup = groupMapper.selectOne(new LambdaQueryWrapper<Group>().eq(Group::getGroupName, defaultGroupName));
            groupId = defaultGroup.getId();
        } else {
            Group group = groupMapper.selectById(groupId);
            if (Objects.isNull(group)) {
                throw new BizException(BizCodeEnum.GROUP_0020002);
            }
        }

        String url = webPageDto.getUrl();
        Document document = null;
        String title = Strings.EMPTY;
        String description = Strings.EMPTY;
        try {
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            log.error("????????????", e);
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

        // ????????????
        if (urlBuilder.getHost().equals("www.youtube.com")) {
            executorService.submit(() -> {
                downloadVideo(urlBuilder);
            });
        }

        WebPage webPage = new WebPage();
        webPage.setUserId(ThreadLocalHolder.get().getId());
        webPage.setUrl(url);
        webPage.setGroupId(groupId);
        webPage.setIsStar("0");
        webPage.setTitle(title);
        webPage.setSource(source);
        webPage.setFavicon(WebPageConstant.faviconBaseUrl + url);
        webPage.setDescription(description);
        webPageMapper.insert(webPage);
    }

    private void downloadVideo(UrlBuilder urlBuilder) {
        // init downloader with default config
        YoutubeDownloader downloader = new YoutubeDownloader();
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
        VideoInfo video = response.data();

        // get best format
        video.bestVideoWithAudioFormat();
        video.bestAudioFormat();
        Format format = video.bestVideoFormat();
        HttpDownloader httpDownloader = new HttpDownloader();
        try {
            httpDownloader.download(format.url(), new File(DEFAULT_FILE_STORE_DIRECTORY + "/2023012421065600.mp4"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void addTags(WebPageTagVo webPageTagVO) {
        Long webPageId = webPageTagVO.getId();
        WebPage webPage = webPageMapper.selectById(webPageId);
        if (Objects.isNull(webPage)) {
            throw new BizException(BizCodeEnum.WEBPAGE_0010001);
        }
        List<WebPageTagVo.WebPageTagItem> tags = webPageTagVO.getTags();
        for (WebPageTagVo.WebPageTagItem webPageTagItem : tags) {
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
    public void removeTags(WebPageTagVo webPageTagVO) {
        Long webPageId = webPageTagVO.getId();
        WebPage webPage = webPageMapper.selectById(webPageId);
        if (Objects.isNull(webPage)) {
            throw new BizException(BizCodeEnum.WEBPAGE_0010001);
        }

        List<WebPageTagVo.WebPageTagItem> tags = webPageTagVO.getTags();
        for (WebPageTagVo.WebPageTagItem webPageTagItem : tags) {
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
        queryWrapper
                .eq(WebPage::getUserId, ThreadLocalHolder.get().getId())
                .orderByDesc(WebPage::getCreateTime);
        if (StringUtils.equals(type, "title")) {
            queryWrapper.like(WebPage::getTitle, question);
        }
        else if (StringUtils.equals(type, "content")) {
            queryWrapper.like(WebPage::getDescription, question);
        }
        else {
            queryWrapper.like(WebPage::getTitle, question)
                    .or()
                    .like(WebPage::getDescription, question);
        }
        IPage<WebPage> searchPage = new Page<>(page, 50);

        return webPageMapper.selectPage(searchPage, queryWrapper);
    }

    @Override
    public WebPage getOne(Long id) {
        return webPageMapper.selectOne(new LambdaQueryWrapper<WebPage>()
                .eq(WebPage::getUserId, ThreadLocalHolder.get().getId())
                .eq(WebPage::getId, id));
    }

    @Override
    public WebPageDetail detail() {
        // all count
        LambdaQueryWrapper<WebPage> allQueryWrapper = new LambdaQueryWrapper<>();
        allQueryWrapper
                .eq(WebPage::getUserId, ThreadLocalHolder.get().getId())
                .orderByDesc(WebPage::getCreateTime);
        Long allTotal = webPageMapper.selectCount(allQueryWrapper);

        // star count
        LambdaQueryWrapper<WebPage> starQueryWrapper = new LambdaQueryWrapper<>();
        starQueryWrapper
                .eq(WebPage::getUserId, ThreadLocalHolder.get().getId())
                .eq(WebPage::getIsStar, "1")
                .orderByDesc(WebPage::getCreateTime);
        Long starTotal = webPageMapper.selectCount(starQueryWrapper);

        // today count
        LambdaQueryWrapper<WebPage> todayQueryWrapper = new LambdaQueryWrapper<>();
        todayQueryWrapper
                .eq(WebPage::getUserId, ThreadLocalHolder.get().getId())
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
        lambdaQueryWrapper
                .eq(WebPage::getUserId, ThreadLocalHolder.get().getId())
                .orderByDesc(WebPage::getCreateTime);

        return webPageMapper.selectPage(searchPage, lambdaQueryWrapper);
    }

    @Override
    public IPage<WebPage> getStar(Integer page) {
        IPage<WebPage> searchPage = new Page<>(page, 50);
        LambdaQueryWrapper<WebPage> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(WebPage::getIsStar, "1")
                .eq(WebPage::getUserId, ThreadLocalHolder.get().getId())
                .orderByDesc(WebPage::getCreateTime);
        return webPageMapper.selectPage(searchPage, lambdaQueryWrapper);
    }

    @Override
    public IPage<WebPage> getToday(Integer page) {
        IPage<WebPage> searchPage = new Page<>(page, 50);
        LambdaQueryWrapper<WebPage> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(WebPage::getUserId, ThreadLocalHolder.get().getId())
                .apply("to_days(create_time) = to_days(now())")
                .orderByDesc(WebPage::getCreateTime);
        return webPageMapper.selectPage(searchPage, lambdaQueryWrapper);
    }

    @Override
    public void remove(Long id) {
        webPageMapper.delete(new LambdaQueryWrapper<WebPage>()
                .eq(WebPage::getUserId, ThreadLocalHolder.get().getId())
                .eq(WebPage::getId, id));
    }

    @Override
    public void batchRemove(List<Long> webpageIds) {
        webPageMapper.delete(new LambdaQueryWrapper<WebPage>()
                .eq(WebPage::getUserId, ThreadLocalHolder.get().getId())
                .in(WebPage::getId, webpageIds));
    }
}
