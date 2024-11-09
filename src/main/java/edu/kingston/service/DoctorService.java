package edu.kingston.service;

import edu.kingston.model.Appointment;
import edu.kingston.model.Doctor;
import edu.kingston.model.TimeSlots;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DoctorService {
    private final AppointmentService appointmentService;

    public DoctorService(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    private final List<Doctor> list = new ArrayList<>();

    //? Allocate time slots to doctors
    private final Set<TimeSlots> allocatedTimeSlots = new HashSet<>();

    //? Add default doctor
    public void addDefaultDoctors() {
        if (getDoctorList().isEmpty() || getDoctorById("D01") == null) {
            addDoctor(new Doctor("D01", "Dr. Heshan Perera", "0777251129", List.of(TimeSlots.MONDAY, TimeSlots.WEDNESDAY)));
            addDoctor(new Doctor("D02", "Dr. Sanduni Perera", "0777112233", List.of(TimeSlots.SATURDAY, TimeSlots.FRIDAY)));
        }
    }

    //? Add doctor
    public void addDoctor(Doctor doctor) {
        list.add(doctor);
    }

    //? Get doctor list
    public List<Doctor> getDoctorList() {
        return list;
    }

    public void deleteDoctor(String doctorId) {
        list.removeIf(doctor -> doctor.getDoctorId().equals(doctorId));
    }

    //? Get doctor by ID
    public Doctor getDoctorById(String doctorId) {

        List<Doctor> doctorList = getDoctorList();
        for (Doctor doctor : doctorList) {
            if (doctor.getDoctorId().equals(doctorId)) {
                return doctor;
            }
        }
        return null;
    }

    //? Method to generate 15-minute slots from TimeSlots
    public List<LocalTime> getTimeSlots(TimeSlots timeSlot) {
        List<LocalTime> timeSlots = new ArrayList<>();
        LocalTime startTime = timeSlot.getStartTime();

        while (startTime.isBefore(timeSlot.getEndTime())) {
            timeSlots.add(startTime);
            startTime = startTime.plusMinutes(15);
        }

        return timeSlots;
    }

    //? Get available time slots for a doctor on a specific date
    public List<LocalTime> getAvailableTimeSlots(Doctor doctor, LocalDate date) {
        List<LocalTime> availableSlots = new ArrayList<>();

        DayOfWeek day = date.getDayOfWeek();
        for (TimeSlots timeSlot : doctor.getAvailability()) {
            if (timeSlot.getDay().equalsIgnoreCase(day.toString())) {
                List<LocalTime> slotTimes = getTimeSlots(timeSlot);
                availableSlots.addAll(slotTimes);
            }
        }

        //? Check existing appointments
        List<Appointment> doctorAppointments = appointmentService.getAppointmentsForDoctor(doctor.getDoctorId(), date);

        for (Appointment appointment : doctorAppointments) {
            LocalTime bookedTime = appointment.getAppointmentTime();
            availableSlots.remove(bookedTime);
        }

        return availableSlots;
    }

    //? Get upcoming two available dates
    public List<LocalDate> getUpcomingTwoAvailableDates(Doctor doctor) {
        List<LocalDate> upcomingDates = new ArrayList<>();
        LocalDate today = LocalDate.now();
        int daysChecked = 0;

        while (upcomingDates.size() < 2) {
            LocalDate potentialDate = today.plusDays(daysChecked);
            DayOfWeek dayOfWeek = potentialDate.getDayOfWeek();

            for (TimeSlots slot : doctor.getAvailability()) {
                if (slot.getDay().equalsIgnoreCase(dayOfWeek.toString())) {
                    upcomingDates.add(potentialDate);
                    break;
                }
            }
            daysChecked++;
        }
        return upcomingDates;
    }

    //? Get available time range for a doctor
    public List<TimeSlots> getAvailableTimeRange(Doctor doctor) {
        List<TimeSlots> availableTimeRange = new ArrayList<>();
        for (TimeSlots timeSlot : doctor.getAvailability()) {
            availableTimeRange.add(timeSlot);
        }
        return availableTimeRange;
    }

    //? Print available time range
    public String printAvailableTimeRange(Doctor doctor) {
        String availableTimeRange = "";
        for (TimeSlots timeSlot : doctor.getAvailability()) {
            availableTimeRange += timeSlot.getDay() + ": " + timeSlot.getTimeRange();
            if (timeSlot != doctor.getAvailability().get(doctor.getAvailability().size() - 1)) {
                availableTimeRange += ", ";
            }
        }
        return availableTimeRange;
    }

    // ? Print doctor availability
    public String printAvailability(Doctor doctor) {
        String availability = "";
        for (TimeSlots timeSlot : doctor.getAvailability()) {
            availability += timeSlot.getDay() + ": " + timeSlot.getTimeRange();
            if (timeSlot != doctor.getAvailability().get(doctor.getAvailability().size() - 1)) {
                availability += ", ";
            }
        }
        return availability;
    }

    //? Add allocated time slots
    public void addAllocatedTimeSlots(TimeSlots timeSlot) {
        allocatedTimeSlots.add(timeSlot);
    }

    //? Check if time slot is allocated
    public boolean isTimeSlotAllocated(TimeSlots timeSlot) {
        return allocatedTimeSlots.contains(timeSlot);
    }
}
