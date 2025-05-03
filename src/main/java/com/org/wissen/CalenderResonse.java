package com.org.wissen;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class CalenderResonse {
    ArrayList<CalenderDay> calenderDayObjects;
    Map<Integer, String> colors;
}
