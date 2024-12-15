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

    private RecyclerView recyclerToday, recyclerPast;
    private BookingAdapter todayAdapter, pastAdapter;
    private List<Booking> todayList = new ArrayList<>();
    private List<Booking> pastList = new ArrayList<>();
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.u_fragment_booking, container, false);

        // Initialize views
        recyclerToday = view.findViewById(R.id.recyclerToday);
        recyclerPast = view.findViewById(R.id.recyclerPast);

        db = FirebaseFirestore.getInstance();

        setupRecyclerViews();
        fetchBookings();

        return view;
    }

    private void setupRecyclerViews() {
        // Set layout managers for RecyclerViews
        recyclerToday.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerPast.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize adapters
        todayAdapter = new BookingAdapter(todayList, db);
        pastAdapter = new BookingAdapter(pastList, db);

        // Set adapters
        recyclerToday.setAdapter(todayAdapter);
        recyclerPast.setAdapter(pastAdapter);
    }

    private void fetchBookings() {
        // Fetch bookings for the current user
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Replace with dynamic user ID, e.g., FirebaseAuth.getInstance().getCurrentUser().getUid()

        db.collection("Users")
                .document(userId)  // Access the user's document by UID
                .collection("bookings")  // Access the bookings collection
                .get()  // Get all bookings for the user
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    todayList.clear();
                    pastList.clear();

                    if (queryDocumentSnapshots != null) {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            // Create a Booking object from the document
                            String date = doc.getString("date");  // Retrieve the date field
                            String venueName = doc.getString("venueName");  // Retrieve the venueName field

                            // Create a new Booking instance with a placeholder id (e.g., "")
                            Booking booking = new Booking("", venueName, date);

                            // Check if the booking is for today
                            if (booking.isToday()) {
                                todayList.add(booking);
                            } else {
                                pastList.add(booking);
                            }
                        }

                        // Notify the adapters about data changes
                        todayAdapter.notifyDataSetChanged();
                        pastAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle errors (e.g., network issues, Firestore exceptions)
                    e.printStackTrace();
                });
    }

}