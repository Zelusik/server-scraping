package com.zelusik.scraping.service;

import com.zelusik.scraping.constant.Day;
import com.zelusik.scraping.dto.place.BusinessHoursDto;
import com.zelusik.scraping.dto.place.OpeningHourDto;
import com.zelusik.scraping.util.OpeningHoursConverter;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Service
public class KakaoPlaceScrapingService {

    private final OpeningHoursConverter converter;

    public String getHomepageUrl(WebElement mArticle) throws NoSuchElementException {
        return mArticle.findElement(By.cssSelector("div.cont_essential > div.details_placeinfo > div.placeinfo_default.placeinfo_homepage a.link_homepage")).getAttribute("href");
    }

    public BusinessHoursDto getBusinessHours(WebElement mArticle) throws NoSuchElementException {
        try {
            WebElement expandBtn = mArticle.findElement(By.cssSelector("div.cont_essential > div.details_placeinfo " +
                    "div.location_detail.openhour_wrap > div.location_present a.btn_more"));
            expandBtn.click();
        } catch (NoSuchElementException e) {
            WebElement ohInfo = mArticle.findElement(By.cssSelector("div.location_detail.openhour_wrap div.location_present"));

            String title = ohInfo.findElement(By.cssSelector("strong.tit_operation")).getText();
            if (!title.contains("영업")) {
                return createBusinessHours(null, null);
            }

            String openingHours = ohInfo.findElement(By.cssSelector("ul.list_operation")).getText();
            return createBusinessHours(openingHours, null);
        }

        // TODO: div.inner_floor, div.displayPeriodList ul이 반드시 존재한다고 가정한 코드. 아닌 경우가 있다면 try-catch로 감싸야한다.
        WebElement operationList = mArticle.findElement(By.cssSelector("div.details_placeinfo div.fold_floor > div.inner_floor"));
        String openingHours = operationList.findElement(By.cssSelector("div.displayPeriodList > ul:nth-child(2)")).getText();
        System.out.println(openingHours);
        String closingHours = getClosingHours(operationList);
        return createBusinessHours(openingHours, closingHours);
    }

    private BusinessHoursDto createBusinessHours(String openingHours, String closingHours) {
        List<OpeningHourDto> openingHourDtos = converter.parseStrToOHs(openingHours);
        if (openingHours != null && closingHours == null) {
            List<Day> ohDays = openingHourDtos.stream().map(OpeningHourDto::getDay).toList();
            List<Day> missingDays = findMissingDays(ohDays);
            closingHours = makeClosingHours(missingDays);
        }
        return new BusinessHoursDto(openingHourDtos, closingHours);
    }

    private List<Day> findMissingDays(List<Day> days) {
        return Arrays.stream(Day.values()).filter(day -> !days.contains(day)).toList();
    }

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

    private String getClosingHours(WebElement operationList) {
        try {
            return operationList.findElement(By.cssSelector("div.displayOffdayList > ul:nth-child(2)")).getText();
        } catch (NoSuchElementException e) {
            return null;
        }
    }
}
