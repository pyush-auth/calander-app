package com.org.wissen;

import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;

@Service
public class CalenderService {

    public ArrayList<CalenderDay> getMonthInfo(int year, int month) {
        ArrayList<CalenderDay> result = new ArrayList<>();
        try {
            DayOfWeek[] days = DayOfWeek.values();
            DayOfWeek startDayOfWeek = days[new Random().nextInt(7)];
//            System.out.println("Random Start Day: " + startDayOfWeek);

            YearMonth yearMonth = YearMonth.of(year, month);


            // Start from 1st day of month, override its day name with random one
            LocalDate startDate = yearMonth.atDay(1);

            int startDayIndex = startDayOfWeek.getValue() % 7; // Mon=1..Sun=7 → 0..6


            for (int i = 0; i < startDate.lengthOfMonth(); i++) {
                int dayOfMonth = i + 1;

                // Calculate artificial day of week
                int currentDayIndex = (startDayIndex + i) % 7;
                DayOfWeek currentDayOfWeek = DayOfWeek.of((currentDayIndex == 0 ? 7 : currentDayIndex));

                int weekNumber = (startDayIndex + i) / 7 + 1;

                Random random = new Random();
                Holiday holiday = null;
                boolean result1 = random.nextDouble() < 0.3;
                String weekDay = currentDayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
                if (result1 || weekDay.equals("Sun") || weekDay.equals("Sat")) {
                    holiday = new Holiday("IN", "ALL", (weekDay.equals("Sun") || weekDay.equals("Sat")) ? "Weekend holiday" : "Holiday");
                }
                CalenderDay calenderDay = new CalenderDay(
                        dayOfMonth,
                        weekDay,
                        weekNumber,
                        holiday);

                System.out.println("Day | Day | Week");
                System.out.println(calenderDay.dayName + "  " + calenderDay.dayNumber + "   " + calenderDay.weekNumber + "  ");
                System.out.println("----|-----|------");


                result.add(calenderDay);

            }
            System.out.println("Day | Day | Week");
            System.out.println("----|-----|------");
            result.forEach(System.out::println);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return result;
    }

    public Map<Integer, String> buildWeeklyColorData(ArrayList<CalenderDay> calenderDayObjects) {
        Map<Integer, String> colours = new HashMap<>();
        Map<Integer, Integer> holidays = new HashMap<>();
        for (CalenderDay d : calenderDayObjects) {
            if (Objects.equals(d.dayName, "Sun") || Objects.equals(d.dayName, "Sat") || d.holiday == null) {
                continue;
            }
            holidays.put(d.weekNumber, holidays.getOrDefault(d.weekNumber, 0) + 1);
        }
        for (int w : holidays.keySet()) {
//            System.out.print(w);
//            System.out.println(holidays.get(w));
            if (holidays.get(w) == 1) {
                colours.put(w, "#ADFF2F");
            } else if (holidays.get(w) == 2) {
                colours.put(w, "#7CFC00");
            } else if (holidays.get(w) == 3) {
                colours.put(w, "#32CD32");
            } else if (holidays.get(w) == 4) {
                colours.put(w, "#228B22");
            } else if (holidays.get(w) == 5) {
                colours.put(w, "#006400");
            } else {

            }
        }
        return colours;
    }
}
