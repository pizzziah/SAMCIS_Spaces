package com.example.myapplication.adminFx;

import android.annotation.SuppressLint;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminHomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdminBookingAdapter adminBookingAdapter;
    private List<AdminBooking> bookingList;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.a_fragment_home, container, false);

        // Initialize Firestore and RecyclerView
        db = FirebaseFirestore.getInstance();
        recyclerView = rootView.findViewById(R.id.recyclerViewBookings); // Updated ID
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setNestedScrollingEnabled(false);

        bookingList = new ArrayList<>();
        adminBookingAdapter = new AdminBookingAdapter(bookingList, getContext());
        recyclerView.setAdapter(adminBookingAdapter);

        // Fetch bookings from Firestore
        fetchBookings();

        return rootView;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchBookings() {
        // Reference to the Firestore collection for all users
        CollectionReference usersCollection = db.collection("Users");

        // Log the Firestore path for debugging purposes
        Log.d("FirestorePath", "Fetching all bookings from all users");

        // Fetch all users' bookings from Firestore
        usersCollection.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        bookingList.clear(); // Clear the existing list to prevent duplicates

                        // Iterate over each user document
                        for (QueryDocumentSnapshot userDoc : task.getResult()) {
                            // Reference to the bookings subcollection of the current user
                            CollectionReference bookingsSubcollection = userDoc
                                    .getReference()
                                    .collection("bookings");

                            // Fetch the bookings for this user
                            bookingsSubcollection.get()
                                    .addOnCompleteListener(subTask -> {
                                        if (subTask.isSuccessful()) {
                                            // Iterate through the bookings for this user
                                            for (QueryDocumentSnapshot bookingDoc : subTask.getResult()) {
                                                // Convert Firestore document to AdminBooking object
                                                AdminBooking booking = bookingDoc.toObject(AdminBooking.class);

                                                // Extract additional fields if needed
                                                String bookingId = bookingDoc.getId();
                                                String details = bookingDoc.getString("details");
                                                String status = bookingDoc.getString("status");
                                                String date = bookingDoc.getString("date");

                                                // Set properties into the AdminBooking object
                                                booking.setBookingId(bookingId);
                                                booking.setBookingDetails(details);
                                                booking.setBookingStatus(Boolean.parseBoolean(status));
                                                booking.setDate(date);

                                                // Log the fetched booking details
                                                Log.d("BookingDetails", "Fetched booking ID: " + bookingId +
                                                        ", Details: " + details +
                                                        ", Status: " + status +
                                                        ", Date: " + date);

                                                // Add the booking to the list
                                                bookingList.add(booking);
                                            }

                                            // Log the size of the list after adding the bookings
                                            Log.d("BookingList", "Booking list size: " + bookingList.size());

                                            // Notify the adapter to refresh the RecyclerView
                                            adminBookingAdapter.notifyDataSetChanged();
                                        } else {
                                            // Handle error fetching bookings for this user
                                            Log.e("FirestoreError", "Error fetching bookings for user: " + userDoc.getId(),
                                                    subTask.getException());
                                        }
                                    });
                        }
                    } else {
                        // Handle error fetching users
                        Log.e("FirestoreError", "Error fetching users: ", task.getException());
                        Toast.makeText(getContext(), "Failed to fetch users", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

