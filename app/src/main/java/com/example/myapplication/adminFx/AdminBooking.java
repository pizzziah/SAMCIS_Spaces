package com.example.myapplication.adminFx;

public class AdminBooking {

    private String bookingId;
    private String bookingDetails;
    private String User;
    private String bookingDate;
    private boolean
            bookingStatus; // E.g., "Pending", "Approved", "Denied"

    // Default constructor required for Firebase Firestore
    public AdminBooking() {
    }

    // Constructor to initialize booking details
    public AdminBooking(String bookingId, String bookingDetails, String User, String bookingDate, boolean bookingStatus) {
        this.bookingId = bookingId;
        this.bookingDetails = bookingDetails;
        this.User = User;
        this.bookingDate = bookingDate;
        this.bookingStatus = bookingStatus;
    }

    // Getter and setter methods for each field
    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingDetails() {
        return bookingDetails;
    }

    public void setBookingDetails(String bookingDetails) {
        this.bookingDetails = bookingDetails;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String User) {
        this.User = User;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public boolean getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(boolean bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    // Optional: Override toString() to easily view the object data
    @Override
    public String toString() {
        return "Booking ID: " + bookingId + ", Details: " + bookingDetails + ", User: " + User + ", Date: " + bookingDate + ", Status: " + bookingStatus;
    }
}
