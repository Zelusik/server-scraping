package com.zelusik.scraping.integration.controller;

import com.zelusik.scraping.controller.KakaoPlaceScrapingController;
import com.zelusik.scraping.dto.place.OpeningHourDto;
import com.zelusik.scraping.dto.place.PlaceInfoResponse;
import com.zelusik.scraping.dto.place.TimeDto;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

import static com.zelusik.scraping.constant.Day.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("[Integration] Selenium을 활용해 장소 정보 가져오기")
@Disabled("실제 가게 정보는 수시로 바뀔 수 있으므로 기능의 정상적인 동작을 확인하기에 불안정하다. 따라서 평소에는 disabled 해놓고 필요할 때 테스트하는 방식으로 사용한다.")
@SpringBootTest
class KakaoPlaceScrapingTest {

    @Autowired
    private KakaoPlaceScrapingController sut;

    @DisplayName("Kakao place id가 주어지면, 해당 장소의 영업시간, 휴무일, 홈페이지 주소를 가져오고 반환한다.")
    @MethodSource("kakaoPlaceInfos")
    @ParameterizedTest(name = "[{index}] kakaoPid={0}")
    void getKakaoPlaceInfoTest(String kakaoPid, List<OpeningHourDto> openingHours, String closingHours, String homepage) {
        // given

        // when
        PlaceInfoResponse actualResult = sut.getKakaoPlaceInfo(kakaoPid);

        // then
        if (openingHours != null) {
            assertThat(actualResult.getOpeningHours()).isNotNull();
            assertThat(actualResult.getOpeningHours().size()).isEqualTo(openingHours.size());
            for (int i = 0; i < openingHours.size(); i++) {
                assertThat(actualResult.getOpeningHours().get(i).getDay()).isEqualTo(openingHours.get(i).getDay());
                assertThat(actualResult.getOpeningHours().get(i).getOpenAt()).isEqualTo(openingHours.get(i).getOpenAt());
                assertThat(actualResult.getOpeningHours().get(i).getCloseAt()).isEqualTo(openingHours.get(i).getCloseAt());
            }
        } else {
            assertThat(actualResult.getOpeningHours()).isNullOrEmpty();
        }
        assertThat(actualResult.getClosingHours()).isEqualTo(closingHours);
        assertThat(actualResult.getHomepageUrl()).isEqualTo(homepage);
    }

