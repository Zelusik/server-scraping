package com.zelusik.scraping.dto.place;

import com.zelusik.scraping.constant.Day;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class OpeningHourDto {

    private Day day;
    private LocalTime openAt;
    private LocalTime closeAt;

    public static OpeningHourDto of(Day day, TimeDto time) {
        return new OpeningHourDto(day, time.getOpenAt(), time.getCloseAt());
    }
}
