package edu.kingston.service;

import edu.kingston.model.Patient;

import java.util.ArrayList;
import java.util.List;

public class PatientService {
    private List<Patient> patientsList = new ArrayList<>();

    //? Add Patient
    public void addPatient(Patient patient) {
        patientsList.add(patient);
    }

    //? Find Patient by NIC
    public Patient findPatientByNic(String nic) {
        for (Patient patient : patientsList) {
            if (patient.getNic().equals(nic)) {
                return patient;
            }
        }
        return null;
    }

    //? Generate Patient ID
    private static int counter = 1;
    public String generateIdForPatient() {
        String base = "P";
        String formattedCounter = String.format("%02d", counter);
        counter++;
        String id = base + formattedCounter;
        return id;
    }
}
