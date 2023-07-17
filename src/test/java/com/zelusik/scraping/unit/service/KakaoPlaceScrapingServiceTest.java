package com.zelusik.scraping.unit.service;

import com.zelusik.scraping.dto.place.BusinessHoursDto;
import com.zelusik.scraping.dto.place.OpeningHourDto;
import com.zelusik.scraping.dto.place.TimeDto;
import com.zelusik.scraping.service.KakaoPlaceScrapingService;
import com.zelusik.scraping.util.OpeningHoursConverter;
import org.junit.jupiter.api.Disabled;
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
import static org.mockito.BDDMockito.*;

@DisplayName("[Unit] Place scraping service test")
@ExtendWith(MockitoExtension.class)
class KakaoPlaceScrapingServiceTest {

    @InjectMocks
    private KakaoPlaceScrapingService sut;

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
        String result = sut.getHomepageUrl(mArticle);

        // then
        assertThat(result).isNull();
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

        given(mArticle.findElement(By.cssSelector("div.cont_essential > div.details_placeinfo div.location_detail.openhour_wrap > div.location_present a.btn_more"))).willReturn(button);
        willDoNothing().given(button).click();
        given(mArticle.findElement(By.cssSelector("div.details_placeinfo div.fold_floor > div.inner_floor"))).willReturn(operationList);
        WebElement openingHoursElem = createWebElemMock("openingHours");
        given(operationList.findElement(By.cssSelector("div.displayPeriodList > ul:nth-child(2)"))).willReturn(openingHoursElem);
        given(openingHoursElem.getText()).willReturn(openingHours);
        WebElement closingHoursTitleElem = createWebElemMock("closingHoursTitle");
        given(operationList.findElement(By.cssSelector("div.displayOffdayList > ul:nth-child(2)"))).willReturn(closingHoursTitleElem);
        given(closingHoursTitleElem.getText()).willReturn(closingHours);
        given(converter.parseStrToOHs(openingHours)).willReturn(openingHourDtos);

        // when
        BusinessHoursDto actualResult = sut.getBusinessHours(mArticle);

