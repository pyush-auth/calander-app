package com.org.wissen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
public class MainController {

    @Autowired
    private CalenderService calenderService;

    @Autowired
    private GoogleCalendarFetcher googleCalendarFetcher;

    @GetMapping(value = "/api/calender", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CalenderResonse> listAllFeatures(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam(name = "random-holiday", defaultValue = "false") boolean randomHoliday) {
        try {
            ArrayList<CalenderDay> calenderDayObjects = calenderService.getMonthInfo(year, month, randomHoliday);
            ArrayList<CalenderDay> googleCalander = googleCalendarFetcher.getHolidays(year, month);

            Map<Integer, CalenderDay> holidayMap = new HashMap<>();
            for (CalenderDay holiday : googleCalander) {
                holidayMap.put(holiday.getDayNumber(), holiday);
            }

            for (CalenderDay day : calenderDayObjects) {
                if (holidayMap.containsKey(day.getDayNumber())) {
                    day.setHoliday(holidayMap.get(day.getDayNumber()).getHoliday());
                }
            }

            Map<Integer, String> colors = calenderService.buildWeeklyColorData(calenderDayObjects);

            return ResponseEntity.ok(new CalenderResonse(calenderDayObjects, colors));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
