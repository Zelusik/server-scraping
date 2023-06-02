package com.zelusik.scraping.dto.place;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class BusinessHoursDto {

    private List<OpeningHourDto> openingHours;
    private String closingHours;
}
