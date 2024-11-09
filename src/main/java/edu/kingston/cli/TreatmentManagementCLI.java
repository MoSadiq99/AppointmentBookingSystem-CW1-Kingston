package edu.kingston.cli;

import edu.kingston.model.Treatment;
import edu.kingston.service.TreatmentService;

import java.util.List;
import java.util.Scanner;

public class TreatmentManagementCLI {

    private final TreatmentService treatmentService;
    private final HomeCLI homeCLI;

    public TreatmentManagementCLI(HomeCLI homeCLI, TreatmentService treatmentService) {
        this.homeCLI = homeCLI;
        this.treatmentService = treatmentService;
    }

    public void treatmentManagement(Scanner scanner) {

        System.out.println("\n+--------------------------------------------------------------------------------+");
        System.out.println("|                                Treatment Management                           |");
        System.out.println("+--------------------------------------------------------------------------------+");
        System.out.println("\n Please select an option:\n");

        System.out.println("1. Add treatment     [#1]");
        System.out.println("2. Update treatment  [#2]");
        System.out.println("3. Delete treatment  [#3]");
        System.out.println("4. View treatments   [#4]");
        System.out.println("5. Back to home      [#5]");
        System.out.println("6. Exit              [#6]");
        System.out.print("\nEnter your choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                addTreatment(scanner);
                break;
            case 2:
                updateTreatment(scanner);
                break;
            case 3:
                deleteTreatment(scanner);
                break;
            case 4:
                viewTreatments(scanner);
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
                break;
        }
    }

    private static int counter = 1;
    private String generateIdForTreatment() {
        String base = "T";
        String formattedCounter = String.format("%02d", counter);
        counter++;
        String id = base + formattedCounter;
        return id;
    }

    public void addTreatment(Scanner scanner) {

        do{
            System.out.println("\n+--------------------------------------------------------------------+");
            System.out.println("|                            Add Treatment                           |");
            System.out.println("+--------------------------------------------------------------------+");
            System.out.println("\n");

            String treatmentId = generateIdForTreatment();
            System.out.printf("%-20s : ", "Treatment ID", treatmentId);

            System.out.printf("\n%-20s : ", "Treatment Name");
            String treatmentName = scanner.nextLine();
            System.out.printf("\n%-20s : ", "Treatment Price");
            Double treatmentPrice = scanner.nextDouble();
            scanner.nextLine();

            Treatment treatment = new Treatment(treatmentId, treatmentName, treatmentPrice);
            treatmentService.addTreatment(treatment);

            System.out.println("\nTreatment added successfully.\n");
            System.out.println("Do you want to add another treatment? (\"N\" to go back)? (Y/N): ");
            String choice = scanner.nextLine();
            if(choice.equalsIgnoreCase("N")) {
                treatmentManagement(scanner);
            }
        }while (true);
    }

    public void viewTreatments(Scanner scanner) {

        System.out.println("\n+--------------------------------------------------------------------+");
        System.out.println("|                            Treatments List                         |");
        System.out.println("+--------------------------------------------------------------------+");
        System.out.println("\n");

        List<Treatment> treatments = treatmentService.getTreatmentsList();

        for (Treatment treatment : treatments) {
            System.out.printf("%-20s","Treatment ID : ", treatment.getTreatmentId());
            System.out.printf("\n%-20s","Treatment Name : ", treatment.getName());
            System.out.printf("\n%-20s","Treatment Price : ", treatment.getPrice());
            System.out.println();
        }

        System.out.println("\n");
        while (true) {
            System.out.print("Do you want to add a new treatment ('n' to go back)? (y/n): ");
            String choice = scanner.nextLine();
            if (choice.equalsIgnoreCase("Y")) {
                addTreatment(scanner);
            } else if (choice.equalsIgnoreCase("N")) {
                treatmentManagement(scanner);
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }
    }


    public void updateTreatment(Scanner scanner) {

        System.out.println("\n+--------------------------------------------------------------------+");
        System.out.println("|                            Update Treatment                        |");
        System.out.println("+--------------------------------------------------------------------+");
        System.out.println("\n");

        viewTreatments(scanner);

        System.out.print("Enter Treatment ID to update: ");
        String treatmentId = scanner.nextLine();
        Treatment treatment = treatmentService.getTreatmentById(treatmentId);

        if (treatment == null) {
            System.out.println("Treatment not found.");
            return;
        }

        System.out.println("Current Treatment Name: " + treatment.getName());
        System.out.print("Enter new Treatment Name: ");
        String newTreatmentName = scanner.nextLine();
        System.out.println("Current Treatment Price: " + treatment.getPrice());
        System.out.print("Enter new Treatment Price: ");
        Double newTreatmentPrice = scanner.nextDouble();
        scanner.nextLine();

        treatment.setName(newTreatmentName);
        treatment.setPrice(newTreatmentPrice);
    }

    public void deleteTreatment(Scanner scanner) {

        System.out.println("\n+--------------------------------------------------------------------+");
        System.out.println("|                           Delete Treatment                         |");
        System.out.println("+--------------------------------------------------------------------+");
        System.out.println("\n");

        System.out.println("+-------------------------- Treatments List -------------------------+");
        treatmentService.printTreatments();

        System.out.print("\nEnter Treatment ID to delete: ");
        String treatmentId = scanner.nextLine();
        Treatment treatment = treatmentService.getTreatmentById(treatmentId);

        if (treatment == null) {
            System.out.println("Treatment not found. Please try again.");
        }else {
            System.out.printf("%-20s","Treatment ID : ", treatment.getTreatmentId());
            System.out.printf("\n%-20s","Treatment Name : ", treatment.getName());
            System.out.printf("\n%-20s","Treatment Price : ", treatment.getPrice());
            System.out.println();

            System.out.print("Are you sure you want to delete this treatment? (y/n): ");
            String choice = scanner.nextLine();
            if (choice.equalsIgnoreCase("y")) {
                treatmentService.deleteTreatment(treatment);
                System.out.println("Treatment deleted successfully.");
            } else {
                System.out.println("Deletion cancelled.");
            }
        }
    }
}


