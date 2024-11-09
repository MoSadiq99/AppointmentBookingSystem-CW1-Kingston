package edu.kingston.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.StringJoiner;

public class Appointment {

    private String appointmentId;
    private Patient patient;
    private Doctor doctor;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private Double registrationFee;
    private List<Treatment> treatments;

    public Appointment(String appointmentId, Patient patient, Doctor doctor, LocalDate appointmentDate, LocalTime appointmentTime, Double registrationFee, List<Treatment> treatments) {
        this.appointmentId = appointmentId;
        this.patient = patient;
        this.doctor = doctor;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.registrationFee = registrationFee;
        this.treatments = treatments;
    }

    @Override
    public String toString() {

        StringJoiner treatmentsJoiner = new StringJoiner(", ");
        for (Treatment treatment : treatments) {
            treatmentsJoiner.add(treatment.getName());
        }
        String result = treatments.toString();

        return"--------------------------Appointment Details-------------------------\n" +
                "Appointment Id: " + appointmentId + "\n" +
                "Patient Name: " + patient.getName() + "\n" +
                "Doctor Name: " + doctor.getName() + "\n" +
                "Appointment Date: " + appointmentDate + "\n" +
                "Appointment Time: " + appointmentTime + "\n" +
                "Registration Fee: " + registrationFee + "\n" +
                "Treatment: " + result + "\n" +
                "---------------------------------------------------------------------";
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public Double getRegistrationFee() {
        return registrationFee;
    }

    public void setRegistrationFee(Double registrationFee) {
        this.registrationFee = registrationFee;
    }

    public List<Treatment> getTreatments() {
        return treatments;
    }

    public void setTreatments(List<Treatment> treatment) {
        this.treatments = treatment;
    }
}
