package com.zelusik.scraping.controller;

import com.zelusik.scraping.dto.place.BusinessHoursDto;
import com.zelusik.scraping.dto.place.PlaceInfoResponse;
import com.zelusik.scraping.service.PlaceScrapingService;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RequiredArgsConstructor
@RequestMapping("/places/scraping")
@RestController
public class PlaceScrapingController {

    private final PlaceScrapingService placeScrapingService;
    private final ChromeOptions createOptions;

    @GetMapping
    public PlaceInfoResponse getKakaoPlaceInfo(@RequestParam String kakaoPid) {
        ChromeDriver driver = new ChromeDriver(createOptions);
        driver.get("https://place.map.kakao.com/" + kakaoPid);

        // Dynamic page가 전부 loading 될 때까지 explicit wait. 최대 10초까지 기다린다.
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("mArticle")));

        WebElement mArticle = driver.findElement(By.id("mArticle"));

        BusinessHoursDto businessHours;
        try {
            businessHours = placeScrapingService.getBusinessHours(mArticle);
        } catch (NoSuchElementException e) {
            businessHours = new BusinessHoursDto(null, null);
        }

        String homepageUrl;
        try {
            homepageUrl = placeScrapingService.getHomepageUrl(mArticle);
        } catch (NoSuchElementException e) {
            homepageUrl = null;
        }

        return PlaceInfoResponse.of(businessHours, homepageUrl);
    }
}
