package com.hsuk.video.data.dto.youtubeApi;

import lombok.Data;

@Data
public class Snippet {
    private Thumbnails thumbnails;
    private String publishedAt;
    private String channelId;
    private String title;
    private String description;

}
