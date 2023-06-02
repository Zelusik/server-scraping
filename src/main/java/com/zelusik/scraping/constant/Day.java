package com.zelusik.scraping.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
@Getter
public enum Day {

    MON("월"),
    TUE("화"),
    WED("수"),
    THU("목"),
    FRI("금"),
    SAT("토"),
    SUN("일");

    private final String description;

    public static Day valueOfDescription(char description) {
        return valueOfDescription(String.valueOf(description));
    }

    public static Day valueOfDescription(String description) {
        return Arrays.stream(values())
                .filter(value -> value.getDescription().equals(description))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static List<Day> getValuesInRange(Day start, Day end) {
        List<Day> result = new LinkedList<>();
        boolean continueAdding = false;

        for (Day day : values()) {
            if (day == start) continueAdding = true;
            if (continueAdding) result.add(day);
            if (day == end) break;
        }

        return result;
    }
}
