package com.hsuk.video.data.controller;

import com.hsuk.video.data.dto.VideoDto;
import com.hsuk.video.data.entity.SuccessResponse;
import com.hsuk.video.data.exception.NotFoundException;
import com.hsuk.video.data.service.VideoService;
import com.hsuk.video.data.utils.PropertyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/videos")
public class VideoController {

    private final VideoService videoService;
    private final Validator validator;

    @Autowired
    public VideoController(VideoService videoService, Validator validator) {
        this.videoService = videoService;
        this.validator = validator;
    }

    @GetMapping(value = "/")
    public List<VideoDto> getAllVideos(@RequestParam(defaultValue = "0") Integer pageNo,
                                       @RequestParam(defaultValue = "50") Integer pageSize,
                                       @RequestParam(defaultValue = "videoId") String sortBy,
                                       @RequestParam(defaultValue = "asc") String sortOrder) {
        return videoService.getAllVideos(pageNo, pageSize, sortBy, sortOrder);
    }

    @GetMapping(value = "/region/{region}")
    public List<VideoDto> getAllVideos(@PathVariable("region") String region) {
        return videoService.getVideosByRegion(region);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<VideoDto> getVideoById(@PathVariable("id") @Min(1) int id) throws Exception {
        log.info("Fetching video with id:{}", id);
        Optional<VideoDto> video = videoService.findById(id);
        if (video.isPresent()) {
            return new ResponseEntity<>(video.get(), HttpStatus.OK);
        } else {
            throw new NotFoundException("Video ID: " + id + " is not found.");
        }
    }

    @PostMapping(value = "/")
    public ResponseEntity<VideoDto> addVideo(@Valid @RequestBody VideoDto video) throws Exception {
        log.info("Adding new video with youtube Title:{} and Id:{}", video.getTitle(), video.getYoutubeId());
        Optional<VideoDto> videoDto = videoService.save(video);
        if (videoDto.isPresent()) {
            return new ResponseEntity<>(videoDto.get(), HttpStatus.OK);
        } else {
            throw new Exception("Video could not be saved.");
        }
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<VideoDto> updateVideo(@PathVariable("id") @Min(1) long id, @RequestBody VideoDto newVideo) throws Exception {
        log.info("Updating new video with youtube Title:{} and Id:{}", newVideo.getTitle(), id);
        VideoDto video = videoService.findById(id)
                .orElseThrow(() -> new NotFoundException("Video with " + id + " is not found."));
        PropertyUtil.copyNonNullProperties(newVideo, video);
        Set<ConstraintViolation<VideoDto>> violations = validator.validate(video);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        Optional<VideoDto> videoDto = videoService.update(video);
        if (videoDto.isPresent()) {
            return new ResponseEntity<>(videoDto.get(), HttpStatus.OK);
        } else {
            throw new Exception("Video was saved but video cannot be fetched.");
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<SuccessResponse> deleteVideo(@PathVariable("id") @Min(1) long id) throws Exception {
        log.info("Deleting video with id:{}", id);
        VideoDto video = videoService.findById(id)
                .orElseThrow(() -> new NotFoundException("Video ID: " + id + " is not found."));
        videoService.deleteById(video.getVideoId());
        return new ResponseEntity<>(new SuccessResponse("Video is deleted successfully."), HttpStatus.OK);
    }
}
