package pi_project.Farouk.Models;

import java.time.LocalDateTime;
import java.time.LocalDate;

public class Exam {
    private int id;
    private int classeId;  // Foreign key to the class
    private String subject;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;


    public Exam(int id, int classeId, String subject, LocalDateTime startTime, LocalDateTime endTime, String location) {
        this.id = id;
        this.classeId = classeId;
        this.subject = subject;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;

    }
    public Exam(){

    }
    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClasseId() {
        return classeId;
    }

    public void setClasseId(int classeId) {
        this.classeId = classeId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean occursOn(LocalDate date) {
        // Check if the startTime's date is the same as the provided date
        return !startTime.toLocalDate().isBefore(date) && !endTime.toLocalDate().isAfter(date);
    }

}
