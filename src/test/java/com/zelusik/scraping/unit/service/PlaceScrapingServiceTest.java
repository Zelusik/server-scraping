package com.zelusik.scraping.unit.service;

import com.zelusik.scraping.dto.place.BusinessHoursDto;
import com.zelusik.scraping.dto.place.OpeningHourDto;
import com.zelusik.scraping.dto.place.TimeDto;
import com.zelusik.scraping.service.PlaceScrapingService;
import com.zelusik.scraping.util.OpeningHoursConverter;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.time.LocalTime;
import java.util.List;

import static com.zelusik.scraping.constant.Day.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.*;

@DisplayName("[Unit] Place scraping service test")
@ExtendWith(MockitoExtension.class)
class PlaceScrapingServiceTest {

    @InjectMocks
    private PlaceScrapingService sut;

    @Mock
    private OpeningHoursConverter converter;

    @DisplayName("mArticle에서 homepage url을 추출하면, 추출된 homepage url이 반환된다.")
    @Test
    void givenMArticle_whenGetHomepageUrl_thenReturnHomepageUrl() {
        // given
        WebElement mArticle = createWebElemMock("mArticle");
        WebElement homepage = createWebElemMock("homepage");
        String expectedResult = "Hello";
        given(mArticle.findElement(By.cssSelector("div.cont_essential > div.details_placeinfo > div.placeinfo_default.placeinfo_homepage a.link_homepage"))).willReturn(homepage);
        given(homepage.getAttribute("href")).willReturn(expectedResult);

        // when
        String actualResult = sut.getHomepageUrl(mArticle);

        // then
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @DisplayName("Homepage url 정보가 없는 mArticle에서 homepage url을 추출하면, 예외가 발생한다.")
    @Test
    void givenMArticleWithoutHomepageUrl_whenGetHomepageUrl_thenReturnHomepageUrl() {
        // given
        WebElement mArticle = createWebElemMock("mArticle");
        given(mArticle.findElement(By.cssSelector("div.cont_essential > div.details_placeinfo > div.placeinfo_default.placeinfo_homepage a.link_homepage"))).willThrow(NoSuchElementException.class);

        // when
        Throwable t = catchThrowable(() -> sut.getHomepageUrl(mArticle));

        // then
        assertThat(t).isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("영업시간 조회 - 버튼을 눌러 정보 열람 후, 영업시간과 휴무일 모두 있는 경우")
    @Test
    void existsButtonAndOpeningAndClosingHours_whenGetBusinessHours_thenReturnResult() {
        // given
        WebElement mArticle = createWebElemMock("mArticle");
        WebElement button = createWebElemMock("button");
        WebElement operationList = createWebElemMock("operationList");
        String openingHours = "수~일 10:30 ~ 15:30";
        List<OpeningHourDto> openingHourDtos = createOpeningHourDtoList();
        String closingHours = "월요일\n화요일";
        BusinessHoursDto expectedResult = new BusinessHoursDto(openingHourDtos, closingHours);

        given(mArticle.findElement(By.cssSelector("div.cont_essential > div.details_placeinfo " +
                "div.location_detail.openhour_wrap > div.location_present a.btn_more"))).willReturn(button);
        willDoNothing().given(button).click();
        given(mArticle.findElement(By.cssSelector("div.details_placeinfo div.fold_floor > div.inner_floor"))).willReturn(operationList);
        WebElement openingHoursElem = createWebElemMock("openingHours");
        given(operationList.findElement(By.cssSelector("ul:nth-child(2)"))).willReturn(openingHoursElem);
        given(openingHoursElem.getText()).willReturn(openingHours);
        WebElement closingHoursTitleElem = createWebElemMock("closingHoursTitle");
        given(operationList.findElement(By.cssSelector("strong:nth-child(3)"))).willReturn(closingHoursTitleElem);
        given(closingHoursTitleElem.getText()).willReturn("휴무일");
        WebElement closingHoursElem = createWebElemMock("closingHours");
        given(operationList.findElement(By.cssSelector("ul:nth-child(4)"))).willReturn(closingHoursElem);
        given(closingHoursElem.getText()).willReturn(closingHours);
        given(converter.parseStrToOHs(openingHours)).willReturn(openingHourDtos);

        // when
        BusinessHoursDto actualResult = sut.getBusinessHours(mArticle);

        // then
        verifyGetBusinessHours(openingHours, expectedResult, actualResult);
    }

    @DisplayName("영업시간 조회 - 버튼을 눌러 정보 열람 후, 영업시간과 휴무일 모두 있으나 휴무일 이전에 다른 정보(공휴일 등)가 함께 있는 경우")
    @Test
    void existsButtonAndOpeningAndClosingHoursAndAdditionalInfo_whenGetBusinessHours_thenReturnResult() {
        // given
        WebElement mArticle = createWebElemMock("mArticle");
        WebElement button = createWebElemMock("button");
        WebElement operationList = createWebElemMock("operationList");
        String openingHours = "수~일 10:30 ~ 15:30";
        List<OpeningHourDto> openingHourDtos = createOpeningHourDtoList();
        String closingHours = "월요일\n화요일";
        BusinessHoursDto expectedResult = new BusinessHoursDto(openingHourDtos, closingHours);

        given(mArticle.findElement(By.cssSelector("div.cont_essential > div.details_placeinfo " +
                "div.location_detail.openhour_wrap > div.location_present a.btn_more"))).willReturn(button);
        willDoNothing().given(button).click();
        given(mArticle.findElement(By.cssSelector("div.details_placeinfo div.fold_floor > div.inner_floor"))).willReturn(operationList);
        WebElement openingHoursElem = createWebElemMock("openingHours");
        given(operationList.findElement(By.cssSelector("ul:nth-child(2)"))).willReturn(openingHoursElem);
        given(openingHoursElem.getText()).willReturn(openingHours);
        WebElement closingHoursTitleElem1 = createWebElemMock("closingHoursTitle1");
        given(operationList.findElement(By.cssSelector("strong:nth-child(3)"))).willReturn(closingHoursTitleElem1);
        given(closingHoursTitleElem1.getText()).willReturn("공휴일");
        WebElement closingHoursTitleElem2 = createWebElemMock("closingHoursTitle2");
        given(operationList.findElement(By.cssSelector("strong:nth-child(5)"))).willReturn(closingHoursTitleElem2);
        given(closingHoursTitleElem2.getText()).willReturn("휴무일");
        WebElement closingHoursElem = createWebElemMock("closingHours");
        given(operationList.findElement(By.cssSelector("ul:nth-child(6)"))).willReturn(closingHoursElem);
        given(closingHoursElem.getText()).willReturn(closingHours);
        given(converter.parseStrToOHs(openingHours)).willReturn(openingHourDtos);

        // when
        BusinessHoursDto actualResult = sut.getBusinessHours(mArticle);

        // then
        verifyGetBusinessHours(openingHours, expectedResult, actualResult);
    }

    @DisplayName("영업시간 조회 - 버튼을 눌러 정보 열람 후, 휴무일이 없는 경우")
    @Test
    void existsButtonAndOpeningHours_whenGetBusinessHours_thenReturnResult() {
        // given
        WebElement mArticle = createWebElemMock("mArticle");
        WebElement button = createWebElemMock("button");
        WebElement operationList = createWebElemMock("operationList");
        String openingHours = "수~일 10:30 ~ 15:30";
        List<OpeningHourDto> openingHourDtos = createOpeningHourDtoList();
        String closingHours = "월요일\n화요일";
        BusinessHoursDto expectedResult = new BusinessHoursDto(openingHourDtos, closingHours);

        given(mArticle.findElement(By.cssSelector("div.cont_essential > div.details_placeinfo " +
                "div.location_detail.openhour_wrap > div.location_present a.btn_more"))).willReturn(button);
        willDoNothing().given(button).click();
        given(mArticle.findElement(By.cssSelector("div.details_placeinfo div.fold_floor > div.inner_floor"))).willReturn(operationList);
        WebElement openingHoursElem = createWebElemMock("openingHours");
        given(operationList.findElement(By.cssSelector("ul:nth-child(2)"))).willReturn(openingHoursElem);
        given(openingHoursElem.getText()).willReturn(openingHours);
        given(operationList.findElement(By.cssSelector("strong:nth-child(3)"))).willThrow(NoSuchElementException.class);
        given(converter.parseStrToOHs(openingHours)).willReturn(openingHourDtos);

        // when
        BusinessHoursDto actualResult = sut.getBusinessHours(mArticle);

        // then
        verifyGetBusinessHours(openingHours, expectedResult, actualResult);
    }

    @DisplayName("영업시간 조회 - 버튼이 없고, 영업시간은 있는 경우")
    @Test
    void existsOpeningHours_whenGetBusinessHours_thenReturnResult() {
        // given
        WebElement mArticle = createWebElemMock("mArticle");
        WebElement ohInfo = createWebElemMock("ohInfo");
        String openingHours = "수~일 10:30 ~ 15:30";
        List<OpeningHourDto> openingHourDtos = createOpeningHourDtoList();
        String closingHours = "월요일\n화요일";
        BusinessHoursDto expectedResult = new BusinessHoursDto(openingHourDtos, closingHours);

        given(mArticle.findElement(By.cssSelector("div.cont_essential > div.details_placeinfo " +
                "div.location_detail.openhour_wrap > div.location_present a.btn_more"))).willThrow(NoSuchElementException.class);
        given(mArticle.findElement(By.cssSelector("div.location_detail.openhour_wrap div.location_present"))).willReturn(ohInfo);
        WebElement ohInfoTitleElem = createWebElemMock("ohInfoTitle");
        given(ohInfo.findElement(By.cssSelector("strong.tit_operation > span"))).willReturn(ohInfoTitleElem);
        given(ohInfoTitleElem.getText()).willReturn("영업시간");
        WebElement openingHoursElem = createWebElemMock("openingHours");
        given(ohInfo.findElement(By.cssSelector("ul.list_operation > li > span"))).willReturn(openingHoursElem);
        given(openingHoursElem.getText()).willReturn(openingHours);
        given(converter.parseStrToOHs(openingHours)).willReturn(openingHourDtos);

        // when
        BusinessHoursDto actualResult = sut.getBusinessHours(mArticle);

        // then
        verifyGetBusinessHours(openingHours, expectedResult, actualResult);
    }

    @DisplayName("영업시간 조회 - 버튼이 없고, 영업시간 정보도 없는 경우(title이 영업시간이 아닌 경우)")
    @Test
    void existsNothing_whenGetBusinessHours_thenReturnResult() {
        // given
        WebElement mArticle = createWebElemMock("mArticle");
        WebElement ohInfo = createWebElemMock("ohInfo");
        BusinessHoursDto expectedResult = new BusinessHoursDto(null, null);
        given(mArticle.findElement(By.cssSelector("div.cont_essential > div.details_placeinfo " +
                "div.location_detail.openhour_wrap > div.location_present a.btn_more"))).willThrow(NoSuchElementException.class);
        given(mArticle.findElement(By.cssSelector("div.location_detail.openhour_wrap div.location_present"))).willReturn(ohInfo);
        WebElement ohInfoTitleElem = createWebElemMock("ohInfoTitle");
        given(ohInfo.findElement(By.cssSelector("strong.tit_operation > span"))).willReturn(ohInfoTitleElem);
        given(ohInfoTitleElem.getText()).willReturn("정보");

        // when
        BusinessHoursDto actualResult = sut.getBusinessHours(mArticle);

        // then
        verifyGetBusinessHours(null, expectedResult, actualResult);
    }

    @NotNull
    private static List<OpeningHourDto> createOpeningHourDtoList() {
        return List.of(
                OpeningHourDto.of(WED, TimeDto.of(LocalTime.of(10, 30), LocalTime.of(15, 30))),
                OpeningHourDto.of(THU, TimeDto.of(LocalTime.of(10, 30), LocalTime.of(15, 30))),
                OpeningHourDto.of(FRI, TimeDto.of(LocalTime.of(10, 30), LocalTime.of(15, 30))),
                OpeningHourDto.of(SAT, TimeDto.of(LocalTime.of(10, 30), LocalTime.of(15, 30))),
                OpeningHourDto.of(SUN, TimeDto.of(LocalTime.of(10, 30), LocalTime.of(15, 30)))
        );
    }

    private WebElement createWebElemMock(String name) {
        return Mockito.mock(WebElement.class, name);
    }

    private void verifyGetBusinessHours(String openingHours, BusinessHoursDto expectedResult, BusinessHoursDto actualResult) {
        then(converter).should().parseStrToOHs(openingHours);
        then(converter).shouldHaveNoMoreInteractions();
        if (expectedResult.getOpeningHours() != null) {
            assertThat(actualResult.getOpeningHours().size()).isEqualTo(expectedResult.getOpeningHours().size());
            for (int i = 0; i < expectedResult.getOpeningHours().size(); i++) {
                assertThat(actualResult.getOpeningHours().get(i).getDay()).isEqualTo(expectedResult.getOpeningHours().get(i).getDay());
                assertThat(actualResult.getOpeningHours().get(i).getOpenAt()).isEqualTo(expectedResult.getOpeningHours().get(i).getOpenAt());
                assertThat(actualResult.getOpeningHours().get(i).getCloseAt()).isEqualTo(expectedResult.getOpeningHours().get(i).getCloseAt());
            }
        } else {
            assertThat(actualResult.getOpeningHours()).isNullOrEmpty();
        }
        assertThat(actualResult.getClosingHours()).isEqualTo(expectedResult.getClosingHours());
    }
}
