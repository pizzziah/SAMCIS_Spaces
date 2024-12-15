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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class bookingConfirmation extends AppCompatActivity {

    private Button bookNowButton;
    private FirebaseFirestore db;
    private Dialog popupDialog;
    private TextView venueNameTextView, venueFloorTextView, venueAvailabilityTextView, bookingDateTextView;
    private String selectedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirmation);

        db = FirebaseFirestore.getInstance();

        // Get the venue details passed from the previous activity (UserBookingFragment)
        Intent intent = getIntent();
        String venueName = intent.getStringExtra("venueName");
        String venueFloor = intent.getStringExtra("venueFloor");
        String venueImageUrl = intent.getStringExtra("venueImageUrl");
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
        bookNowButton.setOnClickListener(view -> showBookingPopup(venueName));
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

    // Method to show the booking confirmation popup
    private void showBookingPopup(String venueId) {
        popupDialog = new Dialog(this);
        popupDialog.setContentView(R.layout.popup_booking_confirmation);
        popupDialog.setCancelable(false); // Prevent dialog dismissal on outside touch

        TextView popupVenueName = popupDialog.findViewById(R.id.popupVenueName);
        TextView popupBookingDate = popupDialog.findViewById(R.id.popupBookingDate);
        TextView popupAvailability = popupDialog.findViewById(R.id.popupBookingAvailability); // Updated to show availability
        Button confirmButton = popupDialog.findViewById(R.id.confirmButton);

        popupVenueName.setText("Venue: " + venueId);
        popupBookingDate.setText("Date: " + (selectedDate.isEmpty() ? "Not selected" : selectedDate));

        // Fetch venue details from Firestore
        DocumentReference venueRef = db.collection("venues").document(venueId);
        venueRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String venueName = documentSnapshot.getString("name");
                String floor = documentSnapshot.getString("floor");
                Boolean available = documentSnapshot.getBoolean("available");

                popupVenueName.setText("Venue: " + venueName);
                popupBookingDate.setText("Floor: " + floor);

                // Update the availability instead of the time
                popupAvailability.setText("Availability: " + (available != null && available ? "Available" : "Not Available"));
            } else {
                Toast.makeText(this, "Venue data not found!", Toast.LENGTH_SHORT).show();
            }
        });

        confirmButton.setOnClickListener(view -> {
            if (selectedDate.isEmpty()) {
                Toast.makeText(this, "Please select a date before confirming the booking!", Toast.LENGTH_SHORT).show();
            } else {
                // Confirm the booking
                Toast.makeText(this, "Booking Confirmed for " + selectedDate, Toast.LENGTH_SHORT).show();
                popupDialog.dismiss();
            }
        });

        popupDialog.show();
    }
}