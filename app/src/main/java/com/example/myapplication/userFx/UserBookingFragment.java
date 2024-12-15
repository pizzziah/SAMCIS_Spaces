package com.example.myapplication.userFx;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserBookingFragment extends Fragment {

    private RecyclerView recyclerPast;
    private BookingAdapter pastAdapter;
    private List<Booking> pastList = new ArrayList<>();
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.u_fragment_booking, container, false);

        // Initialize views
        recyclerPast = view.findViewById(R.id.recyclerPast);

        db = FirebaseFirestore.getInstance();

        setupRecyclerViews();
        fetchBookings();

        return view;
    }

    private void setupRecyclerViews() {
        // Set layout managers for RecyclerViews
        recyclerPast.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize adapters
        pastAdapter = new BookingAdapter(pastList, db);

        // Set adapters
        recyclerPast.setAdapter(pastAdapter);
    }

    private void fetchBookings() {
        // Get the current user's ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("Users")
                .document(userId) // Access the user's document
                .collection("bookings") // Access the user's bookings collection
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    pastList.clear(); // Clear the list to prevent duplicates

                    if (queryDocumentSnapshots != null) {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            // Retrieve data from Firestore document
                            String date = doc.getString("date");
                            String venueName = doc.getString("venueName");
                            String id = doc.getId(); // Document ID

                            // Add a new Booking object to the list
                            Booking booking = new Booking(id, venueName, date);
                            pastList.add(booking);
                        }

                        // Notify the adapter that the data has changed
                        pastAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle any errors
                    e.printStackTrace();
                });
    }

}