package com.example.myapplication.userFx;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Booking {
    private String id;
    private String venueName;
    private String date; // Format: "yyyy-MM-dd"

    public Booking() {}

    public String getId() { return id; }
    public String getVenueName() { return venueName; }
    public String getDate() { return date; }

    public boolean isToday() {
        // Define date format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try {
            // Parse the booking date
            Date bookingDate = sdf.parse(date);

            // Get today's date with the same format
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            // Compare booking date and today's date
            return bookingDate.equals(today.getTime());

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}