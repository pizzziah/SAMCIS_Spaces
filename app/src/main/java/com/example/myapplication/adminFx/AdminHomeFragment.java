package com.example.myapplication.adminFx;

import android.os.Bundle;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;

public class AdminHomeFragment extends Fragment {

    private RecyclerView recyclerViewBookings;
    private RecyclerView recyclerViewApproved;
    private RecyclerView recyclerViewArchived;
    private AdminBookingAdapter adminBookingAdapter;

    private List<AdminBooking> pendingList = new ArrayList<>();
    private List<AdminBooking> approvedList = new ArrayList<>();
    private List<AdminBooking> archivedList = new ArrayList<>();

    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.a_fragment_home, container, false);

        recyclerViewBookings = view.findViewById(R.id.recyclerViewBookings);
        recyclerViewApproved = view.findViewById(R.id.recyclerViewApproved);
        recyclerViewArchived = view.findViewById(R.id.recyclerViewArchived);

        db = FirebaseFirestore.getInstance();

        adminBookingAdapter = new AdminBookingAdapter(pendingList, approvedList, archivedList, getContext());

        recyclerViewBookings.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewApproved.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewArchived.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerViewBookings.setAdapter(adminBookingAdapter);
        recyclerViewApproved.setAdapter(adminBookingAdapter);
        recyclerViewArchived.setAdapter(adminBookingAdapter);

        getBookings();

        return view;
    }

    private void getBookings() {
        db.collection("Users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null) {
                        Log.d("Firestore", "Retrieved Users successfully");

                        for (var document : queryDocumentSnapshots) {
                            Log.d("Firestore", "User document ID: " + document.getId());

                            // Retrieve bookings subcollection for each user
                            db.collection("Users")
                                    .document(document.getId())
                                    .collection("bookings")
                                    .get()
                                    .addOnSuccessListener(subQueryDocumentSnapshots -> {
                                        if (subQueryDocumentSnapshots != null) {
                                            for (var bookingDoc : subQueryDocumentSnapshots) {
                                                AdminBooking booking = bookingDoc.toObject(AdminBooking.class);

                                                if (booking == null) {
                                                    Log.e("Firestore", "Booking document conversion failed.");
                                                    continue;
                                                }

                                                Log.d("Booking Status", "Status: " + booking.getStatus() + ", Archived: " + booking.isArchived());

                                                if (booking.getStatus() != null && booking.getStatus().equals("approved")) {
                                                    approvedList.add(booking);
                                                } else if (booking.isArchived()) {
                                                    archivedList.add(booking);
                                                } else {
                                                    pendingList.add(booking);
                                                }
                                            }
                                            adminBookingAdapter.notifyDataSetChanged();
                                        }
                                    })
                                    .addOnFailureListener(subError -> Log.e("Booking Retrieval Error", subError.getMessage()));
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("Admin Booking Fragment", "User retrieval error: " + e.getMessage()));
    }

    private void approveBooking(AdminBooking booking) {
        booking.setStatus("approved");
        booking.setArchived(true);

        db.collection("Users").document(booking.getUser()).collection("bookings")
                .document(booking.getBookingId())
                .update("status", "approved", "isArchived", true)
                .addOnSuccessListener(aVoid -> {
                    approvedList.add(booking);
                    pendingList.remove(booking);
                    archivedList.add(booking);
                    adminBookingAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("AdminHomeFragment", "Failed to approve booking", e);
                });
    }
}
