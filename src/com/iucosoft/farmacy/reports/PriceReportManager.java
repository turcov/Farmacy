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
public class PriceReportManager {

    private int id;
    private String nameMedicament;
    private String sellPrice;

    public PriceReportManager(int id, String nameMedicament, String sellPrice) {
        this.id = id;
        this.nameMedicament = nameMedicament;
        this.sellPrice = sellPrice;
    }

    public int getId() {
        return id;
    }

    public String getNameMedicament() {
        return nameMedicament;
    }

    public String getSellPrice() {
        return sellPrice;
    }

    
    
    
}
