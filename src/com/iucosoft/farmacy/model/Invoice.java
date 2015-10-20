/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.model;

import java.sql.Date;


/**
 *
 * @author Turkov S
 */
public abstract class Invoice {

    private int idInvoice;
    private int idFinSubj;
    private Date dateInvoice;
    private double totalInvoice;

    public Invoice() {
    }

    public Invoice(int idFinObj, Date dataInvoice, double totalInvoice) {
        this.idFinSubj = idFinObj;
        this.dateInvoice = dataInvoice;
        this.totalInvoice = totalInvoice;
    }

    public Invoice(int idInvoice, int idFinSubj, Date dataInvoice, double totalInvoice) {
        this.idInvoice = idInvoice;
        this.idFinSubj = idFinSubj;
        this.dateInvoice = dataInvoice;
        this.totalInvoice = totalInvoice;
    }

    public double getTotalInvoice() {
        return totalInvoice;
    }

    public void setTotalInvoice(double totalInvoice) {
        this.totalInvoice = totalInvoice;
    }

    public int getIdInvoice() {
        return idInvoice;
    }

    public void setIdInvoice(int idInvoice) {
        this.idInvoice = idInvoice;
    }

    public int getIdFinSubj() {
        return idFinSubj;
    }

    public void setIdFinObj(int idFinSubj) {
        this.idFinSubj = idFinSubj;
    }

    public Date getDateInvoice() {
        return dateInvoice;
    }

    public void setDateInvoice(Date dataInvoice) {
        this.dateInvoice = dataInvoice;
    }

    @Override
    public String toString() {
        return "Invoice{" + "idInvoice=" + idInvoice + ", idFinSubj="+idFinSubj+", dateInvoice=" + dateInvoice + ", totalInvoice=" + totalInvoice + '}';
    }

    public abstract String getTableName();

    public abstract String getIdInvoiceInTable();

    public abstract String getIdFinObjInTable();

    public abstract String getDateInvoiceInTable();

    public abstract String getTotalInvoiceInTable();

    public abstract Invoice getInvoice(int idInvoice, int idFinSubj, Date dateInvoice, double totalInvoice);
    
    public abstract FinanceSubject getNewFinanceSubject();
    
    public abstract Class getFinanceSubjectClass();
    
    public abstract Class getInvoiceDetailClass();
    
    public abstract String getPriceMethodName();
}
