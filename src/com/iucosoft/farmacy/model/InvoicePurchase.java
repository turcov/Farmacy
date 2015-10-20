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
public class InvoicePurchase extends Invoice {

    public InvoicePurchase() {
    }

    public InvoicePurchase(int idFinObj, Date dateInvoice, double totalInvoice) {
        super(idFinObj, dateInvoice, totalInvoice);
    }

    public InvoicePurchase(int idInvoice, int idFinSubj, Date dateInvoice, double totalInvoice) {
        super(idInvoice, idFinSubj, dateInvoice, totalInvoice);
    }

    public String getTableName() {
        return "invoices_purchases";
    }

    public String getIdInvoiceInTable() {
        return "idInvoiceP";
    }

    public String getIdFinObjInTable() {
        return "idSupplier";
    }

    public String getDateInvoiceInTable() {
        return "dateInvoiceP";
    }

    public String getTotalInvoiceInTable() {
        return "totalInvoiceP";
    }

    public Invoice getInvoice(int idInvoice, int idFinSubj, Date dateInvoice, double totalInvoice) {
        return new InvoicePurchase(idInvoice, idFinSubj, dateInvoice, totalInvoice);
    }

    @Override
    public FinanceSubject getNewFinanceSubject() {
        return new Supplier();
    }

    @Override
    public Class getFinanceSubjectClass() {
        return Supplier.class;
    }

    @Override
    public Class getInvoiceDetailClass() {
        return InvoiceDetailPurchase.class;
    }

    @Override
    public String getPriceMethodName() {
        return "getUnitPrice";
    }
}
