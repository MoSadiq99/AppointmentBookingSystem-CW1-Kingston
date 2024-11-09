package edu.kingston.model;

public class Bill {

    private String billId;
    private Appointment appointment;
    private double totalCost;
    private double tax;
    private double grandTotal;
    private PaymentMethod paymentMethod;

    public Bill(String billId, Appointment appointment, double totalCost, double tax, double grandTotal, PaymentMethod paymentMethod) {
        this.billId = billId;
        this.appointment = appointment;
        this.totalCost = totalCost;
        this.tax = tax;
        this.grandTotal = grandTotal;
        this.paymentMethod = paymentMethod;
    }

    public double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(double grandTotal) {
        this.grandTotal = grandTotal;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public double getTax() {
        return tax;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }
}
