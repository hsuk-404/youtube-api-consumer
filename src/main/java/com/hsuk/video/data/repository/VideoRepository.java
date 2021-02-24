package com.hsuk.video.data.repository;

import com.hsuk.video.data.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    @Query("SELECT v FROM Video v WHERE v.youtubeId = ?1")
    Optional<Video> findVideoByYoutubeId(String youtubeId);

    @Query("SELECT v FROM Video v JOIN VideoRegion vr on v.videoId = vr.video.videoId WHERE v.isDeleted=false AND vr.region = ?1")
    Optional<List<Video>> findVideoByRegion(String region);
}