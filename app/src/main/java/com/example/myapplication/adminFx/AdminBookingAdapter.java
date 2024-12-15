package com.example.myapplication.adminFx;

import android.app.AlertDialog;
import android.content.Context;
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

import java.util.List;

public class AdminBookingAdapter extends RecyclerView.Adapter<AdminBookingAdapter.ViewHolder> {

    private List<AdminBooking> bookingList; // List of bookings
    private Context context;
    private FirebaseFirestore db;

    public AdminBookingAdapter(List<AdminBooking> bookingList, Context context) {
        this.bookingList = bookingList;
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
        // Get the current booking item
        AdminBooking booking = bookingList.get(position);

        // Bind the booking details to the UI
        holder.textViewBookingId.setText("Booking ID: " + booking.getBookingId());
        holder.textViewBookingDetails.setText("Details: " + booking.getBookingDetails());

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
                        "Booking Date: " + booking.getBookingDate() + "\n" +
                        "Status: " + booking.getStatus())
                .setPositiveButton("OK", null)
                .show();
    }

    // Approve Booking by updating its status in Firestore
    private void approveBooking(String bookingId, int position) {
        db.collection("Bookings").document(bookingId)
                .update("status", "approved")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Booking approved successfully!", Toast.LENGTH_SHORT).show();
                    notifyItemChanged(position); // Refresh the item in the list
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error approving booking", Toast.LENGTH_SHORT).show();
                });
    }

    // Deny Booking by updating its status in Firestore
    private void denyBooking(String bookingId, int position) {
        db.collection("Bookings").document(bookingId)
                .update("status", "denied")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Booking denied successfully!", Toast.LENGTH_SHORT).show();
                    notifyItemChanged(position); // Refresh the item in the list
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error denying booking", Toast.LENGTH_SHORT).show();
                });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewBookingId, textViewBookingDetails;
        Button buttonViewDetails, buttonApprove, buttonDeny;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize UI components
            textViewBookingId = itemView.findViewById(R.id.textViewBookingId);
            textViewBookingDetails = itemView.findViewById(R.id.textViewBookingDetails);
            buttonViewDetails = itemView.findViewById(R.id.buttonViewDetails);
            buttonApprove = itemView.findViewById(R.id.buttonApprove);
            buttonDeny = itemView.findViewById(R.id.buttonDeny);
        }
    }
}
