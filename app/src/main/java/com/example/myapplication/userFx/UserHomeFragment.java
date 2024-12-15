package com.example.myapplication.userFx;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class UserHomeFragment extends Fragment {

    private FirebaseFirestore db; // Firestore instance

    public UserHomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.u_fragment_home, container, false);

        Log.e("UserBookingFragment", "Venue");

        // Buttons for venues
        Button buttonDevesse = view.findViewById(R.id.check_availability_button_devesse);
        Button buttonAmphi = view.findViewById(R.id.check_availability_button_amphi);
        Button buttonOval = view.findViewById(R.id.check_availability_button_oval);
        Button buttonLab = view.findViewById(R.id.check_availability_button_lab);

        // Set listeners for each button
        buttonDevesse.setOnClickListener(v -> fetchVenueDetails("AjNc3sDvHrvNIH4fXNvd"));
        buttonAmphi.setOnClickListener(v -> fetchVenueDetails("mm4OzygytxnJ6oG3JDel"));
        buttonOval.setOnClickListener(v -> fetchVenueDetails("yjFP5Z7Wy5IgXCNQbBKp"));
        buttonLab.setOnClickListener(v -> fetchVenueDetails("s74Ke9aLcijQz0cE9yla"));

        return view;
    }

    private void fetchVenueDetails(String venueId) {
        Log.e("UserBookingFragment", "Fetching Firestore document: " + venueId);

        // Reference to the Firestore document for the venue
        DocumentReference venueRef = db.collection("venues").document(venueId);

        // Fetch the document from Firestore
        venueRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.e("UserBookingFragment", "DocumentSnapshot data: " + document.getData());

                    // Fetch data with correct field names
                    Boolean venueAvailability = document.getBoolean("available");
                    String venueFloor = document.getString("floor");
                    String venueName = document.getString("name");


                    if (venueName != null && venueFloor != null) {
                        // Pass dynamic data to the booking activity
                        openBookingActivity(venueName, venueFloor, venueAvailability);
                    } else {
                        Log.e("UserBookingFragment", "Venue details are incomplete");
                        Toast.makeText(getActivity(), "Venue details are incomplete", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("UserBookingFragment", "No such document found for " + venueId);
                    Toast.makeText(getActivity(), "Venue details not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("UserBookingFragment", "Fetch failed: " + task.getException());
                Toast.makeText(getActivity(), "Failed to load venue details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Open the booking activity with fetched data
    private void openBookingActivity(String venueName, String venueFloor, Boolean venueAvailability) {
        Log.e("UserBookingFragment", "Trying to open Booking Activity");

        Intent intent = new Intent(getActivity(), bookingConfirmation.class);

        // Pass dynamic data to the activity
        intent.putExtra("venueName", venueName);
        intent.putExtra("venueFloor", venueFloor);
        intent.putExtra("venueAvailability", venueAvailability);

        // Start the activity
        startActivity(intent);
    }
}