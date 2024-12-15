package com.example.myapplication.adminFx;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.userFx.bookingConfirmation;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminHomeFragment extends Fragment {

    private static final String TAG = "AdminHomeFragment"; // Centralized tag for logs
    private FirebaseFirestore db; // Firestore instance

    // Constructor
    public AdminHomeFragment() {
        // Default constructor required
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance(); // Initialize Firestore
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate layout
        View view = inflater.inflate(R.layout.a_fragment_home, container, false);

        Log.d(TAG, "Initializing UI components");

        // Buttons for fetching specific venue details
        Button pendingButton = view.findViewById(R.id.pendingBttn);
        Button approvedButton = view.findViewById(R.id.approvedBttn);
        Button approveButton = view.findViewById(R.id.buttonApprove);
        Button denyButton = view.findViewById(R.id.buttonDeny);

        // Assign Firestore document IDs to buttons
        pendingButton.setOnClickListener(v -> fetchBookingDetails(""));
        approvedButton.setOnClickListener(v -> fetchBookingDetails(""));
        approveButton.setOnClickListener(v -> fetchBookingDetails(""));
        denyButton.setOnClickListener(v -> fetchBookingDetails(""));

        return view;
    }

    /**
     * Fetch venue details from Firestore and handle the result.
     *
     * @param venueId The document ID of the venue.
     */
    private void fetchBookingDetails(String venueId) {
        Log.d(TAG, "Fetching Firestore document: " + venueId);

        DocumentReference venueRef = db.collection("Users").document(venueId);
        venueRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    Log.d(TAG, "Document data: " + document.getData());

                    // Retrieve fields from Firestore document
                    Boolean venueAvailability = document.getBoolean("available");
                    String venueFloor = document.getString("floor");
                    String venueName = document.getString("name");

                    if (venueName != null && venueFloor != null) {
                        // Proceed to open Booking Activity with dynamic data
                        openBookingActivity(venueName, venueFloor, venueAvailability);
                    } else {
                        showToast("Venue details are incomplete");
                        Log.w(TAG, "Venue details are missing fields.");
                    }
                } else {
                    showToast("Venue details not found");
                    Log.w(TAG, "No such document for ID: " + venueId);
                }
            } else {
                showToast("Failed to load venue details");
                Log.e(TAG, "Error fetching document: ", task.getException());
            }
        });
    }

    /**
     * Launch the BookingConfirmation activity with venue details.
     *
     * @param name        Venue name
     * @param floor       Venue floor
     * @param isAvailable Availability status of the venue
     */
    private void openBookingActivity(String name, String floor, Boolean isAvailable) {
        Intent intent = new Intent(getActivity(), bookingConfirmation.class);
        intent.putExtra("VENUE_NAME", name);
        intent.putExtra("VENUE_FLOOR", floor);
        intent.putExtra("VENUE_AVAILABLE", isAvailable != null && isAvailable);
        startActivity(intent);
    }

    /**
     * Show a short toast message.
     *
     * @param message The message to display.
     */
    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
