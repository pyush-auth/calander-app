package com.org.wissen;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoogleCalendarFetcher {
    private static final String APPLICATION_NAME = "Google Calendar API Java";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = List.of("https://www.googleapis.com/auth/calendar.readonly");
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private static Credential getCredentials() throws IOException, GeneralSecurityException {
        InputStream in = GoogleCalendarFetcher.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new com.google.api.client.util.store.FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    public List<Event> fetchCalendarEvents(int year, int month) throws IOException, GeneralSecurityException {
        Calendar service = new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                getCredentials())
                .setApplicationName(APPLICATION_NAME)
                .build();

        // Create LocalDateTime for the start and end of the month
        LocalDateTime startDate = LocalDate.of(year, month, 1).atStartOfDay();
        LocalDateTime endDate = startDate.plusMonths(1);

        // Convert LocalDateTime to ZonedDateTime (use UTC)
        ZonedDateTime startZonedDateTime = startDate.atZone(ZoneId.of("UTC"));
        ZonedDateTime endZonedDateTime = endDate.atZone(ZoneId.of("UTC"));

        // Format to ISO 8601 string: "yyyy-MM-dd'T'HH:mm:ss'Z'"
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        String formattedStartDate = startZonedDateTime.format(formatter).replaceAll("\\[UTC\\]", "");
        String formattedEndDate = endZonedDateTime.format(formatter).replaceAll("\\[UTC\\]", "");

        // Create DateTime objects using the formatted strings
        com.google.api.client.util.DateTime timeMin = new com.google.api.client.util.DateTime(formattedStartDate);
        com.google.api.client.util.DateTime timeMax = new com.google.api.client.util.DateTime(formattedEndDate);

        // Fetch events from Google Calendar
        Events events = service.events().list("primary")
                .setTimeMin(timeMin)
                .setTimeMax(timeMax)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        List<Event> items = events.getItems();

        if (items.isEmpty()) {
            System.out.println("No events found for " + year + "-" + month);
        } else {
            System.out.println("Events for " + year + "-" + month + ":");
            for (Event event : items) {
                String start = event.getStart().getDateTime() != null ?
                        event.getStart().getDateTime().toStringRfc3339() :
                        event.getStart().getDate().toStringRfc3339();
                System.out.printf("%s: %s%n", start, event.getSummary());
            }
        }
        return items;
    }

    public ArrayList<CalenderDay> getHolidays(int year, int month) throws IOException, GeneralSecurityException {
        List<Event> events = fetchCalendarEvents(year, month);
        ArrayList<CalenderDay> holidays = new ArrayList<>();

        for (Event event : events) {
            if (event.getStart() != null && event.getStart().getDate() != null) {
                LocalDate date = LocalDate.parse(event.getStart().getDate().toString());
                if (date.getYear() == year && date.getMonthValue() == month) {
                    Holiday holiday = new Holiday(event.getSummary(), "IN", "Bangalore");
                    CalenderDay day = new CalenderDay(date.getDayOfMonth(), date.getDayOfWeek().name().substring(0, 3), date.get(ChronoField.ALIGNED_WEEK_OF_MONTH), holiday);
                    holidays.add(day);
                }
            }
        }
        return holidays;
    }
}
