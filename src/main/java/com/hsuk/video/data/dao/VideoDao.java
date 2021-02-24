package com.hsuk.video.data.dao;

import com.hsuk.video.data.dto.VideoDto;

import java.util.List;

public interface VideoDao {

    VideoDto saveVideo(VideoDto dto);

    VideoDto getVideoById(Long vid);

    void deleteVideoById(Long vid);

    List<VideoDto> getAllVideos(int pageNo, int pageSize, String sortBy, String sortOrder);

    VideoDto getVideoByYoutubeId(String youtubeId);

    List<VideoDto> getVideosByRegion(String region);
}
