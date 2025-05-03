package com.org.wissen;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CalendarViewController {


    @GetMapping("/calendar-view")
    public String showCalendarPage() {
        return "calendar";
    }
}
