package com.example.myapplication.userFx;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdateBookingActivity extends AppCompatActivity {

    private EditText editTextVenueName, editTextDate;
    private Button buttonUpdate;
    private FirebaseFirestore db;

    private String bookingId; // ID of the booking being updated

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_booking);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Bind views
        editTextVenueName = findViewById(R.id.editTextVenueName);
        editTextDate = findViewById(R.id.editTextDate);
        buttonUpdate = findViewById(R.id.buttonUpdate);

        // Retrieve booking ID and details passed from the previous activity
        bookingId = getIntent().getStringExtra("bookingId");
        String venueName = getIntent().getStringExtra("venueName");
        String date = getIntent().getStringExtra("date");

        // Check if any of the data is missing
        if (venueName == null || date == null || bookingId == null) {
            Toast.makeText(this, "Invalid booking details", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if data is missing
            return;
        }

        // Populate existing booking details
        editTextVenueName.setText(venueName);
        editTextDate.setText(date);

        // Handle Update button click
        buttonUpdate.setOnClickListener(v -> updateBooking());
    }


    private void updateBooking() {
        String newVenueName = editTextVenueName.getText().toString().trim();
        String newDate = editTextDate.getText().toString().trim();

        if (newVenueName.isEmpty() || newDate.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Access Firestore and update the booking
        db.collection("Users") // Access the Users collection
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid()) // Current user's document
                .collection("bookings") // bookings collection under the current user
                .document(bookingId) // The specific booking document
                .update("venueName", newVenueName, "date", newDate) // Fields to update
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Booking updated successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity after success
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update booking", Toast.LENGTH_SHORT).show();
                });
    }

}