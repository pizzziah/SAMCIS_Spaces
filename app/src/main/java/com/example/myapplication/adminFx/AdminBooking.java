package com.example.myapplication.adminFx;

public class AdminBooking {

    private String bookingId;
    private String bookingDetails;
    private String name;
    private String user;
    private String date;
    private String status; // "approved", "denied", or "pending"
    private boolean isArchived; // Added this field

    // Default constructor required for Firebase Firestore
    public AdminBooking() {}

    public AdminBooking(String bookingId, String bookingDetails, String name, String user, String date, String status, boolean isArchived) {
        this.bookingId = bookingId;
        this.bookingDetails = bookingDetails;
        this.name = name;
        this.user = user;
        this.date = date;
        this.status = status;
        this.isArchived = isArchived;
    }

    // Getter and Setter methods
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getBookingDetails() { return bookingDetails; }
    public void setBookingDetails(String bookingDetails) { this.bookingDetails = bookingDetails; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isArchived() { return isArchived; }
    public void setArchived(boolean archived) { isArchived = archived; }

    @Override
    public String toString() {
        return "Booking ID: " + bookingId + ", Details: " + bookingDetails + ", Status: " + status + ", Date: " + date;
    }
}
