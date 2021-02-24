package com.hsuk.video.data.service;

import com.hsuk.video.data.dao.VideoDao;
import com.hsuk.video.data.dto.VideoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class VideoService {
    private final VideoDao videoDao;

    @Autowired
    public VideoService(VideoDao videoDao) {
        this.videoDao = videoDao;
    }

    public List<VideoDto> getAllVideos(int pageNo, int pageSize, String sortBy, String sortOrder) {
        try {
            return videoDao.getAllVideos(pageNo, pageSize, sortBy, sortOrder);
        } catch (Exception e) {
            log.error("Error while trying to fetch videos. Exception: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<VideoDto> getVideosByRegion(String region) {
        try {
            return videoDao.getVideosByRegion(region);
        } catch (Exception e) {
            log.error("Error while trying to fetch videos. Exception: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public Optional<VideoDto> findById(long id) {
        try {
            return Optional.ofNullable(videoDao.getVideoById(id));
        } catch (Exception e) {
            log.error("Error while trying to find video. Exception: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<VideoDto> save(VideoDto videoDto) {
        try {
            if (videoDto.getDescription() != null && videoDto.getDescription().length() > 512) {
                videoDto.setDescription(videoDto.getDescription().substring(0, 512));
            }
            if (videoDto.getTitle() != null && videoDto.getTitle().length() > 255) {
                videoDto.setTitle(videoDto.getTitle().substring(0, 255));
            }
            return Optional.ofNullable(videoDao.saveVideo(videoDto));
        } catch (Exception e) {
            log.error("Error while trying to save video. Exception: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<VideoDto> update(VideoDto videoDto) {
        try {
            if (videoDto.getDescription() != null && videoDto.getDescription().length() > 512) {
                videoDto.setDescription(videoDto.getDescription().substring(0, 512));
            }
            if (videoDto.getTitle() != null && videoDto.getTitle().length() > 255) {
                videoDto.setTitle(videoDto.getTitle().substring(0, 255));
            }
            return Optional.ofNullable(videoDao.saveVideo(videoDto));
        } catch (Exception e) {
            log.error("Error while trying to update video. Exception: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<VideoDto> findByYoutubeId(String youtubeId) {
        try {
            return Optional.ofNullable(videoDao.getVideoByYoutubeId(youtubeId));
        } catch (Exception e) {
            log.error("Error while trying to find video. Exception: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public void deleteById(long id) {
        try {
            videoDao.deleteVideoById(id);
        } catch (Exception e) {
            log.error("Error while trying to delete video. Exception: {}", e.getMessage());
        }
    }
}