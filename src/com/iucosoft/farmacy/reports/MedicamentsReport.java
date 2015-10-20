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
public class MedicamentsReport {
    private int id;
    private String nameMedicament;
    private String category;
    private String latinName;

    public int getId() {
        return id;
    }

    public String getNameMedicament() {
        return nameMedicament;
    }

    public String getCategory() {
        return category;
    }

    public String getLatinName() {
        return latinName;
    }

    public MedicamentsReport(int id, String nameMedicament, String category, String latinName) {
        this.id = id;
        this.nameMedicament = nameMedicament;
        this.category = category;
        this.latinName = latinName;
    }
    
}
