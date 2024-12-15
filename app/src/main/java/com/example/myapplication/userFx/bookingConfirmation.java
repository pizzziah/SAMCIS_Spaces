package com.example.myapplication.userFx;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class bookingConfirmation extends AppCompatActivity {

    private Button bookNowButton, cancelButton;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private TextView venueNameTextView, venueFloorTextView, venueAvailabilityTextView, bookingDateTextView;
    private String selectedDate = "";
    private List<String> bookedDates = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirmation);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Get the venue details passed from the previous activity (UserBookingFragment)
        Intent intent = getIntent();
        String venueName = intent.getStringExtra("venueName");
        String venueFloor = intent.getStringExtra("venueFloor");
        Boolean venueAvailability = intent.getBooleanExtra("venueAvailability", false);

        // Initialize UI elements
        venueNameTextView = findViewById(R.id.venueName);
        venueFloorTextView = findViewById(R.id.venueFloor);
        venueAvailabilityTextView = findViewById(R.id.venueAvailability);
        bookingDateTextView = findViewById(R.id.bookingDate);
        bookNowButton = findViewById(R.id.bookBttn);
        cancelButton = findViewById(R.id.cancelBttn);

        // Set the initial venue details on the UI
        venueNameTextView.setText("Venue: " + venueName);
        venueFloorTextView.setText("Floor: " + venueFloor);
        venueAvailabilityTextView.setText("Availability: " + (venueAvailability ? "Available" : "Not Available"));

        // Set up the date picker dialog for booking date
        bookingDateTextView.setOnClickListener(view -> showDatePickerDialog());

        // Handle Book Now button click
        bookNowButton.setOnClickListener(view -> saveBookingData(venueName));

        // Handle Cancel button click
        cancelButton.setOnClickListener(view -> finish());

        // Fetch booked dates from Firestore
        fetchBookedDates(venueName);
    }

    // Method to fetch booked dates for the specific venue
    private void fetchBookedDates(String venueName) {
        String userId = auth.getCurrentUser().getUid();

        db.collection("Users")
                .document(userId)
                .collection("bookings")
                .whereEqualTo("venueName", venueName)  // Filter by venueName
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    bookedDates.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String date = doc.getString("date");
                        if (date != null) {
                            bookedDates.add(date);  // Add only dates for this venue
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to fetch booked dates: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    // Method to show the calendar dialog for date selection
    @SuppressLint("NewApi")
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
            selectedDate = selectedDayOfMonth + "/" + (selectedMonth + 1) + "/" + selectedYear;  // Format date as DD/MM/YYYY
            bookingDateTextView.setText("Date: " + selectedDate);

            // Check if the selected date is already booked
            String formattedDate = selectedDayOfMonth + "/" + (selectedMonth + 1) + "/" + selectedYear;

            if (bookedDates.contains(formattedDate)) {
                // Show a Toast message that the selected date is not available
                Toast.makeText(this, "The selected date is already booked.", Toast.LENGTH_SHORT).show();
                bookingDateTextView.setText("Date: Not Available");
                selectedDate = "";  // Clear the selected date if it's already booked
            }
        }, year, month, day);

        // Set a custom date set listener (This part ensures that booked dates don't affect the calendar)
        datePickerDialog.getDatePicker().setOnDateChangedListener((view, year1, month1, dayOfMonth) -> {
            String formattedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;

            // When the date is selected, check if it's booked
            if (bookedDates.contains(formattedDate)) {
                // If the date is booked, show a Toast message and prevent selection
                Toast.makeText(this, "This date is not available. Please choose another date.", Toast.LENGTH_SHORT).show();
                bookingDateTextView.setText("Date: Not Available");
                selectedDate = "";  // Clear the selected date if it's already booked
            }
        });

        datePickerDialog.show();
    }


    // Method to save the booking data to Firestore
    private void saveBookingData(String venueName) {
        if (selectedDate.isEmpty()) {
            Toast.makeText(this, "Please select a date before confirming the booking!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        Booking booking = new Booking(userId, venueName, selectedDate);

        DocumentReference bookingRef = db.collection("Users")
                .document(userId)
                .collection("bookings")
                .document();

        bookingRef.set(booking)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Booking confirmed for " + selectedDate, Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to save booking: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}