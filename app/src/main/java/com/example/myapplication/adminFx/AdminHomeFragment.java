package com.example.myapplication.adminFx;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminHomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdminBookingAdapter adminBookingAdapter;
    private List<AdminBooking> bookingList;
    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.a_fragment_home, container, false);

        // Initialize Firestore and RecyclerView
        db = FirebaseFirestore.getInstance();
        recyclerView = rootView.findViewById(R.id.recyclerViewAdminBookings); // Updated ID
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        bookingList = new ArrayList<>();
        adminBookingAdapter = new AdminBookingAdapter(bookingList, getContext());
        recyclerView.setAdapter(adminBookingAdapter);

        // Fetch bookings from Firestore
        fetchBookings();

        return rootView;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchBookings() {
        // Reference to the Users collection
        CollectionReference usersCollection = db.collection("Users");

// Iterate through all users dynamically
        usersCollection.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        bookingList.clear(); // Clear the list to avoid duplicates

                        for (QueryDocumentSnapshot userDoc : task.getResult()) {
                            // Dynamically reference each user's bookings subcollection
                            CollectionReference bookingsSubcollection = userDoc.getReference().collection("bookings");

                            // Fetch the bookings for this user
                            bookingsSubcollection.get()
                                    .addOnCompleteListener(subTask -> {
                                        if (subTask.isSuccessful()) {
                                            for (QueryDocumentSnapshot bookingDoc : subTask.getResult()) {
                                                AdminBooking booking = bookingDoc.toObject(AdminBooking.class);
                                                booking.setBookingId(bookingDoc.getId()); // Store document ID

                                                bookingList.add(booking); // Add to the list
                                                Log.d("BookingData", "Fetched booking from user: " + userDoc.getId());
                                            }

                                            // Update the RecyclerView
                                            adminBookingAdapter.notifyDataSetChanged();
                                        } else {
                                            Log.e("FirestoreError", "Error fetching bookings for user: " + userDoc.getId(), subTask.getException());
                                        }
                                    });
                        }
                    } else {
                        Log.e("FirestoreError", "Error fetching users", task.getException());
                    }
                });


    }
}