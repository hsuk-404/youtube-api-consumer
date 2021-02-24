package com.hsuk.video.data.service;

import com.hsuk.video.data.config.ApiConfigProperties;
import com.hsuk.video.data.dto.VideoDto;
import com.hsuk.video.data.dto.VideoRegionDto;
import com.hsuk.video.data.dto.youtubeApi.ApiResponse;
import com.hsuk.video.data.dto.youtubeApi.Items;
import com.hsuk.video.data.dto.youtubeApi.Snippet;
import com.hsuk.video.data.utils.PropertyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
public class YoutubeDataProcessor {

    private final ApiConfigProperties configProp;
    private final RestTemplate restTemplate;
    private final VideoService videoService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private final AtomicInteger totalCounter = new AtomicInteger(0);
    private final AtomicInteger addCounter = new AtomicInteger(0);
    private final AtomicInteger updateCounter = new AtomicInteger(0);
    private List<String> regionCodesList;

    public YoutubeDataProcessor(ApiConfigProperties configProp, RestTemplate restTemplate, VideoService videoService) {
        this.configProp = configProp;
        this.restTemplate = restTemplate;
        this.videoService = videoService;
    }

    @PostConstruct
    public void init() {
        if (StringUtils.isNotBlank(configProp.getRegionCode())) {
            regionCodesList = Arrays.stream(configProp.getRegionCode().split(",")).collect(Collectors.toList());
        }
    }

    private String getUri(String currentRegionCode, String currentPageToken) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("key", configProp.getKey());
        params.add("part", configProp.getPart());
        params.add("type", "video");
        params.add("chart", configProp.getChart());
        params.add("maxResults", configProp.getMaxResultPerRequest());
        params.add("regionCode", currentRegionCode);
        if (StringUtils.isNotBlank(currentPageToken)) {
            params.add("pageToken", currentPageToken);
        }
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme(configProp.getScheme()).host(configProp.getHost())
                .queryParams(params).build();
        return uriComponents.toUriString();
    }

    //Runs every day
    @Scheduled(fixedRate = 86_400_000, initialDelay = 5_000)
    public void processAPI() {
        for (String currentRegionCode : regionCodesList) {
            String currentPageToken = "";
            int currentPage = 1;
            log.info("Current Region [{}]", currentRegionCode);

            while (true) {
                try {
                    String requestURI = getUri(currentRegionCode, currentPageToken);
                    log.info("Requesting videos for region code [{}] using URL {}.", currentRegionCode, requestURI);
                    ApiResponse response = restTemplate.getForObject(requestURI, ApiResponse.class);
                    if (response == null) {
                        log.error("Could not retrieve data from youtube API.");
                        break;
                    }
                    currentPageToken = response.getNextPageToken();
                    List<Items> items = response.getItems();

                    int totalPages = Integer.parseInt(response.getPageInfo().getTotalResults()) /
                            Integer.parseInt(response.getPageInfo().getResultsPerPage());


                    log.info("Processing page {} out of {} for region [{}].", currentPage, totalPages, currentRegionCode);

                    // Parse the response from Youtube API
                    if (items != null) {
                        parseResponse(items, currentRegionCode);
                    }

                    log.info("Processed page {} out of {} for region [{}].", currentPage, totalPages, currentRegionCode);
                    log.info("Next page token : {}", currentPageToken);

                    currentPage++;

                    if (currentPage > totalPages || StringUtils.isBlank(currentPageToken)) {
                        log.info("Finished processing [{}]", currentRegionCode);
                        break;
                    }

                    log.info("Sleeping for 3 seconds.");
                    TimeUnit.SECONDS.sleep(3);
                } catch (HttpClientErrorException httpClientErrorException) {
                    log.info(":( Error while requesting the API call. Probably, the quota has exceeded for today.");
                    log.error("Error: {}", httpClientErrorException.getMessage());

                    if (httpClientErrorException.getRawStatusCode() == 400 && httpClientErrorException.getResponseBodyAsString().contains("invalid region code")) {
                        log.info("Skipping region code[{}].", currentRegionCode);
                        break;
                    } else {
                        log.info("Sleeping for 120 seconds.");
                        try {
                            TimeUnit.SECONDS.sleep(120);
                        } catch (InterruptedException ex) {
                            log.error("Thread Interrupted while execution.{}", ex.getMessage());
                        }
                    }
                } catch (Exception ex) {
                    log.error(":( Unknown Error trying to run the API call, error:{}", ex.getMessage());
                }
            }
        }
    }

    void parseResponse(List<Items> items, String regionCode) {
        for (Items newItem : items) {
            VideoDto videoDto = new VideoDto();
            Snippet newVideo = newItem.getSnippet();
            String youtubeId = newItem.getId();
            videoDto.setYoutubeId(youtubeId);
            videoDto.setTitle(newVideo.getTitle());
            videoDto.setDescription(newVideo.getDescription());
            if (StringUtils.isBlank(newVideo.getDescription())) {
                videoDto.setDescription(videoDto.getTitle());
            }
            videoDto.setPublishedAt(LocalDateTime.parse(newVideo.getPublishedAt(), formatter));
            videoDto.setThumbnail(newVideo.getThumbnails().getHigh().getUrl());
            videoDto.setChannelId(newVideo.getChannelId());

            VideoRegionDto videoRegionDto = new VideoRegionDto();
            videoRegionDto.setRegion(regionCode);

            Set<VideoRegionDto> videoDtoSet = new HashSet<>();
            videoDtoSet.add(videoRegionDto);
            videoDto.setVideoRegions(videoDtoSet);

            totalCounter.incrementAndGet();
            Optional<VideoDto> findVideoDto = videoService.findByYoutubeId(youtubeId);

            if (findVideoDto.isPresent()) {
                if (StringUtils.isBlank(newVideo.getDescription()) && StringUtils.isBlank(findVideoDto.get().getDescription())) {
                    videoDto.setDescription(videoDto.getTitle());
                }
                videoDtoSet.addAll(findVideoDto.get().getVideoRegions());
                PropertyUtil.copyNonNullProperties(videoDto, findVideoDto.get());
                findVideoDto.get().setVideoRegions(videoDtoSet);
                videoService.update(findVideoDto.get());
                updateCounter.incrementAndGet();
            } else {
                videoService.save(videoDto);
                addCounter.incrementAndGet();
            }
        }
        log.info("Added {} and updated {} videos out of {} items fetched..", addCounter, updateCounter, totalCounter);
    }
}