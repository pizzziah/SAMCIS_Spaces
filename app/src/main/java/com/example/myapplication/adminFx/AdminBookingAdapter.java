package com.example.myapplication.adminFx;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class AdminBookingAdapter extends RecyclerView.Adapter<AdminBookingAdapter.ViewHolder> {

    private List<AdminBooking> pendingList;
    private List<AdminBooking> approvedList;
    private List<AdminBooking> archivedList;
    private Context context;

    public AdminBookingAdapter(List<AdminBooking> pendingList, List<AdminBooking> approvedList, List<AdminBooking> archivedList, Context context) {
        this.pendingList = pendingList;
        this.approvedList = approvedList;
        this.archivedList = archivedList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.booking_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position < pendingList.size()) {
            AdminBooking booking = pendingList.get(position);
            holder.textViewBookingDetails.setText("Pending Booking: " + booking.getBookingDetails());
            holder.textViewDate.setText("Date: " + booking.getDate());
        } else if (position < pendingList.size() + approvedList.size()) {
            AdminBooking booking = approvedList.get(position - pendingList.size());
            holder.textViewBookingDetails.setText("Approved Booking: " + booking.getBookingDetails());
            holder.textViewDate.setText("Date: " + booking.getDate());
        } else {
            AdminBooking booking = archivedList.get(position - pendingList.size() - approvedList.size());
            holder.textViewBookingDetails.setText("Archived Booking: " + booking.getBookingDetails());
            holder.textViewDate.setText("Date: " + booking.getDate());
        }
    }

    @Override
    public int getItemCount() {
        return pendingList.size() + approvedList.size() + archivedList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewBookingDetails, textViewDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewBookingDetails = itemView.findViewById(R.id.textViewBookingDetails);
            textViewDate = itemView.findViewById(R.id.textViewBookingDate);
        }
    }
}
