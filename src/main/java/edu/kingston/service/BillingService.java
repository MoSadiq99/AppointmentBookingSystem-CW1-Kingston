package edu.kingston.service;

import edu.kingston.model.*;

import java.util.ArrayList;
import java.util.List;

public class BillingService {
    public static List<Bill> billList = new ArrayList<>();

    //? Add bill
    public static void addBill(Bill bill) {
        billList.add(bill);
    }

    //? Get bill list
    public static List<Bill> getBillList() {
        return billList;
    }

    //? Get bill by id
    public static Bill getBillById(String billId) {
        for (Bill bill : billList) {
            if (bill.getBillId().equals(billId)) {
                return bill;
            }
        }
        return null;
    }
}
