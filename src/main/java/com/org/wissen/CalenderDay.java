package com.org.wissen;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class CalenderDay {
    int dayNumber;
    String dayName;
    int weekNumber;
    Holiday holiday;
}