    static Stream<Arguments> kakaoPlaceInfos() {
        return Stream.of(
                arguments("682903436", null, null, null),
                arguments("14575281",
                        List.of(OpeningHourDto.of(TUE, TimeDto.of(LocalTime.of(10, 30), LocalTime.of(15, 30))),
                                OpeningHourDto.of(WED, TimeDto.of(LocalTime.of(10, 30), LocalTime.of(15, 30))),
                                OpeningHourDto.of(THU, TimeDto.of(LocalTime.of(10, 30), LocalTime.of(15, 30))),
                                OpeningHourDto.of(FRI, TimeDto.of(LocalTime.of(10, 30), LocalTime.of(15, 30))),
                                OpeningHourDto.of(SAT, TimeDto.of(LocalTime.of(10, 30), LocalTime.of(15, 30))),
                                OpeningHourDto.of(SUN, TimeDto.of(LocalTime.of(10, 30), LocalTime.of(15, 30)))),
                        "월요일", "https://app.catchtable.co.kr/ct/shop/moru"),
                arguments("308342289",
                        List.of(OpeningHourDto.of(MON, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(22, 0))),
                                OpeningHourDto.of(TUE, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(22, 0))),
                                OpeningHourDto.of(WED, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(22, 0))),
                                OpeningHourDto.of(THU, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(22, 0))),
                                OpeningHourDto.of(FRI, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(22, 0))),
                                OpeningHourDto.of(SAT, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(22, 0))),
                                OpeningHourDto.of(SUN, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(22, 0)))),
                        null, "https://www.instagram.com/toma_wv/"),
                arguments("25001083",
                        List.of(OpeningHourDto.of(MON, TimeDto.of(LocalTime.of(8, 0), LocalTime.of(0, 0))),
                                OpeningHourDto.of(TUE, TimeDto.of(LocalTime.of(8, 0), LocalTime.of(0, 0))),
                                OpeningHourDto.of(WED, TimeDto.of(LocalTime.of(8, 0), LocalTime.of(0, 0))),
                                OpeningHourDto.of(THU, TimeDto.of(LocalTime.of(8, 0), LocalTime.of(0, 0))),
                                OpeningHourDto.of(FRI, TimeDto.of(LocalTime.of(8, 0), LocalTime.of(0, 0))),
                                OpeningHourDto.of(SAT, TimeDto.of(LocalTime.of(8, 0), LocalTime.of(0, 0))),
                                OpeningHourDto.of(SUN, TimeDto.of(LocalTime.of(8, 0), LocalTime.of(0, 0)))),
                        null, "http://www.mcdonalds.co.kr/"),
                arguments("24529744",
                        List.of(OpeningHourDto.of(MON, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(22, 0))),
                                OpeningHourDto.of(TUE, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(22, 0))),
                                OpeningHourDto.of(WED, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(22, 0))),
                                OpeningHourDto.of(THU, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(22, 0))),
                                OpeningHourDto.of(FRI, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(22, 0))),
                                OpeningHourDto.of(SAT, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(22, 0))),
                                OpeningHourDto.of(SUN, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(22, 0)))),
                        null, "https://m.booking.naver.com/booking/6/bizes/137321/items/2713823?area=ple"),
                arguments("13318338",
                        List.of(OpeningHourDto.of(MON, TimeDto.of(LocalTime.of(11, 0), LocalTime.of(1, 0))),
                                OpeningHourDto.of(TUE, TimeDto.of(LocalTime.of(11, 0), LocalTime.of(1, 0))),
                                OpeningHourDto.of(WED, TimeDto.of(LocalTime.of(11, 0), LocalTime.of(1, 0))),
                                OpeningHourDto.of(THU, TimeDto.of(LocalTime.of(11, 0), LocalTime.of(1, 0))),
                                OpeningHourDto.of(FRI, TimeDto.of(LocalTime.of(11, 0), LocalTime.of(1, 0))),
                                OpeningHourDto.of(SAT, TimeDto.of(LocalTime.of(11, 0), LocalTime.of(1, 0))),
                                OpeningHourDto.of(SUN, TimeDto.of(LocalTime.of(11, 0), LocalTime.of(22, 0)))),
                        null, "http://cestlavie.fordining.kr/"),
                arguments("20995567",
                        List.of(OpeningHourDto.of(MON, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(21, 30))),
                                OpeningHourDto.of(TUE, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(21, 30))),
                                OpeningHourDto.of(WED, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(21, 30))),
                                OpeningHourDto.of(THU, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(21, 30))),
                                OpeningHourDto.of(FRI, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(21, 30))),
                                OpeningHourDto.of(SAT, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(21, 30))),
                                OpeningHourDto.of(SUN, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(21, 30)))),
                        null, "https://dunsanmasil.modoo.at/"),
                arguments("184145085",
                        List.of(OpeningHourDto.of(MON, TimeDto.of(LocalTime.of(11, 0), LocalTime.of(19, 0))),
                                OpeningHourDto.of(TUE, TimeDto.of(LocalTime.of(11, 0), LocalTime.of(19, 0))),
                                OpeningHourDto.of(WED, TimeDto.of(LocalTime.of(11, 0), LocalTime.of(19, 0))),
                                OpeningHourDto.of(THU, TimeDto.of(LocalTime.of(11, 0), LocalTime.of(18, 0))),
                                OpeningHourDto.of(SAT, TimeDto.of(LocalTime.of(11, 0), LocalTime.of(19, 0))),
                                OpeningHourDto.of(SUN, TimeDto.of(LocalTime.of(11, 0), LocalTime.of(19, 0)))),
                        "금요일", "https://www.instagram.com/hi_hasand"),
                arguments("8149130",
                        List.of(OpeningHourDto.of(MON, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(22, 0))),
                                OpeningHourDto.of(TUE, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(22, 0))),
                                OpeningHourDto.of(WED, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(22, 0))),
                                OpeningHourDto.of(THU, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(22, 0))),
                                OpeningHourDto.of(FRI, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(22, 0))),
                                OpeningHourDto.of(SAT, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(22, 0))),
                                OpeningHourDto.of(SUN, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(22, 0)))),
                        "설당일\n설전날\n설다음날\n추석당일\n추석전날\n추석다음날", "http://www.xn--w39a45ki5j7idj7fkmcgy7b.com/"),
                arguments("1399098939",
                        List.of(OpeningHourDto.of(MON, TimeDto.of(LocalTime.of(10, 0), LocalTime.of(19, 30))),
                                OpeningHourDto.of(TUE, TimeDto.of(LocalTime.of(10, 0), LocalTime.of(19, 30))),
                                OpeningHourDto.of(THU, TimeDto.of(LocalTime.of(10, 0), LocalTime.of(19, 30))),
                                OpeningHourDto.of(FRI, TimeDto.of(LocalTime.of(10, 0), LocalTime.of(19, 30))),
                                OpeningHourDto.of(SAT, TimeDto.of(LocalTime.of(10, 0), LocalTime.of(19, 30))),
                                OpeningHourDto.of(SUN, TimeDto.of(LocalTime.of(10, 0), LocalTime.of(19, 30)))),
                        "수요일", null)
        );
    }
}