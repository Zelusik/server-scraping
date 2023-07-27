package com.zelusik.scraping.dto.place;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PlaceMenuListResponse {

    private List<String> menus;

    public static PlaceMenuListResponse of(List<String> menus) {
        return new PlaceMenuListResponse(menus);
    }
}
