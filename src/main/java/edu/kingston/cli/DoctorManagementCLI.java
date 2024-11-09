package edu.kingston.cli;

import edu.kingston.model.Doctor;
import edu.kingston.model.TimeSlots;
import edu.kingston.service.DoctorService;

import java.util.*;

public class DoctorManagementCLI {

    private static int counter = 1;
    private final DoctorService doctorService;
    private final HomeCLI homeCLI;

    public DoctorManagementCLI(HomeCLI homeCLI, DoctorService doctorService) {
        this.homeCLI = homeCLI;
        this.doctorService = doctorService;
    }

    //? Generate ID for doctor
    public static String generateIdForDoctor() {
        String base = "D";
        String formattedCounter = String.format("%02d", counter);
        counter++;
        String id = base + formattedCounter;
        return id;
    }

    public void doctorManagement(Scanner scanner) {

        System.out.println("\n+--------------------------------------------------------------------+");
        System.out.println("|                        Doctor Management                           |");
        System.out.println("+--------------------------------------------------------------------+");
        System.out.println("\n Please select an option:\n");

        System.out.println("1. Add doctor     [#1]");
        System.out.println("2. Update doctor  [#2]");
        System.out.println("3. Delete doctor  [#3]");
        System.out.println("4. View doctors   [#4]");
        System.out.println("5. Back to menu   [#5]");
        System.out.println("6. Exit           [#6]");
        System.out.print("\nEnter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                addDoctor(scanner);
                break;
            case 2:
                updateDoctor(scanner);
                break;
            case 3:
                deleteDoctor(scanner);
                break;
            case 4:
                viewDoctor(scanner);
                break;
            case 5:
                homeCLI.start();
                break;
            case 6:
                System.out.println("\nThank you for using Aura Skin Care Clinic. Goodbye!\n");
                System.exit(0);
                break;
            default:
                System.out.println("\nInvalid choice. Please try again.\n");
        }
    }

    //? Add doctor
    public void addDoctor(Scanner scanner) {

        do {
            System.out.println("\n+--------------------------------------------------------------------+");
            System.out.println("|                            Add Doctor                              |");
            System.out.println("+--------------------------------------------------------------------+");
            System.out.println("\n");

            //? Generate ID
            String id = generateIdForDoctor();
            System.out.print("\tDoctor Id : " + id);
            System.out.print("\n+----------------------+\n\n");

            //? Get doctor details
            System.out.printf("%-30s : ", "Enter doctor's name");
            String name = scanner.nextLine();

            System.out.printf("%-30s : ", "Enter doctor's phone number");
            String phone = scanner.nextLine();

            //? Display available time slots
            System.out.println("\nAvailable time slots:\n");
            int i = 1;
            for (TimeSlots slot : TimeSlots.values()) {
                if (!doctorService.isTimeSlotAllocated(slot)) {
                    System.out.println(i++ + ". " + slot.getDay() + ": " + slot.getTimeRange());
                }
            }

            List<TimeSlots> selectedTimeSlots = new ArrayList<>();

            while (true) {
                System.out.print("\nTo select the time slot enter the specific number (e.g., '1') or 'q' to finish: ");
                String input = scanner.nextLine();

                switch (input) {
                    case "1":
                        input = "MONDAY";
                        break;
                    case "2":
                        input = "WEDNESDAY";
                        break;
                    case "3":
                        input = "FRIDAY";
                        break;
                    case "4":
                        input = "SATURDAY";
                        break;
                    case "q":
                        System.out.println("\nDoctor's assigned time slots: " + selectedTimeSlots);
                        break;
                    default:
                        System.out.println("\nInvalid input. Please try again.");
                        continue;
                }

                if (input.equals("q")) {
                    break;
                }

                TimeSlots timeSlot = TimeSlots.fromString(input);
                if (timeSlot == null) {
                    System.out.println("\nInvalid time slot. Please try again.");
                } else if (!selectedTimeSlots.contains(timeSlot) && !doctorService.isTimeSlotAllocated(timeSlot)) {
                    selectedTimeSlots.add(timeSlot);
                    doctorService.addAllocatedTimeSlots(timeSlot);
                } else {
                    System.out.println("\nTime slot already selected.");
                }
            }

            Doctor doctor = new Doctor(id, name, phone, selectedTimeSlots);
            doctorService.addDoctor(doctor);

            System.out.print("\nDo you want to add another doctor? (y/n): ");
            char answer = scanner.next().charAt(0);
            scanner.nextLine();

            if (answer == 'n' || answer == 'N') {
                doctorManagement(scanner);
                break;

            } else if (answer != 'y' && answer != 'Y') {
                System.out.println("\nInvalid input. Returning to the doctor management menu.");
                doctorManagement(scanner);
                break;
            }

        } while (true);
    }

