package com.klaxon.kserver.module.media.subscribe.task;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.klaxon.kserver.module.media.extractor.MovieMetaExtractor;
import com.klaxon.kserver.module.media.manager.MovieManager;
import com.klaxon.kserver.module.media.mapper.MovieShareMapper;
import com.klaxon.kserver.module.media.model.entity.MovieShare;
import com.klaxon.kserver.module.media.subscribe.downloader.adrive.AdriveDownloader;
import com.klaxon.kserver.module.media.subscribe.provider.MetaProvider;
import com.klaxon.kserver.module.media.subscribe.provider.MovieItem;
import com.klaxon.kserver.module.media.subscribe.resource.ResourceProvider;
import com.uwetrottmann.tmdb2.entities.Movie;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class SubscribeTask {

    @Resource
    private MovieMetaExtractor movieMetaExtractor;
    @Resource
    private MovieManager movieManager;
    @Resource
    private MovieShareMapper movieShareMapper;
    @Resource
    private List<MetaProvider> metaProviders;
    @Resource
    private List<ResourceProvider> resourceProviders;
    @Resource
    private AdriveDownloader adriveDownloader;

    @Scheduled(cron = "0 */10 0 * * ?")
    public void subscribe() {
        log.info("Start run task to scrape latest movie");


        List<MovieItem> movieItems = Lists.newArrayList();
        for (MetaProvider metaProvider : metaProviders) {
            if (!metaProvider.isSupport()) {
                continue;
            }
            movieItems.addAll(metaProvider.parse());
        }

        for (MovieItem movieItem : movieItems) {
            ExtractInfo extractInfo = null;
            try {
                // find meta info from TMDB
                Movie tmdbMovie = movieMetaExtractor.extract(movieItem.getTitle(), movieItem.getRelease());
                if (Objects.isNull(tmdbMovie)) {
                    log.info("find movie information from TMDB failed, movie name: {}", movieItem.getTitle());
                    continue;
                }

                // save movie meta info to database
                com.klaxon.kserver.module.media.model.entity.Movie movie = movieManager.saveMovie(tmdbMovie);

                List<String> cloudDiskUrls = Lists.newArrayList();
                for (ResourceProvider resourceProvider : resourceProviders) {
                    extractInfo = resourceProvider.parse(movieItem.getTitle(), movieItem.getRelease());
                    if (Objects.isNull(extractInfo)) {
                        log.info("[{}] find share link failed, movie name: {}", resourceProvider.getName(), movieItem.getTitle());
                        continue;
                    }
                    List<String> resourceProviderCloudDiskUrls = extractInfo.getCloudDiskUrls();
                    cloudDiskUrls.addAll(resourceProviderCloudDiskUrls);

                    for (String cloudDiskUrl : resourceProviderCloudDiskUrls) {
                        // check exists
                        MovieShare movieShare = movieShareMapper.selectOne(new LambdaQueryWrapper<MovieShare>()
                                .eq(MovieShare::getMovieId, movie.getId()).eq(MovieShare::getShareUrl, cloudDiskUrl));
                        if (Objects.nonNull(movieShare)) {
                            continue;
                        }

                        movieShare = new MovieShare();
                        movieShare.setMovieId(movie.getId());
                        movieShare.setShareUrl(cloudDiskUrl);
                        movieShare.setRefererUrl(extractInfo.resourceUrl);
                        movieShareMapper.insert(movieShare);
                    }
                }

                if (!cloudDiskUrls.isEmpty()) {
                    adriveDownloader.saveSharedLink(cloudDiskUrls.get(0));
                }

            } catch (Exception e) {
                if (Objects.nonNull(extractInfo)) {
                    log.error("save error： {}", JSONUtil.toJsonStr(extractInfo), e);
                } else {
                    log.error("save error：", e);
                }
            }
        }

        log.info("Run task to scrape latest movie finnish");
    }

    @Builder
    @Getter
    @Setter
    public static class ExtractInfo {
        private String name;
        private String resourceUrl;
        private List<String> cloudDiskUrls;
    }

}
