package com.zelusik.scraping.dto.place;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PlaceInfoResponse {

    private List<OpeningHourDto> openingHours;
    private String closingHours;
    private String homepageUrl;

    public static PlaceInfoResponse of(BusinessHoursDto businessHours, String homepageUrl) {
        return new PlaceInfoResponse(businessHours.getOpeningHours(), businessHours.getClosingHours(), homepageUrl);
    }
}