    //? View doctor
    void viewDoctor(Scanner scanner) {
        do {
            System.out.println("\n+--------------------------------------------------------------------+");
            System.out.println("|                           Doctors List                             |");
            System.out.println("+--------------------------------------------------------------------+");

            List<Doctor> doctorList = doctorService.getDoctorList();
            if (doctorList.isEmpty()) {
                System.out.println("\nNo doctor found.");
            } else {
                for (Doctor doctor : doctorList) {
                    System.out.println("\n---------------------------Doctor Details---------------------------");
                    System.out.printf("\n%-20s : ","Doctor ID" + doctor.getDoctorId());
                    System.out.printf("\n%-20s : ","Name" + doctor.getName());
                    System.out.printf("\n%-20s : ","Phone Number" + doctor.getPhoneNo());
                    System.out.printf("\n%-20s : ","Assigned Time Slots" + doctor.getAvailability());
                    System.out.println("--------------------------------------------------------------------\n");
                }
            }
            System.out.print("\nDo you want to add a new doctor? (y/n): ");
            char answer = scanner.next().charAt(0);
            scanner.nextLine();
            if (answer == 'n' || answer == 'N') {
                doctorManagement(scanner);
            } else if (answer == 'y' || answer == 'Y') {
                addDoctor(scanner);
            } else {
                System.out.println("Invalid input. Please try again.");
            }
        } while (true);
    }

