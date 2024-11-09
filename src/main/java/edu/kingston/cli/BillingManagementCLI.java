package edu.kingston.cli;

import edu.kingston.model.*;
import edu.kingston.service.AppointmentService;
import edu.kingston.service.BillingService;
import edu.kingston.service.TreatmentService;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class BillingManagementCLI {

    private final BillingService billingService;
    private final AppointmentService appointmentService;
    private final TreatmentService treatmentService;
    private final HomeCLI homeCLI;
    public BillingManagementCLI(HomeCLI homeCLI, AppointmentService appointmentService, BillingService billingService, TreatmentService treatmentService) {
        this.homeCLI = homeCLI;
        this.billingService = billingService;
        this.appointmentService = appointmentService;
        this.treatmentService = treatmentService;
    }

    public void billingManagement(Scanner scanner) {

        System.out.println("\n+---------------------------------------------------------------------+");
        System.out.println("|                           Billing Management                        |");
        System.out.println("+---------------------------------------------------------------------+");
        System.out.println("\n Please select an option:\n");

        System.out.println("1. Generate Bill     [#1]");
        System.out.println("2. View Bills        [#2]");
        System.out.println("3. Back to menu      [#3]");
        System.out.println("4. Exit              [#4]");
        System.out.print("\nEnter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                generateBill(scanner);
                break;
            case 2:
                viewBills(scanner);
                break;
            case 3:
                homeCLI.start();
                break;
            case 4:
                System.exit(0);
                break;
            default:
                break;
        }
    }

    // Helper method to validate appointment ID format
    private boolean isValidAppointmentId(String appointmentId) {
        return appointmentId.startsWith("A0") && appointmentId.matches("A0\\w+");
    }

    //? Generate Bill
    public void generateBill(Scanner scanner) {
        System.out.println("\n+------------------------------------------------+");
        System.out.println("|                      Billing                   |");
        System.out.println("+------------------------------------------------+\n");

        //? Get Appointment by
        Appointment appointment = null;

        while (true) {
            System.out.printf(" %-26s : ", "Enter appointment id");
            String appointmentId = scanner.nextLine().trim();

            // Check if the user wants to go back
            if (appointmentId.equalsIgnoreCase("q")) {
                billingManagement(scanner);
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

        // Generate a unique bill ID
        String billId = generateBillId();

        // Display bill details
        System.out.printf("+------------------------------------------------+%n");
        System.out.printf("| %-25s : %-18s |%n", "Invoice ID", billId);
        System.out.println("+------------------------------------------------+");

        System.out.println("|------------------ Appointment -----------------|");
        System.out.printf("| %-25s : %-18s |%n", "Appointment ID", appointment.getAppointmentId());
        System.out.printf("| %-25s : %-18s |%n", "Patient Name", appointment.getPatient().getName());
        System.out.printf("| %-25s : %-18s |%n", "Doctor Name", appointment.getDoctor().getName());
        System.out.printf("| %-25s : %-18s |%n", "Appointment Date", appointment.getAppointmentDate());
        System.out.printf("| %-25s : %-18s |%n", "Appointment Time", appointment.getAppointmentTime());
        System.out.printf("| %-25s : LKR %-14.2f |%n", "Registration Fee (Paid)", appointment.getRegistrationFee());
        System.out.println("+------------------------------------------------+");

        System.out.println("|-------------Selected Treatments----------------|");
        appointment.getTreatments().forEach(treatment -> {
            System.out.printf("| %-25s : LKR %-14.2f |%n", treatment.getName(), treatment.getPrice());
        });
        System.out.println("+------------------------------------------------+");

        // Edit Treatments List if needed
        System.out.print("\nDo you want to edit the treatment list? [y/n]: ");
        String editTreatments = scanner.nextLine();

        if (editTreatments.equalsIgnoreCase("y")) {
            System.out.println("\n+----------------Edit Treatments-----------------+");

            System.out.print("\nSelect a number to edit: ");
            System.out.print("\n\t1. Add additional Treatments\n\t2. Remove all Treatments and add New\n\nEnter your choice: ");
            String choice = scanner.nextLine();
            if (choice.equals("1")) {
                editTreatmentList(scanner,appointment, appointment.getTreatments());
            }
            if (choice.equals("2")) {
                appointment.setTreatments(new ArrayList<>());
                editTreatmentList(scanner, appointment, new ArrayList<>());
            }
        }

        // Display appointment details with updated treatments as an Invoice
        System.out.printf("\n+------------------------------------------------+%n");
        System.out.printf("| %-25s : %-18s |%n", "Invoice ID", billId);
        System.out.println("+------------------------------------------------+");

        System.out.println("|------------------ Appointment -----------------|");
        System.out.printf("| %-25s : %-18s |%n", "Appointment ID", appointment.getAppointmentId());
        System.out.printf("| %-25s : %-18s |%n", "Patient Name", appointment.getPatient().getName());
        System.out.printf("| %-25s : %-18s |%n", "Doctor Name", appointment.getDoctor().getName());
        System.out.printf("| %-25s : %-18s |%n", "Appointment Date", appointment.getAppointmentDate());
        System.out.printf("| %-25s : %-18s |%n", "Appointment Time", appointment.getAppointmentTime());
        System.out.printf("| %-25s : LKR %-14.2f |%n", "Registration Fee (Paid)", appointment.getRegistrationFee());
        System.out.println("+------------------------------------------------+");

        System.out.println("|----------------Treatments Done-----------------|");
        appointment.getTreatments().forEach(treatment -> {
            System.out.printf("| %-25s : LKR %-14.2f |%n", treatment.getName(), treatment.getPrice());
        });
        System.out.println("+------------------------------------------------+");

        // Calculate and display total cost
        double totalCost = 0.0;
        for (Treatment treatment : appointment.getTreatments()) {
            totalCost += treatment.getPrice();
        }
        double tax = totalCost * 2.5 / 100;
        double grandTotal = totalCost + tax;
        double roundedGrandTotal = Math.round(grandTotal) ;

        System.out.printf("| %-25s : LKR %-14.2f |%n", "Sub Total", totalCost);
        System.out.printf("| %-25s : LKR %-14.2f |%n", "Tax", tax);
        System.out.println("+------------------------------------------------+");
        System.out.printf("| %-25s : LKR %-14.2f |%n", "Total Amount", roundedGrandTotal);
        System.out.println("==================================================");

        // Choose Payment method
        System.out.println("\n---------------- Payment Method ------------------");
        System.out.println("\t\t1. Cash");
        System.out.println("\t\t2. Credit Card");
        System.out.println("\t\t3. Debit Card\n");
        PaymentMethod paymentMethod = null;
        while (paymentMethod == null) {
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();
                switch (choice) {
                    case 1:
                        paymentMethod = PaymentMethod.CASH;
                        break;
                    case 2:
                        paymentMethod = PaymentMethod.CREDIT_CARD;
                        break;
                    case 3:
                        paymentMethod = PaymentMethod.DEBIT_CARD;
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter 1, 2, or 3.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number (1, 2, or 3).");
                scanner.nextLine(); // Clear the invalid input
            }
        }

        // Cash Payment Method
        if (paymentMethod == PaymentMethod.CASH) {
            double receivedAmount = 0.0;
            while (true) {
                System.out.println("--------------------------------------------------");
                System.out.print("Enter received amount: ");
                try {
                    receivedAmount = scanner.nextDouble();
                    scanner.nextLine();
                    if (receivedAmount < grandTotal) {
                        System.out.printf("Insufficient amount. Please enter at least LKR %.2f\n", roundedGrandTotal);
                    } else {
                        break;
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a valid amount.");
                    scanner.nextLine(); // Clear the invalid input
                }
            }

            // Calculate change
            double change = receivedAmount - roundedGrandTotal;

            System.out.println("--------------------------------------------------");
            if (change > 0) {
                System.out.printf("Change: LKR %.2f\n", change);
            } else if (change < 0) {
                System.out.printf("Balance amount: LKR %.2f\n", Math.abs(change));
            } else {
                System.out.println("Exact amount received. No change or balance.");
            }
        }

        // Print total amount and payment method
        System.out.println("--------------------------------------------------");
        System.out.printf("Payment of LKR %.2f has been made using %s ", roundedGrandTotal, paymentMethod);
        System.out.println("\n--------------------------------------------------");
        System.out.println("\n \t\t Thank you for using our services!");
        System.out.println("--------------------------------------------------");

        // Add Bill to list
        billingService.addBill(new Bill(billId, appointment, totalCost, tax, roundedGrandTotal, paymentMethod));

        while (true) {
            System.out.print("\nBill generated successfully. Do you want to generate another bill? (y/n): ");
            String response = scanner.nextLine();
            if (response.equalsIgnoreCase("n")) {
                billingManagement(scanner);
                break;
            } else if (response.equalsIgnoreCase("y")) {
                generateBill(scanner);
                break;
            } else {
                System.out.print("Invalid input. Please enter 'y' or 'n'.");
            }
        }
    }

    //? Edit Treatments List
    private void editTreatmentList(Scanner scanner, Appointment appointment, List<Treatment> treatments) {
        if (treatments.isEmpty()) {
            treatments = new ArrayList<>();
        }

        // List all treatments
        List<Treatment> allTreatments = treatmentService.getTreatmentsList();
        int treatmentNo = 1;

        System.out.println("\n-------------------------- Treatments List -------------------------------");
        System.out.printf("%-15s %-15s %-30s %-20s%n", "Number", "ID", "Name", "Price (LKR)");
        System.out.println("--------------------------------------------------------------------------");

        for (Treatment treatment : allTreatments) {
            System.out.printf("%-15d %-15s %-30s %-20.2f%n", treatmentNo++, treatment.getTreatmentId(), treatment.getName(), treatment.getPrice());
        }
        System.out.println("--------------------------------------------------------------------------");

        // Add treatments
        do {
            System.out.print("\nSelect a new treatment by entering the Number (or Enter to skip): ");
            String input = scanner.nextLine();
            if (input == null || input.isEmpty()) {
                break;
            }

            int index = Integer.parseInt(input) - 1;
            Treatment treatment = allTreatments.get(index);

            if (treatment == null) {
                System.out.println("Treatment not found. Please try again.");
            } else {
                treatments.add(treatment);
            }
        } while (true);

        // Set treatments to appointment
        appointment.setTreatments(treatments);
    }

    //? Generate bill id
    private static int counter = 1;
    public String generateBillId() {
        String base = "B" + String.format("%02d", counter);
        counter++;
        return base;
    }

    //? View bills menu
    public void viewBills(Scanner scanner) {

        System.out.println("\n+--------------------------------------------------------------------+");
        System.out.println("|                           Bill Management                          |");
        System.out.println("+--------------------------------------------------------------------+");
        System.out.println("\n");

        System.out.println("1. View all bills              [#1]");
        System.out.println("2. Search by Appointment ID    [#2]");
        System.out.print("\nEnter your choice: ");
        String choice = scanner.nextLine();
        scanner.nextLine();

        List<Bill> billsToDisplay;

        if (choice.equals("1")) {

            System.out.println("\n+-----------------------------------------------------+");
            System.out.println("|                  View all Bills                     |");
            System.out.println("+-----------------------------------------------------+");

            // View all bills
            billsToDisplay = billingService.getBillList();

        } else if (choice.equals("2")) {

            System.out.println("\n+-----------------------------------------------------+");
            System.out.println("|                  View Bills by ID                   |");
            System.out.println("+-----------------------------------------------------+");

            // Filter by appointment ID
            System.out.print("\nEnter appointment ID to search: ");
            String appointmentId = scanner.nextLine();
            billsToDisplay = billingService.getBillList().stream()
                    .filter(bill -> bill.getAppointment().getAppointmentId().equals(appointmentId))
                    .collect(Collectors.toList());

            if (billsToDisplay.isEmpty()) {
                System.out.println("No bills found for appointment ID: " + appointmentId);
                System.out.println("\nInvalid choice. Returning to menu.");
                viewBills(scanner);
            }

        } else {
            System.out.println("Invalid choice. Returning to menu.");
            viewBills(scanner);
            return;
        }

        // Display the bills
        System.out.println("*---------------------Bill Details--------------------*\n");

        billsToDisplay.forEach(bill -> {

            System.out.printf("Bill ID: %-15s | Appointment ID: %-15s\n", bill.getBillId(), bill.getAppointment().getAppointmentId());
            System.out.println("-----------------------------------------------------------------");
            System.out.printf("Patient: %-15s | Doctor: %-15s\n", bill.getAppointment().getPatient().getName(), bill.getAppointment().getDoctor().getName());
            System.out.println("-----------------------------------------------------------------");
            System.out.printf("Date: %-15s    | Time: %-15s%n", bill.getAppointment().getAppointmentDate(), bill.getAppointment().getAppointmentTime());
            System.out.println("-----------------------------------------------------------------");

            double roundedGrandTotal = Math.round(bill.getGrandTotal());
            System.out.printf("%-24s : LKR %.2f%n", "Registration Fee [Paid]", bill.getAppointment().getRegistrationFee());
            System.out.printf("%-24s : LKR %.2f%n", "Sub Total", bill.getTotalCost());
            System.out.printf("%-24s : LKR %.2f%n", "Tax", bill.getTax());
            System.out.printf("%-24s : LKR %.2f%n", "Total Amount", roundedGrandTotal);
            System.out.println("-----------------------------------------------------------------");
            System.out.printf("%-24s : %s%n", "Payment Method", bill.getPaymentMethod());
            System.out.print("=================================================================\n");


        });

        // Option to generate new bill or return to billing management
        while (true) {
            System.out.print("\nDo you want to generate a new bill? (y/n): ");
            String response = scanner.nextLine();
            if (response.equalsIgnoreCase("n")) {
                billingManagement(scanner);
                break;
            } else if (response.equalsIgnoreCase("y")) {
                generateBill(scanner);
                break;
            } else {
                System.out.println("Invalid input. Please enter 'y' or 'n'.");
            }
        }
    }
}
