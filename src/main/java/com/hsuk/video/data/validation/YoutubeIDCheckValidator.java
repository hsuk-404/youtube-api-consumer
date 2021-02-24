package com.hsuk.video.data.validation;

import com.hsuk.video.data.dao.VideoDao;
import com.hsuk.video.data.dto.VideoDto;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class YoutubeIDCheckValidator implements ConstraintValidator<YoutubeIDCheck, VideoDto> {

    private final VideoDao videoDao;

    public YoutubeIDCheckValidator(VideoDao videoDao) {
        this.videoDao = videoDao;
    }

    @Override
    public boolean isValid(VideoDto value, ConstraintValidatorContext context) {
        try {
            Long videoIdValue = value.getVideoId();
            String youtubeIdValue = value.getYoutubeId();
            VideoDto videoFound = videoDao.getVideoByYoutubeId(youtubeIdValue);
            if (videoFound != null) {
                return videoFound.getVideoId().equals(videoIdValue);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}