package com.example.myapplication.userFx;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UpdateBookingActivity extends AppCompatActivity {

    private EditText editTextDate;
    private Button buttonUpdate;
    private FirebaseFirestore db;

    private String bookingId; // ID of the booking being updated

    private List<String> bookedDates = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_booking);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Bind views
        editTextDate = findViewById(R.id.editTextDate);
        buttonUpdate = findViewById(R.id.buttonUpdate);

        // Retrieve booking ID and details passed from the previous activity
        bookingId = getIntent().getStringExtra("bookingId");
        String date = getIntent().getStringExtra("date");

        // Check if any of the data is missing
        if (date == null || bookingId == null) {
            Toast.makeText(this, "Invalid booking details", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if data is missing
            return;
        }
        fetchBookedDates();

        // Populate existing booking details
        editTextDate.setText(date);

        // Set click listener for the date EditText to open DatePickerDialog
        editTextDate.setOnClickListener(v -> showDatePickerDialog());

        // Handle Update button click
        buttonUpdate.setOnClickListener(v -> updateBooking());
    }
    private void fetchBookedDates() {
        db.collection("Users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("bookings")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    bookedDates.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String date = document.getString("date");
                        if (date != null && !document.getId().equals(bookingId)) { // Exclude current booking
                            bookedDates.add(date);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load booked dates", Toast.LENGTH_SHORT).show();
                });
    }

    private void showDatePickerDialog() {
        // Get current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String formattedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;

                    if (bookedDates.contains(formattedDate)) {
                        Toast.makeText(this, "This date is already booked.", Toast.LENGTH_SHORT).show();
                    } else {
                        editTextDate.setText(formattedDate); // Update the date
                    }
                },
                year,
                month,
                day
        );

        // Disable past dates
        DatePicker datePicker = datePickerDialog.getDatePicker();
        datePicker.setMinDate(System.currentTimeMillis() - 1000);

        // Add listener to block booked dates
        datePicker.init(year, month, day, (view, year1, month1, dayOfMonth) -> {
            String formattedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
            if (bookedDates.contains(formattedDate)) {
                Toast.makeText(this, "This date is not available.", Toast.LENGTH_SHORT).show();
                view.updateDate(year, month, day); // Revert to a default date
            }
        });

        datePickerDialog.show();
    }

    private void updateBooking() {
        String newDate = editTextDate.getText().toString().trim();

        if (newDate.isEmpty()) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        // Access Firestore and update the booking
        db.collection("Users") // Access the Users collection
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid()) // Current user's document
                .collection("bookings") // bookings collection under the current user
                .document(bookingId) // The specific booking document
                .update("date", newDate) // Fields to update
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Booking updated successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity after success
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update booking", Toast.LENGTH_SHORT).show();
                });
    }
}