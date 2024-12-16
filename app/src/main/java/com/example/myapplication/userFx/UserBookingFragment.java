package com.example.myapplication.userFx;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.u_fragment_booking, container, false);

        // Ensure recyclerPast is correctly initialized
        recyclerPast = view.findViewById(R.id.recyclerPast);

        if (recyclerPast == null) {
            Log.e("UserBookingFragment", "RecyclerView not found!");
            return view;
        }

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
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("Users")
                .document(userId)
                .collection("bookings")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null) {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            String date = doc.getString("date");
                            String venueName = doc.getString("venueName");
                            String id = doc.getId();

                            // Ensure no null values in critical fields
                            if (date != null && venueName != null) {
                                Booking booking = new Booking(id, venueName, date); // Include status
                                pastList.add(booking);
                            }
                        }
                        pastAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle error
                    Toast.makeText(getContext(), "Failed to fetch bookings", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }
}