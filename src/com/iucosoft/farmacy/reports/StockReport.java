/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.reports;

/**
 *
 * @author Turkov S
 */
public class StockReport {
    private int id;
    private String nameMedicament;
    private double balance;

    public StockReport(int id, String nameMedicament, double balance) {
        this.id = id;
        this.nameMedicament = nameMedicament;
        this.balance = balance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameMedicament() {
        return nameMedicament;
    }

    public void setNameMedicament(String nameMedicament) {
        this.nameMedicament = nameMedicament;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

  
}
