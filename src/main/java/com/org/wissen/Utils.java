package com.org.wissen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class Utils {

    @Autowired
    private CalenderService calenderService;
    public static Map<Integer, Map<Integer, ArrayList<CalenderDay>>> calenderDayObjects = new HashMap<>();

    public synchronized ArrayList<CalenderDay> generateHolidays(int year, int month, boolean randomHoliday) {
        if (calenderDayObjects.containsKey(year) && calenderDayObjects.get(year).containsKey(month)) {
            return calenderDayObjects.get(year).get(month);
        } else if (!calenderDayObjects.containsKey(year)) {
            ArrayList<CalenderDay> temp = calenderService.getMonthInfo(year, month, randomHoliday);
            Map<Integer, ArrayList<CalenderDay>> tempMap = new HashMap<>();
            tempMap.put(month, temp);
            calenderDayObjects.put(year, tempMap);
            return calenderDayObjects.get(year).get(month);
        } else if (calenderDayObjects.containsKey(year) && !calenderDayObjects.get(year).containsKey(month)) {
            ArrayList<CalenderDay> temp = calenderService.getMonthInfo(year, month, randomHoliday);
            Map<Integer, ArrayList<CalenderDay>> yearMAP = calenderDayObjects.get(year);
            yearMAP.put(month, temp);
            calenderDayObjects.put(year, yearMAP);
            return calenderDayObjects.get(year).get(month);
        }

        return null;
    }
}
