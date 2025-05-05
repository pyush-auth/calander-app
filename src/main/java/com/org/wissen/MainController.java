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

    @Autowired
    private Utils utils;


    @GetMapping(value = "/api/calender", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CalenderResonse> listAllFeatures(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam(name = "random-holiday", defaultValue = "false") boolean randomHoliday,
            @RequestParam(name = "only-holiday-week", defaultValue = "false") boolean onlyHolidayWeek
    ) {
        try {
            ArrayList<CalenderDay> calenderDayObjects = utils.generateHolidays(year, month, randomHoliday);
            ArrayList<CalenderDay> googleCalendar = googleCalendarFetcher.getHolidays(year, month);
            ArrayList<CalenderDay> holidaymakers = new ArrayList<>();

            Map<Integer, String> colors = calenderService.buildWeeklyColorData(calenderDayObjects);

            Map<Integer, CalenderDay> holidayMap = new HashMap<>();
            for (CalenderDay holiday : googleCalendar) {
                holidayMap.put(holiday.getDayNumber(), holiday);
            }


            for (CalenderDay day : calenderDayObjects) {
                if (holidayMap.containsKey(day.getDayNumber())) {
                    day.setHoliday(holidayMap.get(day.getDayNumber()).getHoliday());
                }

                if (colors.containsKey(day.getWeekNumber())) {
                    holidaymakers.add(day);
                }
            }


            return ResponseEntity.ok(new CalenderResonse(onlyHolidayWeek ? holidaymakers : calenderDayObjects, colors));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
