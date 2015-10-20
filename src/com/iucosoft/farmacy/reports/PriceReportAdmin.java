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
public class PriceReportAdmin {

    private int id;
    private String nameMedicament;
    private String price;
    private double margin;
    private String sellPrice;

    public PriceReportAdmin(int id, String nameMedicament, String price, double margin, String sellPrice) {
        this.id = id;
        this.nameMedicament = nameMedicament;
        this.price = price;
        this.margin = margin;
        this.sellPrice = sellPrice;
    }

    public int getId() {
        return id;
    }

    public String getNameMedicament() {
        return nameMedicament;
    }

    public String getPrice() {
        return price;
    }

    public double getMargin() {
        return margin;
    }

    public String getSellPrice() {
        return sellPrice;
    }

    
    
    
}
