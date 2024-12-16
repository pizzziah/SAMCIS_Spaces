package com.example.myapplication.adminFx;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminBookingAdapter extends RecyclerView.Adapter<AdminBookingAdapter.ViewHolder> {

    private List<AdminBooking> bookingList;
    private List<AdminBooking> approvedList; // List to store approved bookings
    private List<AdminBooking> deniedList; // List to store denied bookings
    private Context context;
    private FirebaseFirestore db;

    public AdminBookingAdapter(List<AdminBooking> bookingList, Context context) {
        this.bookingList = bookingList;
        this.approvedList = new ArrayList<>();
        this.deniedList = new ArrayList<>();
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout for individual booking items
        View view = LayoutInflater.from(context).inflate(R.layout.booking_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Determine which list the booking comes from (approved or denied)
        AdminBooking booking = bookingList.get(position);

        // Bind the booking details to the UI
        holder.textViewBookingId.setText("Booking ID: " + booking.getBookingId());
        holder.textViewBookingDetails.setText("Details: " + booking.getBookingDetails());
        holder.textViewBookingDate.setText("Booking Date: "  + booking.getDate());
        holder.textViewStatus.setText("Status: "  + booking.getStatus());

        // View Details Button
        holder.buttonViewDetails.setOnClickListener(v -> showBookingDetailsDialog(booking));

        // Approve Booking Button
        holder.buttonApprove.setOnClickListener(v -> approveBooking(booking.getBookingId(), position));

        // Deny Booking Button
        holder.buttonDeny.setOnClickListener(v -> denyBooking(booking.getBookingId(), position));
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    // Show detailed booking info in a dialog
    private void showBookingDetailsDialog(AdminBooking booking) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Booking Details")
                .setMessage("Booking ID: " + booking.getBookingId() + "\n" +
                        "Details: " + booking.getBookingDetails() + "\n" +
                        "Booking Date: " + booking.getDate() + "\n" +
                        "Status: " + booking.getStatus())
                .setPositiveButton("OK", null)
                .show();
    }

    // Approve Booking by updating its status in Firestore
    private void approveBooking(String name, int position) {
        db.collection("Users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot userDoc : task.getResult()) {
                            userDoc.getReference()
                                    .collection("bookings")
                                    .document(name)
                                    .update("status", "true")
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(context, "Booking approved successfully!", Toast.LENGTH_SHORT).show();
                                        Log.d("UpdateBooking", "Updated booking name: " + name + " for user: " + userDoc.getId());
                                        removeBooking(position); // Remove the booking after approval
                                    });
                        }
                    } else {
                        Log.e("FirestoreError", "Error fetching users", task.getException());
                        Toast.makeText(context, "Error fetching users", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    // Deny Booking by updating its status in Firestore
    private void denyBooking(String name, int position) {
        db.collection("Users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot userDoc : task.getResult()) {
                            userDoc.getReference()
                                    .collection("bookings")
                                    .document(name)
                                    .update("status", "false")
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(context, "Booking denied successfully!", Toast.LENGTH_SHORT).show();
                                        Log.d("DenyBooking", "Booking denied: " + name + " for user: " + userDoc.getId());
                                        removeBooking(position); // Remove the booking after denial
                                    });

                        }
                    } else {
                        Log.e("FirestoreError", "Error fetching users", task.getException());
                        Toast.makeText(context, "Error fetching users", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void removeBooking(int position) {
        bookingList.remove(position); // Remove the booking from the list
        notifyItemRemoved(position);  // Notify the adapter that the item was removed
        notifyItemRangeChanged(position, bookingList.size()); // Adjust positions of remaining items
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewBookingId, textViewBookingDetails, textViewBookingDate, textViewStatus, textViewName;
        Button buttonViewDetails, buttonApprove, buttonDeny;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize UI components
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewBookingId = itemView.findViewById(R.id.textViewBookingId);
            textViewBookingDetails = itemView.findViewById(R.id.textViewBookingDetails);
            textViewBookingDate = itemView.findViewById(R.id.textViewBookingDate);
            buttonViewDetails = itemView.findViewById(R.id.buttonViewDetails);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            buttonApprove = itemView.findViewById(R.id.buttonApprove);
            buttonDeny = itemView.findViewById(R.id.buttonDeny);
        }
    }
}