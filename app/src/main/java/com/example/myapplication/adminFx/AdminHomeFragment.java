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

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.a_fragment_home, container, false);

        // Initialize Firestore and RecyclerView
        db = FirebaseFirestore.getInstance();
        recyclerView = rootView.findViewById(R.id.recyclerViewBookings); // Updated ID
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
        // Reference to the parent document (e.g., User document)
        DocumentReference userDocument = db.collection("Users").document("UserID");  // Change "UserID" to the actual user ID you want

        // Reference to the "bookings" subcollection
        CollectionReference bookingsSubcollection = userDocument.collection("bookings");

        // Fetch bookings from the subcollection
        bookingsSubcollection
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        bookingList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            AdminBooking booking = document.toObject(AdminBooking.class);
                            booking.setBookingId(document.getId()); // Store document ID for operations
                            bookingList.add(booking);

                            // Debugging - Log the fetched data
                            Log.d("BookingData", "Fetched booking: " + booking.getBookingId());
                        }

                        // Check if the list is populated
                        Log.d("BookingList", "Booking list size: " + bookingList.size());

                        // Notify adapter to update the view
                        adminBookingAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Failed to fetch bookings", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
