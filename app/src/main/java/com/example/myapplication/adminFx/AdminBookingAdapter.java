package com.example.myapplication.adminFx;

import static com.example.myapplication.adminFx.AdminBookingAdapter.*;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adminFx.AdminBooking;

import java.util.List;

public class AdminBookingAdapter extends RecyclerView.Adapter<BookingViewHolder> {

    private Context context;
    private List<AdminBooking> bookingList;
    private OnBookingActionListener listener;

    public interface OnBookingActionListener {
        void onApproveClick(AdminBooking booking);

        void onDenyClick(AdminBooking booking);
    }

    public AdminBookingAdapter(Context context, List<AdminBooking> bookingList, OnBookingActionListener listener) {
        this.context = context;
        this.bookingList = bookingList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        AdminBooking booking = bookingList.get(position);

        // Set booking data
        holder.bookingDate.setText(booking.getBookingDate());
        holder.bookingTime.setText(booking.getBookingTime());
        holder.receivedDate.setText("Received: " + booking.getReceivedDate());
        holder.bookingImage.setImageResource(booking.getImageResource());

        // Handle button clicks
        holder.approveButton.setOnClickListener(v -> listener.onApproveClick(booking));
        holder.denyButton.setOnClickListener(v -> listener.onDenyClick(booking));
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {

        ImageView bookingImage;
        TextView bookingDate, bookingTime, receivedDate, viewDetails;
        Button approveButton, denyButton;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);

            bookingImage = itemView.findViewById(R.id.bookingImage);
            bookingDate = itemView.findViewById(R.id.bookingDate);
            bookingTime = itemView.findViewById(R.id.bookingTime);
            receivedDate = itemView.findViewById(R.id.receivedDate);
            viewDetails = itemView.findViewById(R.id.viewDetails);
            approveButton = itemView.findViewById(R.id.buttonApprove);
            denyButton = itemView.findViewById(R.id.buttonDeny);
        }
    }
}


