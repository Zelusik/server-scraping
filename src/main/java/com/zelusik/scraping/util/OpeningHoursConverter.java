package com.zelusik.scraping.util;

import com.zelusik.scraping.constant.Day;
import com.zelusik.scraping.dto.place.OpeningHourDto;
import com.zelusik.scraping.dto.place.TimeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Component
public class OpeningHoursConverter {

    public List<OpeningHourDto> parseStrToOHs(String openingHours) {
        if (openingHours == null) return null;

        List<OpeningHourDto> result = new ArrayList<>();
        for (String oh : openingHours.split("\n")) {
            oh = oh.trim();

            if (oh.contains("라스트오더")) continue;
            if (oh.contains("휴게시간")) continue;

            if (oh.startsWith("매일")) {
                // ex. 매일 11:30 ~ 22:00
                TimeDto time = extractOpeningHoursTime(oh, 3);
                result.addAll(mapOpeningHours(Arrays.stream(Day.values()), time));
            } else if (oh.charAt(1) == '~') {
                // ex. 월~토 18:00 ~ 02:00
                Day startDay = Day.valueOfDescription(oh.charAt(0));
                Day endDay = Day.valueOfDescription(oh.charAt(2));
                List<Day> dayList = Day.getValuesInRange(startDay, endDay);
                TimeDto time = extractOpeningHoursTime(oh, 4);
                result.addAll(mapOpeningHours(dayList.stream(), time));
            } else if (oh.charAt(1) == ',') {
                // ex. 월,화,목,금,토,일 10:00 ~ 19:30
                int firstSpaceIdx = oh.indexOf(" ");
                List<Day> dayList = new ArrayList<>();
                for (int i = 0; i < firstSpaceIdx; i += 2) {
                    Day dayOfWeek = Day.valueOfDescription(oh.charAt(i));
                    dayList.add(dayOfWeek);
                }
                TimeDto time = extractOpeningHoursTime(oh, firstSpaceIdx + 1);
                result.addAll(mapOpeningHours(dayList.stream(), time));
            } else if (oh.charAt(1) == ' ') {
                // ex. 목 11:00 ~ 18:00
                Day day = Day.valueOfDescription(oh.charAt(0));
                TimeDto time = extractOpeningHoursTime(oh, 2);
                result.add(OpeningHourDto.of(day, time));
            } else {
                log.error("장소 영업시간이 처리할 수 없는 형태입니다. text={}", oh);
            }
        }

        // 요일 순서대로 정렬
        Comparator<OpeningHourDto> ohComparator = Comparator.comparing(oh -> oh.getDay().ordinal());
        return result.stream().sorted(ohComparator).toList();
    }

    /**
     * 영업시간 정보(String)를 받아 영업 시작 시간과 영업 종료 시간을 추출한다.
     *
     * @param openingHours   영업시간 정보.
     * @param openAtStartIdx 영업 시작 시간이 시작하는 index.
     * @return openingHours에서 추출한 영업 시작 시간, 종료 시간 정보가 담긴 객체.Q
     */
    private TimeDto extractOpeningHoursTime(String openingHours, int openAtStartIdx) {
        int closeAtStartIdx = openAtStartIdx + 5 + 3;

        String subStrOpenAt = openingHours.substring(openAtStartIdx, openAtStartIdx + 5);
        String subStrCloseAt = openingHours.substring(closeAtStartIdx, closeAtStartIdx + 5);
        LocalTime openAt = subStrOpenAt.equals("24:00") ? LocalTime.of(23, 59) : LocalTime.parse(subStrOpenAt);
        LocalTime closeAt = subStrCloseAt.equals("24:00") ? LocalTime.of(23, 59) : LocalTime.parse(subStrCloseAt);

        return TimeDto.of(openAt, closeAt);
    }

    private static List<OpeningHourDto> mapOpeningHours(Stream<Day> dayList, TimeDto time) {
        return dayList.map(day -> OpeningHourDto.of(day, time)).toList();
    }
}
