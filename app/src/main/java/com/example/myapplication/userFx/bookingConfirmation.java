package com.example.myapplication.userFx;

import android.app.Dialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

import java.util.Calendar;

public class bookingConfirmation extends AppCompatActivity {

    private Button bookNowButton;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private Dialog popupDialog;
    private TextView venueNameTextView, venueFloorTextView, venueAvailabilityTextView, bookingDateTextView;
    private String selectedDate = "";

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

        // Set the initial venue details on the UI
        venueNameTextView.setText("Venue: " + venueName);
        venueFloorTextView.setText("Floor: " + venueFloor);
        venueAvailabilityTextView.setText("Availability: " + (venueAvailability ? "Available" : "Not Available"));

        // Set up the date picker dialog for booking date
        bookingDateTextView.setOnClickListener(view -> showDatePickerDialog());

        // Handle Book Now button click
        bookNowButton.setOnClickListener(view -> saveBookingData(venueName));
    }

    // Method to show the calendar dialog for date selection
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
            selectedDate = selectedDayOfMonth + "/" + (selectedMonth + 1) + "/" + selectedYear;  // Format date as DD/MM/YYYY
            bookingDateTextView.setText("Date: " + selectedDate);
        }, year, month, day);

        datePickerDialog.show();
    }


    // Method to save the booking data to Firestore
    private void saveBookingData(String venueName) {
        // Check if a date is selected
        if (selectedDate.isEmpty()) {
            Toast.makeText(this, "Please select a date before confirming the booking!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the current user ID from Firebase Authentication
        String userId = auth.getCurrentUser().getUid();

        // Get venue name (for example, from Intent or UI)
        String venueName1 = venueName;

        // Create a reference to the bookings collection of the current user in Firestore
        DocumentReference bookingRef = db.collection("Users")
                .document(userId) // Access the user's document by UID
                .collection("bookings") // Access the bookings collection
                .document(); // Automatically generate a new document ID for each booking

        // Create a new Booking object with the user ID, venue name, and selected date
        Booking booking = new Booking(userId, venueName1, selectedDate);

        // Save the selected date to Firestore
        bookingRef.set(booking)
                .addOnSuccessListener(aVoid -> {
                    // Successfully saved booking
                    Toast.makeText(this, "Booking confirmed for " + selectedDate, Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity after booking is confirmed
                })
                .addOnFailureListener(e -> {
                    // Failed to save booking
                    Toast.makeText(this, "Failed to save booking: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}
