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

    private static final String TAG = "AdminHomeFragment"; // For logging
    private FirebaseFirestore db; // Firestore instance

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

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.a_fragment_home, container, false);
        Log.d(TAG, "AdminHomeFragment View initialized");

        // Initialize buttons
        Button pendingButton = view.findViewById(R.id.pendingBttn);
        Button approvedButton = view.findViewById(R.id.approvedBttn);
        Button approveButton = view.findViewById(R.id.buttonApprove);
        Button denyButton = view.findViewById(R.id.buttonDeny);

        // Button click listeners
        pendingButton.setOnClickListener(v -> fetchBookingDetails("pending_doc_id"));
        approvedButton.setOnClickListener(v -> fetchBookingDetails("approved_doc_id"));
        approveButton.setOnClickListener(v -> fetchBookingDetails("approve_doc_id"));
        denyButton.setOnClickListener(v -> fetchBookingDetails("deny_doc_id"));

        return view;
    }

    /**
     * Fetch booking details from Firestore based on document ID.
     *
     * @param venueId The document ID to fetch data from Firestore.
     */
    private void fetchBookingDetails(String venueId) {
        Log.d(TAG, "Fetching Firestore document: " + venueId);

        DocumentReference venueRef = db.collection("Bookings").document(venueId);
        venueRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    Log.d(TAG, "Document data: " + document.getData());

                    // Fetch fields
                    Boolean venueAvailability = document.getBoolean("available");
                    String venueFloor = document.getString("floor");
                    String venueName = document.getString("name");

                    if (venueName != null && venueFloor != null) {
                        openBookingActivity(venueName, venueFloor, venueAvailability);
                    } else {
                        showToast("Venue details are incomplete");
                        Log.w(TAG, "Missing fields in the document");
                    }
                } else {
                    showToast("No venue details found");
                    Log.w(TAG, "No document found for ID: " + venueId);
                }
            } else {
                showToast("Error loading venue details");
                Log.e(TAG, "Firestore error: ", task.getException());
            }
        });
    }

    /**
     * Launch the BookingConfirmation activity with venue details.
     */
    private void openBookingActivity(String name, String floor, Boolean isAvailable) {
        Intent intent = new Intent(getActivity(), bookingConfirmation.class);
        intent.putExtra("VENUE_NAME", name);
        intent.putExtra("VENUE_FLOOR", floor);
        intent.putExtra("VENUE_AVAILABLE", isAvailable != null && isAvailable);
        startActivity(intent);
    }

    /**
     * Display a short Toast message.
     */
    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
