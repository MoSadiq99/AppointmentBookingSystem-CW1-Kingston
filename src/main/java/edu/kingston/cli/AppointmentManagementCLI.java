package edu.kingston.cli;

import edu.kingston.model.*;
import edu.kingston.service.AppointmentService;
import edu.kingston.service.DoctorService;
import edu.kingston.service.PatientService;
import edu.kingston.service.TreatmentService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Consumer;

public class AppointmentManagementCLI {
    private static int counter = 1;
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final AppointmentService appointmentService;
    private final TreatmentService treatmentService;
    private final HomeCLI homeCLI;

    public AppointmentManagementCLI(HomeCLI homeCLI, DoctorService doctorService, PatientService patientService, AppointmentService appointmentService, TreatmentService treatmentService) {
        this.homeCLI = homeCLI;
        this.doctorService = doctorService;
        this.patientService = patientService;
        this.appointmentService = appointmentService;
        this.treatmentService = treatmentService;
    }

    public void appointmentManagement(Scanner scanner) {
        System.out.println("\n+------------------------------------------------------------------------+");
        System.out.println("|                        Appointment Management                          |");
        System.out.println("+------------------------------------------------------------------------+");
        System.out.println("\n Please select an option:\n");

        System.out.println("1. Add appointment     [#1]");
        System.out.println("2. Update appointment  [#2]");
        System.out.println("3. Delete appointment  [#3]");
        System.out.println("4. View appointments   [#4]");
        System.out.println("5. Back to main menu   [#5]");
        System.out.println("6. Exit                [#6]");
        System.out.print("\nEnter your choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                addAppointment(scanner);
                break;
            case 2:
                updateAppointment(scanner);
                break;
            case 3:
                deleteAppointment(scanner);
                break;
            case 4:
                viewAppointment(scanner);
                break;
            case 5:
                homeCLI.start();
                break;
            case 6:
                System.out.println("\nThank you for using Aura Skin Care Clinic. Goodbye!\n");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                appointmentManagement(scanner);
                break;
        }
    }

    //? Generate unique appointment id
    public String generateIdForAppointment() {
        String base = "A";
        String formattedCounter = String.format("%02d", counter);
        counter++;
        String id = base + formattedCounter;
        return id;
    }

