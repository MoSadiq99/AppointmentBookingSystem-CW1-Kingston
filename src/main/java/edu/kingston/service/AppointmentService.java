package edu.kingston.service;

import edu.kingston.model.Appointment;
import edu.kingston.model.Treatment;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AppointmentService {

    //* Appointment List
    List<Appointment> appointmentsList = new ArrayList<>();

    //? Add appointment
    public void addAppointment(Appointment appointment) {
        appointmentsList.add(appointment);
    }

    //? Get appointment by ID
    public Appointment getAppointmentById(String appointmentId) {

        for (Appointment appointment : appointmentsList) {
            if (appointment.getAppointmentId().equals(appointmentId)) {
                return appointment;
            }
        }
        return null;
    }

    //? Delete appointment
    public void deleteAppointment(Appointment appointment) {
        appointmentsList.remove(appointment);
    }

    //? Get all appointments
    public List<Appointment> getAppointmentsList() {
        return appointmentsList;
    }

    //? Get appointments by date
    public List<Appointment> getAppointmentsByDate(LocalDate date) {

        List<Appointment> appointmentsByDate = new ArrayList<>();
        for (Appointment appointment : appointmentsList) {
            if (appointment.getAppointmentDate().equals(date)) {
                appointmentsByDate.add(appointment);
            }
        }
        return appointmentsByDate;
    }

    //? Get appointments by patient name
    public List<Appointment> getAppointmentsByPatient(String patientName) {

        List<Appointment> appointmentsByPatient = new ArrayList<>();
        for(Appointment appointment : appointmentsList) {
            if (appointment.getPatient().getName().equals(patientName)) {
                appointmentsByPatient.add(appointment);
            }
        }
        return appointmentsByPatient;
    }

    //? Get appointments by doctor
    public List<Appointment> getAppointmentsByDoctor(String doctorName) {

        List<Appointment> appointmentsByDoctor = new ArrayList<>();
        for(Appointment appointment : appointmentsList) {
            if (appointment.getDoctor().getName().equals(doctorName)) {
                appointmentsByDoctor.add(appointment);
            }
        }
        return appointmentsByDoctor;
    }

    //? Get appointments by doctor ID
    public List<Appointment> getAppointmentsByDoctorID(String doctorId) {

        List<Appointment> appointmentsByDoctorID = new ArrayList<>();
        for(Appointment appointment : appointmentsList) {
            if (appointment.getDoctor().getDoctorId().equals(doctorId)) {
                appointmentsByDoctorID.add(appointment);
            }
        }
        return appointmentsByDoctorID;
    }

    //? Get appointments for a doctor on a specific date
    public List<Appointment> getAppointmentsForDoctor(String doctorId, LocalDate date) {
        List<Appointment> appointmentsForDoctor = new ArrayList<>();
        for (Appointment appointment : getAppointmentsByDoctorID(doctorId)) {
            if (appointment.getAppointmentDate().equals(date)) {
                appointmentsForDoctor.add(appointment);
            }
        }
        return appointmentsForDoctor;
    }

    //? Get All appointment Details Printed
    public void getAllAppointments() {
        for (Appointment appointment : appointmentsList) {
            appointmentsList.forEach(this::printAppointmentDetails);
        }
    }

    //? Print Appointment Details
    private void printAppointmentDetails(Appointment appointment) {
        System.out.printf("\n%-40s%n", "\t\t\tAppointment Details");
        System.out.println("+-------------------------------------------+");
        System.out.printf("%-25s : %-20s %n", "Appointment ID", appointment.getAppointmentId());
        System.out.printf("%-25s : %-20s %n", "Appointment Date", appointment.getAppointmentDate());
        System.out.printf("%-25s : %-20s %n", "Appointment Time", appointment.getAppointmentTime());
        System.out.println("---------------------------------------------");
        System.out.printf("%-25s : %-20s %n", "Doctor ID", appointment.getDoctor().getDoctorId());
        System.out.printf("%-25s : %-20s %n", "Doctor Name", appointment.getDoctor().getName());
        System.out.println("---------------------------------------------");
        System.out.printf("%-25s : %-20s %n", "Patient ID", appointment.getPatient().getPatientId());
        System.out.printf("%-25s : %-20s %n", "Patient Name", appointment.getPatient().getName());
        System.out.println("---------------------------------------------");
        System.out.printf("%-40s%n", "\t\t\tSelected Treatments");
        System.out.println("+-------------------------------------------+");
        System.out.printf("%-25s : %-25s %n", "Treatment", "Price");
        System.out.println("---------------------------------------------");
        for (Treatment treatment : appointment.getTreatments()) {
            System.out.printf("%-25s : %-20s %n", treatment.getName(), treatment.getPrice());
        }
        System.out.println("---------------------------------------------\n");
    }
}
