/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.model;

/**
 *
 * @author Turkov S
 */
public abstract class InvoiceDetail {
    private int id;
    private int idInvoice;
    private int idMedicament;
    private double quantity;
    private double price;
    private double total;

    public InvoiceDetail() {
    }

    public InvoiceDetail(int id, int idInvoice, int idMedicament, double quantity, double price, double total) {
        this.id = id;
        this.idInvoice = idInvoice;
        this.idMedicament = idMedicament;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
    }

    public InvoiceDetail(int idInvoice, int idMedicament, double quantity, double price, double total) {
        this.idInvoice = idInvoice;
        this.idMedicament = idMedicament;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdInvoice() {
        return idInvoice;
    }

    public void setIdInvoice(int idInvoice) {
        this.idInvoice = idInvoice;
    }

    public int getIdMedicament() {
        return idMedicament;
    }

    public void setIdMedicament(int idMedicament) {
        this.idMedicament = idMedicament;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "InvoiceDetail{" + "id=" + id + ", idInvoice=" + idInvoice +", idMedicament="+idMedicament+ ", quantity=" + quantity + ", price=" + price + ", total=" + total + '}';
    }
    
    public abstract String getTableName();

    public abstract String getIdInvoiceInTable();
 
    public abstract String getPriceInTable();
    
    public abstract InvoiceDetail getInvoiceDetail(int id,int idInvoice,int idMedicament, double quantity, double price,double total);
    
    public abstract int coefQuant();
    
    public abstract Class getInvoiceClass();

//public abstract Class getFinanceSubjectClass();
    
}
