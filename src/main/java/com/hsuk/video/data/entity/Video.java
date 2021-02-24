package com.hsuk.video.data.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.Set;


@Entity
@Getter
@Setter
@Table(name = "video")
public class Video extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "video_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long videoId;

    @Column(name = "youtube_id", unique = true)
    @NotEmpty(message = "Youtube ID is required.")
    private String youtubeId;

    @Length(max = 255, message = "Video title exceeded the length.")
    @NotEmpty(message = "Video title is required.")
    private String title;

    @Column(length = 512)
    @Length(max = 512, message = "Video description exceeded the length.")
    @NotEmpty(message = "Video description is required.")
    private String description;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    private String thumbnail;

    @Column(name = "channel_id")
    private String channelId;

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VideoRegion> videoRegions;
}
