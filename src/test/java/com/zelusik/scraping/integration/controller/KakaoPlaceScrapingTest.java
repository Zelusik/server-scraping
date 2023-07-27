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
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("[Integration] Selenium을 활용해 장소 정보 가져오기")
@Disabled("실제 가게 정보는 수시로 바뀔 수 있으므로 기능의 정상적인 동작을 확인하기에 불안정하다. 따라서 평소에는 비활성화한다.")
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
                        List.of(OpeningHourDto.of(MON, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(21, 30))),
                                OpeningHourDto.of(TUE, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(21, 30))),
                                OpeningHourDto.of(WED, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(21, 30))),
                                OpeningHourDto.of(THU, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(21, 30))),
                                OpeningHourDto.of(FRI, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(21, 30))),
                                OpeningHourDto.of(SAT, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(21, 30))),
                                OpeningHourDto.of(SUN, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(21, 30)))),
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

    @DisplayName("Kakao 장소 id가 주어지면, 메뉴 목록을 조회한다.")
    @MethodSource("kakaoPlaceMenuListForTest")
    @ParameterizedTest(name = "[{index}] {0}")
    void givenKakaoPlaceId_whenGetMenuList_thenReturnSearchResults(String name, String kakaoPid, List<String> expectedResult) {
        // given

        // when
        List<String> actualResult = sut.getKakaoPlaceMenuList(kakaoPid).getMenus();

        // then
        assertIterableEquals(expectedResult, actualResult);
    }

    // 테스트 데이터는 https://www.notion.so/wo-ogie/eb78fa7b6d7e4910a3430955bc42a794?pvs=4 참고
    static Stream<Arguments> kakaoPlaceMenuListForTest() {
        return Stream.of(
                arguments("진전복삼계탕 본점", "1117702712", List.of("전복미역국", "특전복삼계탕", "진삼계탕", "녹두삼계탕", "전복삼계탕", "완도통닭", "완도한판(중)", "흑마늘삼계탕", "전복매생이삼계탕", "누릉지삼계탕(현미)", "흑마늘삼계탕(중)", "완도백숙(중)", "전문튀김", "전복장", "진전복죽", "전복장버터비빔밥")),
                arguments("다운타우너 광교 갤러리아점", "1624407362", List.of("아보카도 버거", "내슈빌 치킨 버거", "치즈 버거", "더블치즈 버거", "베이컨치즈 버거", "더블베이컨 버거", "해쉬브라운 버거", "트러플 버거", "더블트러플 버거", "슈림프 버거(*알일한정)", "스파이시 슈림프 버거", "치킨 콤보 다리", "데리야끼 치킨 버거", "오리지널 프라이즈", "치즈 프라이즈", "스키니 프라이즈", "트러플 파마산 프라이즈", "스파이시 치폴레 프라이즈", "스윗 포테이토 프라이즈", "파티 치킨 플래터 다리")),
                arguments("진1926 판교점", "106771056", List.of("양살치살 (130g)", "진모듬 (2인)", "양갈비 (1인분)", "양등심 (1인분)", "양삼겹 (1인분)", "프렌치랙 (1인분)")),
                arguments("칸스테이크하우스", "24529744", List.of("티본스테이크세트(2인)", "치오피노")),
                arguments("일호선", "1879186093", List.of("한우 육회", "한우 육전", "일호선 떡갈비", "골뱅이 소면", "감자채전", "메밀전병", "냉.수.육", "닭 묵은지 찜", "삼겹쪽파말이", "새우새우부추전", "얼큰수제비찌개", "먹태", "고기국수", "우렁이쌀dry", "해창 6도", "가야", "고택찹쌀생주", "회양산 15도", "선호", "산아래", "이백원주", "호랑이배꼽", "대대포", "그래그날", "지란지교", "나루", "DOK", "복순도가 손막걸리", "꽃잠", "소주", "맥주", "콜라", "사이다", "제로콜라")),
                arguments("라디오베이", "26628252", List.of("스프라이트", "칠리치즈프라이", "특제시즈닝으로스모크", "오렌지주스", "크랜베리주스", "핫버팔로윙", "웰치스포도", "맥앤치즈")),
                arguments("돈까스 정석", "689062252", List.of("정필종 등심 돈까스", "프리미엄 등심 돈까스", "스페셜 특등심 돈까스", "정스페셜 치즈 돈까스", "등심 왕새우 돈까스")),
                arguments("마실 둔산점", "20995567", List.of("평일점심 A", "평일점심 B", "귀한정식 A", "귀한정식 B", "귀한정식 C")),
                arguments("김주학짬뽕", "1399098939", List.of("엄나무 고기짬뽕", "엄나무 짬뽕", "엄나무 짜장면", "탕수육(소)", "냉우동")),
                arguments("송계옥 판교점", "734876697", List.of("모둠구이 (대)", "모둠구이 (중)", "사이소금", "염통", "근위", "연골")),
                arguments("땀땀 갤러리아백화점 광교점", "2130164972", List.of("프리미엄 보양 쌀국수", "양지 쌀국수", "매운 우삼겹 쌀국수", "하노이 분짜", "코코넛쉬림프&크리스피롤(4ea)")),
                arguments("꽃돌게장1번가", "26577684", List.of("꽃게탕1인분 (2인이상 주문가능)", "갈치조림정식1인분(2인이상 주문가능)", "꽃게정식 (1인)", "왕꽃게정식 (1인)", "어린이 수제 돈까스", "새우장 / 5마리 (추가메뉴)", "갈치조림 (추가메뉴)", "양념꽃게장 (추가메뉴)", "전복장 / 1마리 (추가메뉴)", "간장꽃게장 / 1마리 (추가메뉴)", "왕간장꽃게장 / 1마리 (추가메뉴)")),
                arguments("맥도날드 수원아주대점", "25001083", List.of("빅맥", "더블 불고기 버거", "슈비버거", "슈슈버거", "맥스파이시 상하이 버거", "아메리카노", "1955버거", "오레오 맥플러리", "딸기 오레오 맥플러리", "초코 오레오 맥플러리", "에그 불고기버거", "필레오피쉬", "보성녹돈버거", "더블 빅맥", "빅맥BLT", "맥크리스피")),
                arguments("봉밀가", "26794399", List.of("평양 메밀물국수", "평양 메밀비빔국수", "뜨거운 돌메밀", "메밀전", "평양고기만두", "평양구운고기만두", "기장 돌냄비 우동", "평양 찐 고기만두")),
                arguments("스미비클럽", "866016092", List.of("19:30 이후 단품으로도 주문 가능", "오마카세")),
                arguments("연술집", "1395506113", List.of("탄탄스지전골", "스지탕", "삼겹숙주볶음", "닭다리살크림우동", "물만두계란탕", "바삭한먹태")),
                arguments("코드야드메리어트 서울판교 모모카페", "24829272", List.of())    // 메뉴가 없는 경우
        );
    }
}