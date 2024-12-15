package com.example.myapplication.userFx;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Booking {

    private String id;
    private String venueName;
    private String date; // Format: "yyyy-MM-dd"

    // Default constructor (required for Firestore deserialization)
    public Booking() {}

    // Constructor
    public Booking(String id, String venueName, String date) {
        this.id = id;
        this.venueName = venueName;
        this.date = date;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // Check if the booking date is today
    public boolean isToday() {
        if (date == null || date.isEmpty()) return false;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try {
            Date bookingDate = sdf.parse(date);
            Calendar bookingCalendar = Calendar.getInstance();
            bookingCalendar.setTime(bookingDate);

            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            return bookingCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    bookingCalendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}