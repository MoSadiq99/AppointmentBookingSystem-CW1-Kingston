package edu.kingston.model;

import java.util.List;

public class Doctor implements Person {

    private String doctorId;
    private String name;
    private String phoneNo;
    private List<TimeSlots> availability;

    public Doctor(String doctorId, String name, String phoneNo, List<TimeSlots> availability) {
        this.doctorId = doctorId;
        this.name = name;
        this.phoneNo = phoneNo;
        this.availability = availability;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getPhoneNo() {
        return phoneNo;
    }

    @Override
    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public List<TimeSlots> getAvailability() {
        return availability;
    }

    public void setAvailability(List<TimeSlots> availability) {
        this.availability = availability;
    }
}
