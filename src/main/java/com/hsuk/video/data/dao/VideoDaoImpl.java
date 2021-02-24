package com.hsuk.video.data.dao;

import com.hsuk.video.data.dto.VideoDto;
import com.hsuk.video.data.entity.EntityListener;
import com.hsuk.video.data.entity.Video;
import com.hsuk.video.data.entity.VideoRegion;
import com.hsuk.video.data.repository.VideoRepository;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Transactional
public class VideoDaoImpl implements VideoDao {

    private final VideoRepository videoRepository;

    @Autowired
    public VideoDaoImpl(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    @Override
    public VideoDto saveVideo(VideoDto videoDto) {
        Video video = convertToEntity(videoDto);
        return VideoDto.convertFromEntity(videoRepository.save(video));
    }

    @Override
    public VideoDto getVideoById(Long videoId) {
        Optional<Video> video = videoRepository.findById(videoId);
        if (video.isPresent() && Boolean.TRUE.equals(video.get().getIsDeleted())) {
            return null;
        }
        return video.map(VideoDto::convertFromEntity).orElse(null);
    }

    @Override
    public List<VideoDto> getAllVideos(int pageNo, int pageSize, String sortBy, String sortOrder) {

        Sort.Direction sortDirection = Sort.Direction.ASC;
        if (!Sort.Direction.ASC.name().equalsIgnoreCase(sortOrder)) {
            sortDirection = Sort.Direction.DESC;
        }
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortDirection, sortBy));

        Video activeVideo = new Video();
        activeVideo.setIsDeleted(false);
        Example<Video> activeVideoExample = Example.of(activeVideo);

        Page<Video> pagedResult = videoRepository.findAll(activeVideoExample, paging);

        List<VideoDto> videoDtos = new ArrayList<>();
        if (pagedResult.hasContent()) {
            pagedResult.getContent().forEach(u -> videoDtos.add(VideoDto.convertFromEntity(u)));
        }

        return videoDtos;
    }

    @Override
    public void deleteVideoById(Long videoId) {
        Optional<Video> video = videoRepository.findById(videoId);
        if (video.isPresent()) {
            video.get().setIsDeleted(true);
            video.get().setDeletedBy(EntityListener.getAppIp());
            video.get().setDeletedAt(LocalDateTime.now());
        }
    }

    @Override
    public VideoDto getVideoByYoutubeId(String youtubeId) {
        Optional<Video> video = videoRepository.findVideoByYoutubeId(youtubeId);
        return video.map(VideoDto::convertFromEntity).orElse(null);
    }

    @Override
    public List<VideoDto> getVideosByRegion(String region) {
        Optional<List<Video>> videos = videoRepository.findVideoByRegion(region);
        List<VideoDto> videoDtos = new ArrayList<>();
        videos.ifPresent(videoList -> videoList.forEach(u -> videoDtos.add(VideoDto.convertFromEntity(u))));
        return videoDtos;
    }

    private Video convertToEntity(VideoDto videoDto) {
        Video video = new Video();
        BeanUtils.copyProperties(videoDto, video);
        if (CollectionUtils.isNotEmpty(videoDto.getVideoRegions())) {
            video.setVideoRegions(videoDto.getVideoRegions().stream().map(val -> {
                VideoRegion region = new VideoRegion();
                region.setRegion(val.getRegion());
                region.setVideo(video);
                return region;
            }).collect(Collectors.toSet()));
        }
        return video;
    }
}
