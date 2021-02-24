package com.hsuk.video.data.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class VideoRegionDto {

    private Long videoRegionId;

    private Long videoId;

    @NotNull(message = "Video region is required.")
    private String region;
}
