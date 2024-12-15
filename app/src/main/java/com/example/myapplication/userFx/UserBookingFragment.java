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
        db.collection("Users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    todayList.clear();
                    pastList.clear();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        // Map Firestore document to Booking object
                        Booking booking = doc.toObject(Booking.class);
                        //booking.setId(doc.getId()); // Set the document ID manually

                        // Check if the booking is for today
                        if (booking.isToday()) {
                            todayList.add(booking);
                        } else {
                            pastList.add(booking);
                        }
                    }
                    // Notify adapters about data changes
                    todayAdapter.notifyDataSetChanged();
                    pastAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Handle errors (e.g., network issues, Firestore exceptions)
                    e.printStackTrace();
                });
    }
}