        // then
        verifyGetBusinessHours(openingHours, expectedResult, actualResult);
    }

    @Disabled("""
            23.7.17 기준 "공휴일" 등의 정보가 div.displayPeriodList에 포함되었다.
            그러므로 현재 코드에서는 "공휴일" 등의 정보를 전혀 추출하지 않고, 그렇기 때문에 바로 위의 test(existsButtonAndOpeningAndClosingHours_whenGetBusinessHours_thenReturnResult)와 로직이 동일하다.
            어차피 "공휴일" 등의 정보는 현재 필요하지 않지만, 추후 필요할 수도 있으니 단지 test case를 남기기 위해 우선 이 test는 disabled 처리한다.
            """)
    @DisplayName("영업시간 조회 - 버튼을 눌러 정보 열람 후, 영업시간과 휴무일 모두 있으나 휴무일 이전에 다른 정보(공휴일 등)가 함께 있는 경우")
    @Test
    void existsButtonAndOpeningAndClosingHoursAndAdditionalInfo_whenGetBusinessHours_thenReturnResult() {
        // given

        // when

        // then
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
        given(operationList.findElement(By.cssSelector("div.displayPeriodList > ul:nth-child(2)"))).willReturn(openingHoursElem);
        given(openingHoursElem.getText()).willReturn(openingHours);
        given(operationList.findElement(By.cssSelector("div.displayOffdayList > ul:nth-child(2)"))).willThrow(NoSuchElementException.class);
        given(converter.parseStrToOHs(openingHours)).willReturn(openingHourDtos);

        // when
        BusinessHoursDto actualResult = sut.getBusinessHours(mArticle);

        // then
        verifyGetBusinessHours(openingHours, expectedResult, actualResult);
    }

    @DisplayName("영업시간 조회 - 버튼은 있으나 영업시간과 휴무일 정보를 담은 tag 자체를 찾을 수 없는 경우")
    @Test
    void notExistsInnerFloor_whenGetBusinessHours_thenReturnEmptyResult() {
        // given
        WebElement mArticle = createWebElemMock("mArticle");
        WebElement button = createWebElemMock("button");
        given(mArticle.findElement(By.cssSelector("div.cont_essential > div.details_placeinfo div.location_detail.openhour_wrap > div.location_present a.btn_more"))).willReturn(button);
        willDoNothing().given(button).click();
        given(mArticle.findElement(By.cssSelector("div.details_placeinfo div.fold_floor > div.inner_floor"))).willThrow(NoSuchElementException.class);

        // when
        BusinessHoursDto result = sut.getBusinessHours(mArticle);

        // then
        then(mArticle).should().findElement(By.cssSelector("div.cont_essential > div.details_placeinfo div.location_detail.openhour_wrap > div.location_present a.btn_more"));
        then(button).should().click();
        then(mArticle).should().findElement(By.cssSelector("div.details_placeinfo div.fold_floor > div.inner_floor"));
        then(mArticle).shouldHaveNoMoreInteractions();
        then(button).shouldHaveNoMoreInteractions();
        assertThat(result)
                .isNotNull()
                .hasAllNullFieldsOrProperties();
    }

    @DisplayName("영업시간 조회 - 버튼이 없고 영업시간 정보를 담고 있는 상위 tag인 ohInfo도 없는 경우")
    @Test
    void notExistsButtonAndOhInfo_whenGetBusinessHours_thenReturnEmptyResult() {
        // given
        WebElement mArticle = createWebElemMock("mArticle");
        given(mArticle.findElement(By.cssSelector("div.cont_essential > div.details_placeinfo div.location_detail.openhour_wrap > div.location_present a.btn_more"))).willThrow(NoSuchElementException.class);
        given(mArticle.findElement(By.cssSelector("div.location_detail.openhour_wrap div.location_present"))).willThrow(NoSuchElementException.class);

        // when
        BusinessHoursDto result = sut.getBusinessHours(mArticle);

        // then
        then(mArticle).should().findElement(By.cssSelector("div.cont_essential > div.details_placeinfo div.location_detail.openhour_wrap > div.location_present a.btn_more"));
        then(mArticle).should().findElement(By.cssSelector("div.location_detail.openhour_wrap div.location_present"));
        then(mArticle).shouldHaveNoMoreInteractions();
        assertThat(result)
                .isNotNull()
                .hasAllNullFieldsOrProperties();
    }

    @DisplayName("영업시간 조회 - 버튼이 없고, 제목을 찾지 못하는 경우")
    @Test
    void notExistsButtonAndTitle_whenGetBusinessHours_thenReturnEmptyResult() {
        // given
        WebElement mArticle = createWebElemMock("mArticle");
        WebElement ohInfo = createWebElemMock("ohInfo");
        given(mArticle.findElement(By.cssSelector("div.cont_essential > div.details_placeinfo div.location_detail.openhour_wrap > div.location_present a.btn_more"))).willThrow(NoSuchElementException.class);
        given(mArticle.findElement(By.cssSelector("div.location_detail.openhour_wrap div.location_present"))).willReturn(ohInfo);
        given(ohInfo.findElement(By.cssSelector("strong.tit_operation"))).willThrow(NoSuchElementException.class);

        // when
        BusinessHoursDto result = sut.getBusinessHours(mArticle);

        // then
        then(mArticle).should().findElement(By.cssSelector("div.cont_essential > div.details_placeinfo div.location_detail.openhour_wrap > div.location_present a.btn_more"));
        then(mArticle).should().findElement(By.cssSelector("div.location_detail.openhour_wrap div.location_present"));
        then(ohInfo).should().findElement(By.cssSelector("strong.tit_operation"));
        then(mArticle).shouldHaveNoMoreInteractions();
        then(ohInfo).shouldHaveNoMoreInteractions();
        assertThat(result)
                .isNotNull()
                .hasAllNullFieldsOrProperties();
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
        given(ohInfo.findElement(By.cssSelector("strong.tit_operation"))).willReturn(ohInfoTitleElem);
        given(ohInfoTitleElem.getText()).willReturn("영업시간");
        WebElement openingHoursElem = createWebElemMock("openingHours");
        given(ohInfo.findElement(By.cssSelector("ul.list_operation"))).willReturn(openingHoursElem);
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
        given(mArticle.findElement(By.cssSelector("div.cont_essential > div.details_placeinfo div.location_detail.openhour_wrap > div.location_present a.btn_more"))).willThrow(NoSuchElementException.class);
        given(mArticle.findElement(By.cssSelector("div.location_detail.openhour_wrap div.location_present"))).willReturn(ohInfo);
        WebElement ohInfoTitleElem = createWebElemMock("ohInfoTitle");
        given(ohInfo.findElement(By.cssSelector("strong.tit_operation"))).willReturn(ohInfoTitleElem);
        given(ohInfoTitleElem.getText()).willReturn("정보");

        // when
        BusinessHoursDto result = sut.getBusinessHours(mArticle);

        // then
        then(mArticle).should().findElement(By.cssSelector("div.cont_essential > div.details_placeinfo div.location_detail.openhour_wrap > div.location_present a.btn_more"));
        then(mArticle).should().findElement(By.cssSelector("div.location_detail.openhour_wrap div.location_present"));
        then(ohInfo).should().findElement(By.cssSelector("strong.tit_operation"));
        then(ohInfoTitleElem).should().getText();
        then(mArticle).shouldHaveNoMoreInteractions();
        then(ohInfo).shouldHaveNoMoreInteractions();
        then(ohInfoTitleElem).shouldHaveNoMoreInteractions();
        assertThat(result)
                .isNotNull()
                .hasAllNullFieldsOrProperties();
    }

    @DisplayName("영업시간 조회 - 버튼이 없고, '영업'이 포함된 제목은 있으나 영업시간 정보가 없는 경우")
    @Test
    void notExistsOpeningHoursInfo_whenGetBusinessHours_thenEmptyResult() {
        // given
        WebElement mArticle = createWebElemMock("mArticle");
        WebElement ohInfo = createWebElemMock("ohInfo");
        given(mArticle.findElement(By.cssSelector("div.cont_essential > div.details_placeinfo div.location_detail.openhour_wrap > div.location_present a.btn_more"))).willThrow(NoSuchElementException.class);
        given(mArticle.findElement(By.cssSelector("div.location_detail.openhour_wrap div.location_present"))).willReturn(ohInfo);
        WebElement ohInfoTitleElem = createWebElemMock("ohInfoTitle");
        given(ohInfo.findElement(By.cssSelector("strong.tit_operation"))).willReturn(ohInfoTitleElem);
        given(ohInfoTitleElem.getText()).willReturn("영업시간");
        given(ohInfo.findElement(By.cssSelector("ul.list_operation"))).willThrow(NoSuchElementException.class);

        // when
        BusinessHoursDto result = sut.getBusinessHours(mArticle);

        // then
        then(mArticle).should().findElement(By.cssSelector("div.cont_essential > div.details_placeinfo div.location_detail.openhour_wrap > div.location_present a.btn_more"));
        then(mArticle).should().findElement(By.cssSelector("div.location_detail.openhour_wrap div.location_present"));
        then(ohInfo).should().findElement(By.cssSelector("strong.tit_operation"));
        then(ohInfoTitleElem).should().getText();
        then(ohInfo).should().findElement(By.cssSelector("ul.list_operation"));
        then(mArticle).shouldHaveNoMoreInteractions();
        then(ohInfo).shouldHaveNoMoreInteractions();
        assertThat(result)
                .isNotNull()
                .hasAllNullFieldsOrProperties();
    }

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
