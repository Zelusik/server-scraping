package com.zelusik.scraping.service;

import com.zelusik.scraping.constant.Day;
import com.zelusik.scraping.dto.place.BusinessHoursDto;
import com.zelusik.scraping.dto.place.OpeningHourDto;
import com.zelusik.scraping.util.OpeningHoursConverter;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Service
public class KakaoPlaceScrapingService {

    private final OpeningHoursConverter converter;

    @Nullable
    public String getHomepageUrl(WebElement mArticle) {
        try {
            return mArticle.findElement(By.cssSelector("div.cont_essential > div.details_placeinfo > div.placeinfo_default.placeinfo_homepage a.link_homepage")).getAttribute("href");
        } catch (NoSuchElementException ex) {
            return null;
        }
    }

    @NonNull
    public BusinessHoursDto getBusinessHours(WebElement mArticle) {
        try {
            WebElement expandBtn = mArticle.findElement(By.cssSelector("div.cont_essential > div.details_placeinfo div.location_detail.openhour_wrap > div.location_present a.btn_more"));
            expandBtn.click();
        } catch (NoSuchElementException e) {
            WebElement ohInfo;
            try {
                ohInfo = mArticle.findElement(By.cssSelector("div.location_detail.openhour_wrap div.location_present"));
            } catch (NoSuchElementException ex) {
                return new BusinessHoursDto(null, null);
            }

            String title;
            try {
                title = ohInfo.findElement(By.cssSelector("strong.tit_operation")).getText();
            } catch (NoSuchElementException ex) {
                return new BusinessHoursDto(null, null);
            }

            if (!title.contains("영업")) {
                return new BusinessHoursDto(null, null);
            }

            try {
                String openingHours = ohInfo.findElement(By.cssSelector("ul.list_operation")).getText();
                return createBusinessHours(openingHours, null);
            } catch (NoSuchElementException ex) {
                return new BusinessHoursDto(null, null);
            }
        }

        try {
            WebElement operationList = mArticle.findElement(By.cssSelector("div.details_placeinfo div.fold_floor > div.inner_floor"));
            String openingHours = operationList.findElement(By.cssSelector("div.displayPeriodList > ul:nth-child(2)")).getText();
            String closingHours = getClosingHours(operationList);
            return createBusinessHours(openingHours, closingHours);
        } catch (NoSuchElementException ex) {
            return new BusinessHoursDto(null, null);
        }
    }

    @NonNull
    private BusinessHoursDto createBusinessHours(String openingHours, String closingHours) {
        List<OpeningHourDto> openingHourDtos = converter.parseStrToOHs(openingHours);
        if (openingHours != null && closingHours == null) {
            List<Day> ohDays = openingHourDtos.stream().map(OpeningHourDto::getDay).toList();
            List<Day> missingDays = findMissingDays(ohDays);
            closingHours = makeClosingHours(missingDays);
        }
        return new BusinessHoursDto(openingHourDtos, closingHours);
    }

    @NonNull
    private List<Day> findMissingDays(List<Day> days) {
        return Arrays.stream(Day.values()).filter(day -> !days.contains(day)).toList();
    }

    @Nullable
    private String makeClosingHours(List<Day> missingDays) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < missingDays.size(); i++) {
            sb.append(missingDays.get(i).getDescription()).append("요일");
            if (i != missingDays.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.length() > 0 ? sb.toString() : null;
    }

    @Nullable
    private String getClosingHours(WebElement operationList) {
        try {
            return operationList.findElement(By.cssSelector("div.displayOffdayList > ul:nth-child(2)")).getText();
        } catch (NoSuchElementException e) {
            return null;
        }
    }
}