    //! Add Appointment
    public void addAppointment(Scanner scanner) {
        System.out.println("\n+--------------------------------------------------------------------+");
        System.out.println("|                            Add Appointment                         |");
        System.out.println("+--------------------------------------------------------------------+");
        System.out.println("\n");

        //? Generate appointment id
        String apptId = generateIdForAppointment();
        System.out.println("\tAppointment id: " + apptId);
        System.out.print("+--------------------------+");

        //? List doctors
        System.out.println("\n\n--------------------------Doctors List-------------------------------");
        List<Doctor> doctorList = doctorService.getDoctorList();

        int doctorNo = 0;
        if (doctorList.isEmpty()) {
            System.out.println("\nNo doctor found.");
        } else {
            for (Doctor doctor : doctorList) {
                System.out.printf("\n%-20s : %d%n", "Doctor No", ++doctorNo);
                System.out.printf("%-20s : %s%n", "Doctor ID", doctor.getDoctorId());
                System.out.printf("%-20s : %s%n", "Name", doctor.getName());
                System.out.printf("%-20s : %s%n", "Phone", doctor.getPhoneNo());
                System.out.printf("%-20s : %s%n", "Assigned Time Slots", doctorService.printAvailableTimeRange(doctor));
                System.out.println();
            }
        }
        System.out.println("---------------------------------------------------------------------");

        //? Select doctor
        Doctor doctor = null;
        while (doctor == null) {
            System.out.print("Select a Doctor by entering the Number (or 'q' to Quit): ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("q")) {
                appointmentManagement(scanner);
            }
            int index = Integer.parseInt(input) - 1;

            try {
                doctor = doctorList.get(index);
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Doctor not found. Please try again.");
            }
        }

        //? Suggest upcoming two available dates based on doctor's availability
        List<LocalDate> suggestedDates = doctorService.getUpcomingTwoAvailableDates(doctor);

        //? Display suggested dates
        System.out.println("\nUpcoming available dates for Doctor " + doctor.getName() + ":");
        for (int i = 0; i < suggestedDates.size(); i++) {
            System.out.println((i + 1) + ". " + suggestedDates.get(i));
        }

        //? Get the date for the appointment
        LocalDate appointmentDate = null;
        TimeSlots availableTimeForDay = null;

        while (availableTimeForDay == null) {
            while (appointmentDate == null) {
                System.out.print("\nEnter appointment date (YYYY-MM-DD) or choose a suggested date (1 or 2): ");
                String dateInput = scanner.nextLine();

                try {
                    if (dateInput.equals("1") || dateInput.equals("2")) {
                        appointmentDate = suggestedDates.get(Integer.parseInt(dateInput) - 1);

                    } else {
                        try {
                            appointmentDate = LocalDate.parse(dateInput);
                            LocalDate today = LocalDate.now();

                            if (appointmentDate.isBefore(today)) {
                                System.out.println("The date cannot be in the past. Please enter a valid future date.");
                                appointmentDate = null;
                            }

                        } catch (DateTimeParseException e) {
                            System.out.println("Invalid date format. Please try again.");
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please try again.");
                }
            }

            // Check the doctor's availability for the entered day
            DayOfWeek dayOfWeek = appointmentDate.getDayOfWeek();
            for (TimeSlots slot : doctor.getAvailability()) {
                if (slot.getDay().equalsIgnoreCase(dayOfWeek.toString())) {
                    availableTimeForDay = slot;
                    break;
                }
            }

            if (availableTimeForDay == null) {
                System.out.println("The doctor is not available on " + dayOfWeek + ". Please choose another date.");
                appointmentDate = null;
            }
        }

        //? Get the time for the appointment
        List<LocalTime> availableSlots = doctorService.getAvailableTimeSlots(doctor, appointmentDate);

        //? Display available time slots
        if (availableSlots.isEmpty()) {
            System.out.println("No available time slots for this date.");
            return;
        }

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

        System.out.println("\nAvailable Time Slots for " + appointmentDate + ":");

        int slotNumber = 1;
        for (LocalTime slot : availableSlots) {
            String formattedTime = slot.format(timeFormatter);
            System.out.printf("%d. %s%n", slotNumber++, formattedTime);
        }

        LocalTime appointmentTime = null;
        while (appointmentTime == null) {
            System.out.print("\nEnter the desired time slot Number: ");
            int input = scanner.nextInt();
            scanner.nextLine();

            try {
                appointmentTime = availableSlots.get(input - 1);
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Input the number for the selected Slot.");
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please try again.");
            }
        }

        //? Get registration fee
        double registrationFee = 500.0;
        while (true) {
            System.out.print("\nRegistration fee LKR 500.00 (Press 'Enter' if payment is done or Enter 'q' to Quit): ");
            String fee = scanner.nextLine();
            if (fee.isEmpty()) {
                System.out.println("\n*-------Payment confirmed-------*\n");
                break;
            } else if (fee.equalsIgnoreCase("q")) {
                System.out.println("\n*-------Appointment cancelled-------*\n");
                appointmentManagement(scanner);
            }
            else {
                System.out.println("\nInvalid input. Confirm the payment by pressing Enter.");
            }
        }

        //? Get patient details
        System.out.println("\n---------------------------Patient Details-------------------------------\n");

        String patientId = patientService.generateIdForPatient();
        System.out.printf("\tPatient id: " + patientId);
        System.out.print("\n+---------------------+");

        System.out.printf("\n\n%-28s: ", "Enter patient Name");
        String name = scanner.nextLine();

        System.out.printf("%-28s: ", "Enter patient Phone Number");
        String phone = scanner.nextLine();

        System.out.printf("%-28s: ", "Enter patient Email");
        String email = scanner.nextLine();

        System.out.printf("%-28s: ", "Enter patient NIC");
        String nic = scanner.nextLine();

        Patient patient = new Patient(patientId, name, phone, email, nic);
        patientService.addPatient(patient);

        //? List treatments
        System.out.println("\n-------------------------- Treatments List -------------------------------");
        System.out.printf("%-15s %-15s %-30s %-20s%n", "Number", "ID", "Name", "Price (LKR)");
        System.out.println("--------------------------------------------------------------------------");

        List<Treatment> treatments = treatmentService.getTreatmentsList();
        int treatmentNo = 1;

        for (Treatment treatment : treatments) {
            System.out.printf("%-15d %-15s %-30s %-20.2f%n", treatmentNo++, treatment.getTreatmentId(), treatment.getName(), treatment.getPrice());
        }
        System.out.println("--------------------------------------------------------------------------");

        //? Add treatments
        List<Treatment> treatmentsList = new ArrayList<>();
        do {
            System.out.print("\nSelect a treatment by entering the Number (or Enter to skip): ");
            String input = scanner.nextLine();
            if (input == null || input.isEmpty()) {
                break;
            }
            Treatment treatment;
            try {
                int index = Integer.parseInt(input) - 1;
                treatment = treatments.get(index);
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Invalid input. Please try again.");
                continue;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please try again.");
                continue;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please try again.");
                continue;
            }
            treatmentsList.add(treatment);
        } while (true);

        //? Add appointment
        Appointment appointment = new Appointment(apptId, patient, doctor, appointmentDate, appointmentTime, registrationFee, treatmentsList);
        appointmentService.addAppointment(appointment);

        System.out.printf("\n%-40s%n", "*---------The new Appointment has been added successfully---------*");
        printAppointmentDetails(appointment);
        printBillingDetails(appointment);

        //? Add another appointment or go back
        while (true) {
            System.out.print("\nDo you want to add another appointment? (y/n): ");
            String choice = scanner.nextLine();
            if (choice.equalsIgnoreCase("n")) {
                appointmentManagement(scanner);
                break;
            } else if (choice.equalsIgnoreCase("y")) {
                addAppointment(scanner);
                break;
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    //? Print Billing Details
    private void printBillingDetails(Appointment appointment) {

        //? Calculate total cost
        double totalCost = 0.0;
        for (Treatment treatment : appointment.getTreatments()) {
            totalCost += treatment.getPrice();
        }
        double tax = totalCost * 2.5 / 100;
        double grandTotal = totalCost + tax + appointment.getRegistrationFee();
        double roundedGrandTotal = Math.round(grandTotal);

        //? Print billing details
        System.out.printf("%-40s%n", "\t\tTotal Cost for Appointment");
        System.out.println("+-------------------------------------------+");
        System.out.printf("%-25s : %-20s %n", "Registration Fee", appointment.getRegistrationFee());
        System.out.printf("%-25s : %-20s %n", "Sub Total", totalCost);
        System.out.printf("%-25s : %-20s %n", "Tax", tax);
        System.out.println("---------------------------------------------");
        System.out.printf("%-25s : %-20s %n", "Total Amount", roundedGrandTotal);
        System.out.println("=============================================");
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

    //! Update Appointment
    public void updateAppointment(Scanner scanner) {
        do {
            System.out.println("\n+--------------------------------------------------------------------+");
            System.out.println("|                           Update Appointment                       |");
            System.out.println("+--------------------------------------------------------------------+");
            System.out.println();

            //? Get Appointment by ID
            Appointment appointment = null;

            while (true) {
                System.out.printf("%-30s", "Enter appointment ID to update (or Enter 'q' to go back): ");
                String appointmentId = scanner.nextLine().trim();

                // Check if the user wants to go back
                if (appointmentId.equalsIgnoreCase("q")) {
                    appointmentManagement(scanner);
                    break;
                }
                // Validate the appointment ID format
                if (!isValidAppointmentId(appointmentId)) {
                    System.out.println("\nInvalid appointment ID format. IDs should start with 'A0' and contain only alphanumeric characters.\n");
                    continue;
                }

                // Fetch and display the appointment
                appointment = appointmentService.getAppointmentById(appointmentId);
                if (appointment == null) {
                    System.out.println("\nAppointment not found. Please try again.\n");
                } else {
                    break;
                }
            }

            //? Print Appointment Details
            printAppointmentDetails(appointment);
            System.out.println("-------------------------------------------------------------------");

            //? Select doctor
            System.out.println("\n--------------------------Doctors List-----------------------------");
            List<Doctor> doctorList = doctorService.getDoctorList();

            int doctorNo = 0;
            if (doctorList.isEmpty()) {
                System.out.println("\nNo doctor found.");
            } else {
                for (Doctor doctor : doctorList) {
                    System.out.printf("\n%-20s : %d%n", "Doctor No", ++doctorNo);
                    System.out.printf("%-20s : %s%n", "Doctor ID", doctor.getDoctorId());
                    System.out.printf("%-20s : %s%n", "Name", doctor.getName());
                    System.out.printf("%-20s : %s%n", "Phone", doctor.getPhoneNo());
                    System.out.printf("%-20s : %s%n", "Assigned Time Slots", doctorService.printAvailableTimeRange(doctor));
                }
            }
            System.out.println("\n------------------------------------------------------------------");

            //? Get new doctor
            Doctor doctor = null;
            while (doctor == null) {
                System.out.print("Enter new doctor No to assign (or press Enter to skip): ");
                String input = scanner.nextLine();
                if (input.isEmpty()) {
                    doctor = appointment.getDoctor();
                    break;
                }
                int index = Integer.parseInt(input) - 1;
                try {
                    doctor = doctorList.get(index);
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("Doctor not found. Please try again.");
                }
            }

            //? Update the date for the appointment if the doctor is different and user wants
            LocalDate appointmentDate = null;
            LocalTime appointmentTime = null;

            String input = null;
            if (Objects.equals(doctor.getDoctorId(), appointment.getDoctor().getDoctorId())) {
                System.out.print("\nDo you want to choose a new appointment date and time? (y/n): ");
                input = scanner.nextLine();
            } else {
                input = "y";
            }
            if (input.equalsIgnoreCase("n")) {
                appointmentDate = appointment.getAppointmentDate();
                appointmentTime = appointment.getAppointmentTime();

                appointment.setAppointmentDate(appointmentDate);
                appointment.setAppointmentTime(appointmentTime);
            }

            //? Update the date for the appointment
            if (input.equalsIgnoreCase("y")) {
                TimeSlots availableTimeForDay = null;
                while (availableTimeForDay == null) {
                    while (appointmentDate == null) {

                        //? Suggest upcoming two available dates based on doctor's availability
                        List<LocalDate> suggestedDates = doctorService.getUpcomingTwoAvailableDates(doctor);

                        // Display suggested dates
                        System.out.println("\nUpcoming available dates for Doctor " + doctor.getName() + ":");
                        for (int i = 0; i < suggestedDates.size(); i++) {
                            System.out.println((i + 1) + ". " + suggestedDates.get(i));
                        }

                        System.out.print("\nEnter appointment date (YYYY-MM-DD) or choose a suggested date (1 or 2): ");
                        String dateInput = scanner.nextLine();

                        try {
                            if (dateInput.equals("1") || dateInput.equals("2")) {
                                // Use the suggested date
                                appointmentDate = suggestedDates.get(Integer.parseInt(dateInput) - 1);
                            } else {
                                // Parse user input date
                                appointmentDate = LocalDate.parse(dateInput);
                                LocalDate today = LocalDate.now();

                                if (appointmentDate.isBefore(today)) {
                                    System.out.println("The date cannot be in the past. Please enter a valid future date.");
                                    appointmentDate = null;  // Reset appointment date to loop again
                                }
                            }
                        } catch (DateTimeParseException e) {
                            System.out.println("Invalid date. Please try again.");
                            appointmentDate = null;  // Reset appointment date to loop again
                        }
                    }

                    // Check the doctor's availability for the entered day
                    DayOfWeek dayOfWeek = appointmentDate.getDayOfWeek();
                    for (TimeSlots slot : doctor.getAvailability()) {
                        if (slot.getDay().equalsIgnoreCase(dayOfWeek.toString())) {
                            availableTimeForDay = slot;
                            break;
                        }
                    }

                    if (availableTimeForDay == null) {
                        System.out.println("The doctor is not available on " + dayOfWeek + ". Please choose another date.");
                        appointmentDate = null;  // Reset date so user can enter a new one
                    }
                }

                //? Get the time for the appointment
                List<LocalTime> availableSlots = doctorService.getAvailableTimeSlots(doctor, appointmentDate);

                //? Display available time slots
                if (availableSlots.isEmpty()) {
                    System.out.println("No available time slots for this date.");
                    return;
                }

                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

                System.out.println("\nAvailable Time Slots for " + appointmentDate + ":");

                int slotNumber = 1;
                for (LocalTime slot : availableSlots) {
                    String formattedTime = slot.format(timeFormatter);
                    System.out.printf("%d. %s%n", slotNumber++, formattedTime);
                }

                while (appointmentTime == null) {
                    System.out.print("\nEnter the desired time slot Number: ");
                    int inputSlotNo = scanner.nextInt();
                    scanner.nextLine();

                    try {
                        appointmentTime = availableSlots.get(inputSlotNo - 1);
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("Input the number for the selected Slot.");
                    }
                }
                appointment.setAppointmentDate(appointmentDate);
                appointment.setAppointmentTime(appointmentTime);
            }

            //? Set the doctor
            appointment.setDoctor(doctor);

            //? Print Patient Details
            System.out.println("\n---------------------Current Patient Details---------------------");

            System.out.printf("%-20s : %s%n", "Patient Name", appointment.getPatient().getName());
            System.out.printf("%-20s : %s%n", "Patient Phone Number", appointment.getPatient().getPhoneNo());
            System.out.printf("%-20s : %s%n", "Patient Email", appointment.getPatient().getEmail());
            System.out.printf("%-20s : %s%n", "Patient NIC", appointment.getPatient().getNic());
            System.out.println("-----------------------------------------------------------------");

            System.out.print("\nDo you want to update patient details? [y/n]: ");
            String choice = scanner.nextLine();

            //? Update Patient Details
            if (choice.equalsIgnoreCase("y")) {
                System.out.println("\n---------------------Update Patient Details---------------------");
                updateField(scanner, "patient name", appointment.getPatient()::setName);
                updateField(scanner, "patient phone number", appointment.getPatient()::setPhoneNo);
                updateField(scanner, "patient email", appointment.getPatient()::setEmail);
                updateField(scanner, "patient NIC", appointment.getPatient()::setNic);
            }

            System.out.println("-----------------------------------------------------------------");

            System.out.println("\n*----------------Appointment updated successfully----------------*");

            System.out.print("\nDo you want to go back to the previous menu? (y/n): ");
            String input2 = scanner.nextLine();
            if (input2.equalsIgnoreCase("y")) {
                appointmentManagement(scanner);
                break;
            }
            if (input2.equalsIgnoreCase("n")) {
                updateAppointment(scanner);
                break;
            } else {
                appointmentManagement(scanner);
            }
        } while (true);
    }

    //? Update Fields
    private void updateField(Scanner scanner, String fieldName, Consumer<String> updateMethod) {
        while (true) {
            System.out.printf("Enter new %s (or press Enter to skip): ", fieldName);
            String input = scanner.nextLine();
            if (input.isEmpty()) {
                break;
            } else {
                updateMethod.accept(input);
                break;
            }
        }
    }

    //! Delete Appointment
    public void deleteAppointment(Scanner scanner) {

        System.out.println("\n+--------------------------------------------------------------------+");
        System.out.println("|                           Delete Appointment                       |");
        System.out.println("+--------------------------------------------------------------------+");
        System.out.println("\n");

        System.out.println("____________________________Appointments List_________________________");
        System.out.print("\n");
        List<Appointment> appointmentsList = appointmentService.getAppointmentsList();
        if (appointmentsList.isEmpty()) {
            System.out.println("No appointment found.");
        } else {
            for (Appointment appointment : appointmentsList) {
                System.out.println(appointment);
                System.out.print("------------------------------------------------------------------\n");
            }
        }

        //? Get Appointment by ID
        Appointment appointment = null;

        while (true) {
            System.out.printf("%-30s", "Enter appointment ID to update (or Enter 'q' to go back): ");
            String appointmentId = scanner.nextLine().trim();

            // Check if the user wants to go back
            if (appointmentId.equalsIgnoreCase("q")) {
                appointmentManagement(scanner);
                break;
            }
            // Validate the appointment ID format
            if (!isValidAppointmentId(appointmentId)) {
                System.out.println("\nInvalid appointment ID format. IDs should start with 'A0' and contain only alphanumeric characters.\n");
                continue;
            }

            // Fetch and display the appointment
            appointment = appointmentService.getAppointmentById(appointmentId);
            if (appointment == null) {
                System.out.println("\nAppointment not found. Please try again.\n");
            } else {
                break;
            }
        }

        do {
            System.out.print("\nDo you want to go back to the previous menu? (y/n): ");
            String choice = scanner.nextLine();
            if (choice.equalsIgnoreCase("y")) {
                appointmentManagement(scanner);
                break;
            } else if (choice.equalsIgnoreCase("n")) {
                deleteAppointment(scanner);
                break;
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        } while (true);
    }

    //! View Appointments Menu
    public void viewAppointment(Scanner scanner) {

        System.out.println("\n+--------------------------------------------------------------------+");
        System.out.println("|                          View Appointments                         |");
        System.out.println("+--------------------------------------------------------------------+");
        System.out.println("\n");

        System.out.println("1. View all appointments          [#1]");
        System.out.println("2. View appointments by date      [#2]");
        System.out.println("3. Search appointments by ID      [#3]");
        System.out.println("4. Search appointments by patient [#4]");
        System.out.println("5. Search appointments by doctor  [#5]");
        System.out.println("6. Back to Home                   [#6]");
        System.out.println("7. Exit                           [#7]");
        System.out.print("\nEnter your choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                viewAllAppointments(scanner);
                break;
            case 2:
                viewAppointmentsByDate(scanner);
                break;
            case 3:
                searchAppointmentById(scanner);
                break;
            case 4:
                searchAppointmentByPatient(scanner);
                break;
            case 5:
                searchAppointmentByDoctor(scanner);
                break;
            case 6:
                homeCLI.start();
                break;
            case 7:
                System.exit(0);
                break;
            default:
        }
    }

    //? View All Appointments
    private void viewAllAppointments(Scanner scanner) {

        do {
            System.out.println("\n+--------------------------------------------------------------------+");
            System.out.println("|                        All Appointments                            |");
            System.out.println("+--------------------------------------------------------------------+");

            appointmentService.getAllAppointments();

            System.out.print("\nEnter 'q' to go back: ");
            String choice = scanner.nextLine();

            if (choice.equalsIgnoreCase("q")) {
                viewAppointment(scanner);
                break;

            } else {
                System.out.println("Invalid choice. Please try again.");
            }

        } while (true);
    }

    //? View Appointments by Date
    private void viewAppointmentsByDate(Scanner scanner) {
        System.out.println("\n+--------------------------------------------------------------------+");
        System.out.println("|                       Appointments by date                         |");
        System.out.println("+--------------------------------------------------------------------+");
        System.out.println("\n");

        while (true) {
            System.out.print("Enter appointment date (YYYY-MM-DD) (or press 'q' to go back): ");
            String dateInput = scanner.nextLine();

            if (dateInput.equalsIgnoreCase("q")) {
                viewAppointment(scanner);
                break;
            }

            try {
                LocalDate localDate = LocalDate.parse(dateInput);
                List<Appointment> appointmentsListByDate = appointmentService.getAppointmentsByDate(localDate);

                if (appointmentsListByDate.isEmpty()) {
                    System.out.println("No appointment found.");
                } else {
                    appointmentsListByDate.forEach(this::printAppointmentDetails);
                }
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please try again.");
            }
        }
    }

    //? Search Appointment by ID
    private void searchAppointmentById(Scanner scanner) {
        System.out.println("\n+--------------------------------------------------------------------+");
        System.out.println("|                          Search Appointment by ID                  |");
        System.out.println("+--------------------------------------------------------------------+\n");

        while (true) {
            System.out.print("Enter appointment ID to search (or press 'q' to go back): ");
            String appointmentId = scanner.nextLine().trim();

            // Check if the user wants to go back
            if (appointmentId.equalsIgnoreCase("q")) {
                viewAppointment(scanner);
                break;
            }

            // Validate the appointment ID format
            if (!isValidAppointmentId(appointmentId)) {
                System.out.println("\nInvalid appointment ID format. IDs should start with 'A0' and contain only alphanumeric characters.\n");
                continue;
            }

            // Fetch and display the appointment
            Appointment appointment = appointmentService.getAppointmentById(appointmentId);
            if (appointment == null) {
                System.out.println("\nAppointment not found. Please try again.\n");
            } else {
                printAppointmentDetails(appointment);
                break;  // Exit loop after successful retrieval
            }
        }
    }

    //? Helper method to validate appointment ID format
    private boolean isValidAppointmentId(String appointmentId) {
        return appointmentId.startsWith("A0") && appointmentId.matches("A0\\w+");
    }

    //? Search Appointment by Patient
    private void searchAppointmentByPatient(Scanner scanner) {
        System.out.println("\n+--------------------------------------------------------------------+");
        System.out.println("|                      Search Appointment by Patient                 |");
        System.out.println("+--------------------------------------------------------------------+");
        System.out.println("\n");

        while (true) {
            System.out.print("Enter patient name to search (or 'q' to go back): ");
            String patientName = scanner.nextLine();

            if (patientName.equalsIgnoreCase("q")) {
                viewAppointment(scanner);
                break;
            }

            List<Appointment> appointmentsListByPatient = appointmentService.getAppointmentsByPatient(patientName);

            if (appointmentsListByPatient.isEmpty()) {
                System.out.println("No appointment found. Please try again.");
            } else {
                appointmentsListByPatient.forEach(this::printAppointmentDetails);
            }
        }
    }

    //? Search Appointment by Doctor
    private void searchAppointmentByDoctor(Scanner scanner) {
        System.out.println("\n+--------------------------------------------------------------------+");
        System.out.println("|                      Search Appointment by Doctor                  |");
        System.out.println("+--------------------------------------------------------------------+");
        System.out.println("\n");


        do {
            System.out.print("Enter doctor name to search (or 'q' to go back): ");
            String doctorName = scanner.nextLine();
            if (doctorName.equalsIgnoreCase("q")) {
                viewAppointment(scanner);
                break;
            }

            List<Appointment> appointmentsListByDoctor = appointmentService.getAppointmentsByDoctor(doctorName);
            if (appointmentsListByDoctor.isEmpty()) {
                System.out.println("No appointment found.");
            } else {
                for (Appointment appointment : appointmentsListByDoctor) {
                    printAppointmentDetails(appointment);
                }
            }
        } while (true);
    }
}
