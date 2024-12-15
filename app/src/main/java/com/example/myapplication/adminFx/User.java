package com.example.myapplication.adminFx;

public class User {
    private String userId;
    private String Category;
    private String FullName;
    private String IDNumber;
    private boolean ProfileComplete;
    private String Program;
    private String UserEmail;
    private String UserRole;
    private String YearLevel;

    public User() {
        // Default constructor required for Firestore
    }

    // Getters and setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCategory() { return Category; }
    public String getFullName() { return FullName; }
    public String getIdNumber() { return IDNumber; }
    public boolean isProfileComplete() { return ProfileComplete; }
    public String getProgram() { return Program; }
    public String getUserEmail() { return UserEmail; }
    public String getUserRole() { return UserRole; }
    public String getYearLevel() { return YearLevel; }
}
