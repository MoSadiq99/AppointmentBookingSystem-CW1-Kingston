package edu.kingston.model;

public class Treatment {

    private String treatmentId;
    private String name;
    private Double price;

    public Treatment(String treatmentId, String name, Double price) {
        this.treatmentId = treatmentId;
        this.name = name;
        this.price = price;
    }

    public String getTreatmentId() {
        return treatmentId;
    }

    public void setTreatmentId(String treatmentId) {
        this.treatmentId = treatmentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
