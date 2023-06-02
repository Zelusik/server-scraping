package com.zelusik.scraping.unit.util;

import com.zelusik.scraping.dto.place.OpeningHourDto;
import com.zelusik.scraping.dto.place.TimeDto;
import com.zelusik.scraping.util.OpeningHoursConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

import static com.zelusik.scraping.constant.Day.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("[Unit] 영업시간 converter test")
class OpeningHoursConverterTest {

    private final OpeningHoursConverter sut;

    public OpeningHoursConverterTest() {
        this.sut = new OpeningHoursConverter();
    }

    @DisplayName("문자열 형태의 영업시간이 주어지면, 영업시간을 List<OpeningHourDto> 형태로 변환한다.")
    @MethodSource("openingHoursTestData")
    @ParameterizedTest(name = "[{index}] openingHours={0}")
    void parseStringToOpeningHours(String openingHours, List<OpeningHourDto> expectedResult) {
        // given

        // when
        List<OpeningHourDto> actualResult = sut.parseStrToOHs(openingHours);

        // then
        assertThat(actualResult.size()).isEqualTo(expectedResult.size());
        for (int i = 0; i < actualResult.size(); i++) {
            assertThat(actualResult.get(i).getDay()).isEqualTo(expectedResult.get(i).getDay());
            assertThat(actualResult.get(i).getOpenAt()).isEqualTo(expectedResult.get(i).getOpenAt());
            assertThat(actualResult.get(i).getCloseAt()).isEqualTo(expectedResult.get(i).getCloseAt());
        }
    }

