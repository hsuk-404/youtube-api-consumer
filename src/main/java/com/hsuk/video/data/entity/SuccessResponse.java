package com.hsuk.video.data.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
public class SuccessResponse implements Serializable {

    private final String successMsg;
    private final String successCode = "OK";
    private final int status = HttpStatus.OK.value();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private final LocalDateTime timestamp;

    public SuccessResponse(String successMsg) {
        this.timestamp = LocalDateTime.now();
        this.successMsg = successMsg;
    }
}