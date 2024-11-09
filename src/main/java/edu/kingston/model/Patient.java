package edu.kingston.model;


public class Patient implements Person{

    private String patientId;
    private String name;
    private String phoneNo;
    private String email;
    private String nic;

    public Patient(String patientId, String name, String phoneNo, String email, String nic) {
        this.patientId = patientId;
        this.name = name;
        this.phoneNo = phoneNo;
        this.email = email;
        this.nic = nic;
    }

    @Override
    public String toString() {
        return "ID: " + patientId + "\n" +
                "Name: " + name + "\n" +
                "Phone No: " + phoneNo + "\n" +
                "Email: " + email + "\n" +
                "NIC: " + nic + "\n";
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getPhoneNo() {
        return phoneNo;
    }

    @Override
    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }
}