    static Stream<Arguments> openingHoursTestData() {
        return Stream.of(
                arguments(
                        "화~일 10:30 ~ 15:30",
                        List.of(OpeningHourDto.of(TUE, TimeDto.of(LocalTime.of(10, 30), LocalTime.of(15, 30))),
                                OpeningHourDto.of(WED, TimeDto.of(LocalTime.of(10, 30), LocalTime.of(15, 30))),
                                OpeningHourDto.of(THU, TimeDto.of(LocalTime.of(10, 30), LocalTime.of(15, 30))),
                                OpeningHourDto.of(FRI, TimeDto.of(LocalTime.of(10, 30), LocalTime.of(15, 30))),
                                OpeningHourDto.of(SAT, TimeDto.of(LocalTime.of(10, 30), LocalTime.of(15, 30))),
                                OpeningHourDto.of(SUN, TimeDto.of(LocalTime.of(10, 30), LocalTime.of(15, 30))))
                ),
                arguments(
                        "매일 07:30 ~ 24:00",
                        List.of(OpeningHourDto.of(MON, TimeDto.of(LocalTime.of(7, 30), LocalTime.of(23, 59))),
                                OpeningHourDto.of(TUE, TimeDto.of(LocalTime.of(7, 30), LocalTime.of(23, 59))),
                                OpeningHourDto.of(WED, TimeDto.of(LocalTime.of(7, 30), LocalTime.of(23, 59))),
                                OpeningHourDto.of(THU, TimeDto.of(LocalTime.of(7, 30), LocalTime.of(23, 59))),
                                OpeningHourDto.of(FRI, TimeDto.of(LocalTime.of(7, 30), LocalTime.of(23, 59))),
                                OpeningHourDto.of(SAT, TimeDto.of(LocalTime.of(7, 30), LocalTime.of(23, 59))),
                                OpeningHourDto.of(SUN, TimeDto.of(LocalTime.of(7, 30), LocalTime.of(23, 59))))
                ),
                arguments(
                        "월~토 11:00 ~ 01:00\n일 11:00 ~ 22:00",
                        List.of(OpeningHourDto.of(MON, TimeDto.of(LocalTime.of(11, 0), LocalTime.of(1, 0))),
                                OpeningHourDto.of(TUE, TimeDto.of(LocalTime.of(11, 0), LocalTime.of(1, 0))),
                                OpeningHourDto.of(WED, TimeDto.of(LocalTime.of(11, 0), LocalTime.of(1, 0))),
                                OpeningHourDto.of(THU, TimeDto.of(LocalTime.of(11, 0), LocalTime.of(1, 0))),
                                OpeningHourDto.of(FRI, TimeDto.of(LocalTime.of(11, 0), LocalTime.of(1, 0))),
                                OpeningHourDto.of(SAT, TimeDto.of(LocalTime.of(11, 0), LocalTime.of(1, 0))),
                                OpeningHourDto.of(SUN, TimeDto.of(LocalTime.of(11, 0), LocalTime.of(22, 0))))
                ),
                arguments(
                        "월,화,수,토,일 11:00 ~ 19:00\n목 11:00 ~ 18:00",
                        List.of(OpeningHourDto.of(MON, TimeDto.of(LocalTime.of(11, 0), LocalTime.of(19, 0))),
                                OpeningHourDto.of(TUE, TimeDto.of(LocalTime.of(11, 0), LocalTime.of(19, 0))),
                                OpeningHourDto.of(WED, TimeDto.of(LocalTime.of(11, 0), LocalTime.of(19, 0))),
                                OpeningHourDto.of(THU, TimeDto.of(LocalTime.of(11, 0), LocalTime.of(18, 0))),
                                OpeningHourDto.of(SAT, TimeDto.of(LocalTime.of(11, 0), LocalTime.of(19, 0))),
                                OpeningHourDto.of(SUN, TimeDto.of(LocalTime.of(11, 0), LocalTime.of(19, 0))))
                ),
                arguments(
                        "매일 11:30 ~ 22:00\n매일 라스트오더 ~ 21:00",
                        List.of(OpeningHourDto.of(MON, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(22, 0))),
                                OpeningHourDto.of(TUE, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(22, 0))),
                                OpeningHourDto.of(WED, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(22, 0))),
                                OpeningHourDto.of(THU, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(22, 0))),
                                OpeningHourDto.of(FRI, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(22, 0))),
                                OpeningHourDto.of(SAT, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(22, 0))),
                                OpeningHourDto.of(SUN, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(22, 0))))
                ),
                arguments(
                        "매일 11:30 ~ 21:30\n매일 휴게시간 15:00 ~ 17:00",
                        List.of(OpeningHourDto.of(MON, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(21, 30))),
                                OpeningHourDto.of(TUE, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(21, 30))),
                                OpeningHourDto.of(WED, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(21, 30))),
                                OpeningHourDto.of(THU, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(21, 30))),
                                OpeningHourDto.of(FRI, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(21, 30))),
                                OpeningHourDto.of(SAT, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(21, 30))),
                                OpeningHourDto.of(SUN, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(21, 30))))
                ),
                arguments(
                        "매일 11:30 ~ 22:00\n월~금 휴게시간 15:00 ~ 17:00\n토,일 휴게시간 15:30 ~ 17:00",
                        List.of(OpeningHourDto.of(MON, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(22, 0))),
                                OpeningHourDto.of(TUE, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(22, 0))),
                                OpeningHourDto.of(WED, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(22, 0))),
                                OpeningHourDto.of(THU, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(22, 0))),
                                OpeningHourDto.of(FRI, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(22, 0))),
                                OpeningHourDto.of(SAT, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(22, 0))),
                                OpeningHourDto.of(SUN, TimeDto.of(LocalTime.of(11, 30), LocalTime.of(22, 0))))
                ),
                arguments(
                        "월,화,목,금,토,일 10:00 ~ 19:30\n월,화,목,금,토,일 휴게시간 15:10 ~ 17:00",

                        List.of(OpeningHourDto.of(MON, TimeDto.of(LocalTime.of(10, 0), LocalTime.of(19, 30))),
                                OpeningHourDto.of(TUE, TimeDto.of(LocalTime.of(10, 0), LocalTime.of(19, 30))),
                                OpeningHourDto.of(THU, TimeDto.of(LocalTime.of(10, 0), LocalTime.of(19, 30))),
                                OpeningHourDto.of(FRI, TimeDto.of(LocalTime.of(10, 0), LocalTime.of(19, 30))),
                                OpeningHourDto.of(SAT, TimeDto.of(LocalTime.of(10, 0), LocalTime.of(19, 30))),
                                OpeningHourDto.of(SUN, TimeDto.of(LocalTime.of(10, 0), LocalTime.of(19, 30))))

                )
        );
    }
}