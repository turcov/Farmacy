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
public class Price {
    private int idMedicament;
    private double unitPrice;
    private double margin;
    private double saleUnitPrice;
    private String nameMedicament;

    public Price() {
    }

    public Price(double unitPrice, double margin, double saleUnitPrice) {
        this.unitPrice = unitPrice;
        this.margin = margin;
        this.saleUnitPrice = saleUnitPrice;
    }

    public Price(int idMedicament, double unitPrice, double margin, double saleUnitPrice) {
        this.idMedicament = idMedicament;
        this.unitPrice = unitPrice;
        this.margin = margin;
        this.saleUnitPrice = saleUnitPrice;
    }

    public double getSaleUnitPrice() {
        return saleUnitPrice;
    }

    public void setSaleUnitPrice(double saleUnitPrice) {
        this.saleUnitPrice = saleUnitPrice;
    }

    public int getIdMedicament() {
        return idMedicament;
    }

    public void setIdMedicament(int idMedicament) {
        this.idMedicament = idMedicament;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getMargin() {
        return margin;
    }

    public void setMargin(double margin) {
        this.margin = margin;
    }

//    @Override
//    public int hashCode() {
//        int hash = 7;
//        hash = 23 * hash + this.idMedicament;
//        hash = 23 * hash + (int) (Double.doubleToLongBits(this.unitPrice) ^ (Double.doubleToLongBits(this.unitPrice) >>> 32));
//        hash = 23 * hash + (int) (Double.doubleToLongBits(this.margin) ^ (Double.doubleToLongBits(this.margin) >>> 32));
//        hash = 23 * hash + (int) (Double.doubleToLongBits(this.saleUnitPrice) ^ (Double.doubleToLongBits(this.saleUnitPrice) >>> 32));
//        return hash;
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (obj == null) {
//            return false;
//        }
//        if (getClass() != obj.getClass()) {
//            return false;
//        }
//        final Price other = (Price) obj;
//        if (this.idMedicament != other.idMedicament) {
//            return false;
//        }
//        if (Double.doubleToLongBits(this.unitPrice) != Double.doubleToLongBits(other.unitPrice)) {
//            return false;
//        }
//        if (Double.doubleToLongBits(this.margin) != Double.doubleToLongBits(other.margin)) {
//            return false;
//        }
//        if (Double.doubleToLongBits(this.saleUnitPrice) != Double.doubleToLongBits(other.saleUnitPrice)) {
//            return false;
//        }
//        return true;
//    }

    public String getNameMedicament() {
        return nameMedicament;
    }

    public void setNameMedicament(String nameMedicament) {
        this.nameMedicament = nameMedicament;
    }
    
    
    
    
}
