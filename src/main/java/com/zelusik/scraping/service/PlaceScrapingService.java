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
public class PlaceScrapingService {

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

            String title = ohInfo.findElement(By.cssSelector("strong.tit_operation > span")).getText();
            if (!title.contains("영업")) {
                return createBusinessHours(null, null);
            }

            String openingHours = ohInfo.findElement(By.cssSelector("ul.list_operation > li > span")).getText();
            return createBusinessHours(openingHours, null);
        }

        WebElement operationList = mArticle.findElement(By.cssSelector("div.details_placeinfo div.fold_floor > div.inner_floor"));
        String openingHours = operationList.findElement(By.cssSelector("ul:nth-child(2)")).getText();
        return createBusinessHours(openingHours, getClosingHours(operationList));
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
            if (operationList.findElement(By.cssSelector("strong:nth-child(3)")).getText().equals("휴무일")) {
                return operationList.findElement(By.cssSelector("ul:nth-child(4)")).getText();
            } else if (operationList.findElement(By.cssSelector("strong:nth-child(5)")).getText().equals("휴무일")) {
                return operationList.findElement(By.cssSelector("ul:nth-child(6)")).getText();
            }
            return null;
        } catch (NoSuchElementException e) {
            return null;
        }
    }
}
