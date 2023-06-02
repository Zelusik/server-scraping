package com.zelusik.scraping.dto.place;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class TimeDto {

    LocalTime openAt;
    LocalTime closeAt;

    public static TimeDto of(LocalTime openAt, LocalTime closeAt) {
        return new TimeDto(openAt, closeAt);
    }
}
