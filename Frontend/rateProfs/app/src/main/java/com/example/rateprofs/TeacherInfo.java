package com.example.rateprofs;



import java.util.List;


public class TeacherInfo {

    private String id;
    private String primaryInstructor;


    private List<String> reviewIds; // List of review IDs
    private double averageRating; // Updated when new reviews are added
    private int numOfRatings; // Updated when new reviews are added

    private List<String> classCodes;

    public TeacherInfo(String primaryInstructor, double averageRating) {
        this.primaryInstructor = primaryInstructor;
        this.averageRating = averageRating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrimaryInstructor() {
        return primaryInstructor;
    }

    public void setPrimaryInstructor(String primaryInstructor) {
        this.primaryInstructor = primaryInstructor;
    }

    public List<String> getReviewIds() {
        return reviewIds;
    }

    public void setReviewIds(List<String> reviewIds) {
        this.reviewIds = reviewIds;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getNumOfRatings() {
        return numOfRatings;
    }

    public void setNumOfRatings(int numOfRatings) {
        this.numOfRatings = numOfRatings;
    }

    public List<String> getClassCodes() {
        return classCodes;
    }

    public void setClassCodes(List<String> classCodes) {
        this.classCodes = classCodes;
    }
}

