package edu.kingston.cli;


import edu.kingston.cli.AppointmentManagementCLI;
import edu.kingston.cli.BillingManagementCLI;
import edu.kingston.cli.DoctorManagementCLI;
import edu.kingston.cli.TreatmentManagementCLI;
import edu.kingston.service.*;

import java.util.Scanner;

public class HomeCLI {
    private final Scanner scanner = new Scanner(System.in);

    // Shared service instances
    private final PatientService patientService = new PatientService();
    private final TreatmentService treatmentService = new TreatmentService();
    private final AppointmentService appointmentService = new AppointmentService();
    private final BillingService billingService = new BillingService();
    private final DoctorService doctorService = new DoctorService(appointmentService);

    // Shared service instances to each cli
    private final DoctorManagementCLI doctorManagementCLI = new DoctorManagementCLI(this, doctorService);
    private final TreatmentManagementCLI treatmentManagementCLI = new TreatmentManagementCLI(this, treatmentService);
    private final AppointmentManagementCLI appointmentManagementCLI = new AppointmentManagementCLI(this, doctorService, patientService, appointmentService, treatmentService);
    private final BillingManagementCLI billingManagementCLI = new BillingManagementCLI(this, appointmentService, billingService, treatmentService);

    public void start() {
        // Add default treatments and doctors
        treatmentService.addDefaultTreatments();
        doctorService.addDefaultDoctors();

        System.out.println("\n+-----------------------------------------------------------------------+");
        System.out.println("|                              WELCOME TO                               |");
        System.out.println("+-----------------------------------------------------------------------+");
        System.out.println("\n");

        System.out.println("           $$$$        $$       $$   $$$$$$$$$         $$$$");
        System.out.println("          $$  $$       $$       $$   $$      $$       $$  $$");
        System.out.println("         $$    $$      $$       $$   $$     $$       $$    $$");
        System.out.println("        $$$$$$$$$$     $$       $$   $$$$$$$$       $$$$$$$$$$");
        System.out.println("       $$        $$    $$       $$   $$     $$     $$        $$");
        System.out.println("      $$          $$    $$$$$$$$$    $$      $$$  $$          $$");

        System.out.println("\n+-----------------------------------------------------------------------+");
        System.out.println("|                            SKIN CARE CLINIC                           |");
        System.out.println("+-----------------------------------------------------------------------+");


        System.out.println("\n Please select an option:\n");

        System.out.println("1. Appointment Management   [#1]");
        System.out.println("2. Billing Management       [#2]");
        System.out.println("3. Doctor Management        [#3]");
        System.out.println("4. Treatment Management     [#4]");
        System.out.print("\nEnter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                appointmentManagementCLI.appointmentManagement(scanner);
                break;
            case 2:
                billingManagementCLI.billingManagement(scanner);
                break;
            case 3:
                doctorManagementCLI.doctorManagement(scanner);
                break;
            case 4:
                treatmentManagementCLI.treatmentManagement(scanner);
                break;
            case 5:
                System.out.println("\nThank you for using Aura Skin Care Clinic. Goodbye!\n");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }
}
