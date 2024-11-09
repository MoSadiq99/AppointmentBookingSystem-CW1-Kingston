package edu.kingston.service;

import edu.kingston.model.Treatment;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TreatmentService {

    List<Treatment> treatmentsList = new ArrayList<>();

    //? Add treatment
    public void addTreatment(Treatment treatment) {

        treatmentsList.add(treatment);
    }

    //? Get treatment
    public List<Treatment> getTreatmentsList() {
        return treatmentsList;
    }

    //? Get treatment by ID
    public Treatment getTreatmentById(String treatmentId) {

        for (Treatment treatment : treatmentsList) {
            if (treatment.getTreatmentId().equals(treatmentId)) {
                return treatment;
            }
        }
        return null;
    }

    //? Delete treatment
    public void deleteTreatment(Treatment treatment) {

        treatmentsList.remove(treatment);
    }

    public void addDefaultTreatments() {
        // Check if each default treatment exists before adding
        if (getTreatmentById("T01") == null) {
            addTreatment(new Treatment("T01", "Acne Treatment", 2750.00));
        }
        if (getTreatmentById("T02") == null) {
            addTreatment(new Treatment("T02", "Skin Whitening", 7650.00));
        }
        if (getTreatmentById("T03") == null) {
            addTreatment(new Treatment("T03", "Mole Removal", 3850.00));
        }
        if (getTreatmentById("T04") == null) {
            addTreatment(new Treatment("T04", "Laser Treatment", 12500.00));
        }
    }

    public void printTreatments() {

        for (Treatment treatment : treatmentsList) {
            System.out.printf("%-20s","Treatment ID : ", treatment.getTreatmentId());
            System.out.printf("\n%-20s","Treatment Name : ", treatment.getName());
            System.out.printf("\n%-20s","Treatment Price : ", treatment.getPrice());
            System.out.println("----------------------------------------------------------------------");
            System.out.println();
        }
    }
}
