package com.hsuk.video.data.dto;

import com.hsuk.video.data.entity.Video;
import com.hsuk.video.data.validation.YoutubeIDCheck;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@YoutubeIDCheck(message = "Video with given youtube ID already exists.")
public class VideoDto {

    private Long videoId;

    @NotNull(message = "Video title is required.")
    private String title;

    @NotNull(message = "Youtube ID is required.")
    private String youtubeId;

    @NotNull(message = "Video description is required.")
    private String description;

    @NotEmpty(message = "Video region is required.")
    private Set<VideoRegionDto> videoRegions;

    private String thumbnail;
    private String channelId;
    private LocalDateTime publishedAt;

    public static VideoDto convertFromEntity(Video video) {
        VideoDto dto = new VideoDto();
        BeanUtils.copyProperties(video, dto);

        Set<VideoRegionDto> videoRegionDtos = video.getVideoRegions().stream().map(val -> {
            VideoRegionDto videoRegionDto = new VideoRegionDto();
            BeanUtils.copyProperties(val, videoRegionDto);
            videoRegionDto.setVideoId(dto.getVideoId());

            return videoRegionDto;
        }).collect(Collectors.toSet());
        dto.setVideoRegions(videoRegionDtos);
        return dto;
    }
}
