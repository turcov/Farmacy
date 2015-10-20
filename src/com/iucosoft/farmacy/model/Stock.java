/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.model;

/**
 *
 * @author Serguei
 */
public class Stock {
    private int idMedicament;
    private double balance;
    private String nameMedicament;

    public Stock() {
    }

    public Stock(double balance) {
        this.balance = balance;
    }

    public Stock(int idMedicament, double balance) {
        this.idMedicament = idMedicament;
        this.balance = balance;
    }

    public int getIdMedicament() {
        return idMedicament;
    }

    public void setIdMedicament(int idMedicament) {
        this.idMedicament = idMedicament;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getNameMedicament() {
        return nameMedicament;
    }

    public void setNameMedicament(String nameMedicament) {
        this.nameMedicament = nameMedicament;
    }
    
    
}
