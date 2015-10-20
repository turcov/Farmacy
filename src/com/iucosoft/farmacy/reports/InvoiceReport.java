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
public class InvoiceReport {
    private int nn;
    private String medicament;
    private double quantity;
    private String price;
    private String summ;

    public InvoiceReport(int nn, String medicament, double quantity, String price, String summ) {
        this.nn = nn;
        this.medicament = medicament;
        this.quantity = quantity;
        this.price = price;
        this.summ = summ;
    }

    public int getNn() {
        return nn;
    }

    public String getMedicament() {
        return medicament;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getPrice() {
        return price;
    }

    public String getSumm() {
        return summ;
    }
    
}
