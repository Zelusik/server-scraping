package com.zelusik.scraping.controller;

import com.zelusik.scraping.dto.place.BusinessHoursDto;
import com.zelusik.scraping.dto.place.PlaceInfoResponse;
import com.zelusik.scraping.dto.place.PlaceMenuListResponse;
import com.zelusik.scraping.service.KakaoPlaceScrapingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
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

@Tag(name = "장소 Scraping")
@RequiredArgsConstructor
@RequestMapping("/places/scraping")
@RestController
public class KakaoPlaceScrapingController {

    private final KakaoPlaceScrapingService kakaoPlaceScrapingService;
    private final ChromeOptions chromeDriverOptions;

    @Operation(summary = "Kakao 장소 정보 scraping",
            description = "<p>요청 시 전달받은 <code>kakaoPid</code>를 이용해 kakao map 상세 페이지에서 영업시간, 휴무일, 홈페이지 url을 추출해 응답한다.")
    @GetMapping
    public PlaceInfoResponse getKakaoPlaceInfo(@RequestParam String kakaoPid) {
        ChromeDriver driver = new ChromeDriver(chromeDriverOptions);
        try {
            driver.get("https://place.map.kakao.com/" + kakaoPid);

            // Dynamic page가 전부 loading 될 때까지 explicit wait. 최대 10초까지 기다린다.
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("mArticle")));

            WebElement mArticle = driver.findElement(By.id("mArticle"));

            BusinessHoursDto businessHours = kakaoPlaceScrapingService.getBusinessHours(mArticle);
            String homepageUrl = kakaoPlaceScrapingService.getHomepageUrl(mArticle);

            return PlaceInfoResponse.of(businessHours, homepageUrl);
        } finally {
            driver.quit();
        }
    }

    @Operation(summary = "가게의 메뉴 목록 조회",
            description = "<p>전달받은 <code>kakaoPid</code>에 해당하는 장소의 메누 목록을 조회한다.")
    @GetMapping("/menus")
    public PlaceMenuListResponse getKakaoPlaceMenuList(@RequestParam String kakaoPid) {
        ChromeDriver driver = new ChromeDriver(chromeDriverOptions);
        try {
            driver.get("https://place.map.kakao.com/" + kakaoPid);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("mArticle")));

            WebElement mArticle = driver.findElement(By.id("mArticle"));
            return PlaceMenuListResponse.of(kakaoPlaceScrapingService.getMenuList(mArticle));
        } finally {
            driver.quit();
        }
    }
}
