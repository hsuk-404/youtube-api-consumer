package com.hsuk.video.data.exception;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class NotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    @NonNull
    private final String message;
}