    //? Delete doctor
    void deleteDoctor(Scanner scanner) {

        do {
            System.out.println("\n+--------------------------------------------------------------------+");
            System.out.println("|                          Delete Doctor                             |");
            System.out.println("+--------------------------------------------------------------------+");

            List<Doctor> doctorList = doctorService.getDoctorList();
            if (doctorList.isEmpty()) {
                System.out.println("\nNo doctor found.");
            } else {
                for (Doctor doctor : doctorList) {
                    System.out.println("\n---------------------------Doctor Details---------------------------");
                    System.out.printf("\n%-20s : ","Doctor ID" + doctor.getDoctorId());
                    System.out.printf("\n%-20s : ","Name" + doctor.getName());
                    System.out.printf("\n%-20s : ","Phone Number" + doctor.getPhoneNo());
                    System.out.printf("\n%-20s : ","Assigned Time Slots" + doctor.getAvailability());
                    System.out.println("--------------------------------------------------------------------\n");
                }
            }

            System.out.print("\nEnter doctor ID to delete: ");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("q")){
                doctorManagement(scanner);
            } else {
                doctorService.deleteDoctor(input);
                System.out.println("\nDoctor deleted successfully.");
            }

            System.out.print("\nDo you want to delete another doctor? (y/n): ");
            char answer = scanner.next().charAt(0);
            scanner.nextLine();
            if (answer == 'n' || answer == 'N') {
                deleteDoctor(scanner);
            } else if (answer == 'y' || answer == 'Y') {
                doctorManagement(scanner);
            } else {
                System.out.println("Invalid input. Please try again.");
            }

        } while (true);

    }

    //? Update doctor
    void updateDoctor(Scanner scanner) {

        do {
            System.out.println("\n+--------------------------------------------------------------------+");
            System.out.println("|                          Update Doctor                             |");
            System.out.println("+--------------------------------------------------------------------+");

            List<Doctor> doctorList = doctorService.getDoctorList();
            if (doctorList.isEmpty()) {
                System.out.println("\nNo doctor found.");
            } else {
                for (Doctor doctor : doctorList) {
                    System.out.println("\n----------------------------Doctor Details----------------------------");
                    System.out.printf("\n%-30s : %-20s", "Doctor ID", doctor.getDoctorId());
                    System.out.printf("\n%-30s : %-20s", "Name", doctor.getName());
                    System.out.printf("\n%-30s : %-20s", "Phone Number", doctor.getPhoneNo());
                    System.out.printf("\n%-30s : %-20s", "Assigned Time Slots", doctorService.printAvailability(doctor));
                    System.out.println("\n----------------------------------------------------------------------");
                }
            }

            System.out.print("\nEnter doctor ID to update: ");
            String doctorId = scanner.nextLine();

            Doctor doctor = doctorService.getDoctorById(doctorId);
            if (doctor == null) {
                System.out.println("Doctor not found. Please try again.");
                continue;
            }
            System.out.print("Enter new name (Press Enter to skip): ");
            String name = scanner.nextLine();
            if (name.isEmpty()) {
                name = doctor.getName();
            } else {
                doctor.setName(name);
            }
            System.out.print("Enter new phone number (Press Enter to skip): ");
            String phone = scanner.nextLine();
            if (phone.isEmpty()) {
                phone = doctor.getPhoneNo();
            } else {
                doctor.setPhoneNo(phone);
            }

            List<TimeSlots> selectedTimeSlots;
            do {
                System.out.print("Enter new time slot (Press Enter to skip): ");
                String input = scanner.nextLine();

                // Convert input to TimeSlot using fromString method
                TimeSlots timeSlot = TimeSlots.fromString(input);
                selectedTimeSlots = doctor.getAvailability();

                if (input == null || input.isEmpty()) {
                    break;
                }

                // Check for invalid time slots
                if (timeSlot == null) {
                    System.out.println("\nInvalid time slot. Please try again.");
                    continue;
                }

                // Handle duplicate or already allocated time slots
                if (selectedTimeSlots.contains(timeSlot)) {
                    System.out.print("\nTime slot already assigned to this doctor. Please try again.");
                    continue;
                }

                if (doctorService.isTimeSlotAllocated(timeSlot)) {
                    System.out.print("\nTime slot is already allocated to another doctor. Please try again.");
                    continue;
                }

                // Check if the user wants to clear all time slots
                System.out.print("\nDo you want to clear all time slots to add new ones? (y/n): ");
                char answer = scanner.next().charAt(0);
                scanner.nextLine();

                if (answer == 'y' || answer == 'Y') {
                    selectedTimeSlots.clear();
                }

                // Add the time slot to the doctor and allocated time slots
                selectedTimeSlots.add(timeSlot);
                doctorService.addAllocatedTimeSlots(timeSlot);

                // Ask the user if they want to add another time slot
                System.out.print("\nDo you want to add another time slot? (y/n): ");
                char addMore = scanner.next().charAt(0);
                scanner.nextLine();

                if (addMore == 'n' || addMore == 'N') {
                    break;
                }

            } while (true);

            doctor.setAvailability(selectedTimeSlots);
            System.out.println("\nDoctor updated successfully.");

            // Prompt to update another doctor
            System.out.print("\nDo you want to update another doctor? (y/n): ");
            char answer = scanner.next().charAt(0);
            scanner.nextLine();
            if (answer == 'n' || answer == 'N') {
                doctorManagement(scanner);
            } else if (answer == 'y' || answer == 'Y') {
                updateDoctor(scanner);
            } else {
                System.out.println("Invalid input. Please try again.");
            }

        } while (true);
    }
}
