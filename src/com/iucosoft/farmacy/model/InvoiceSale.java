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
public class InvoiceSale extends Invoice {

    public InvoiceSale() {
    }

    public InvoiceSale(int idFinObj, Date dateInvoice, double totalInvoice) {
        super(idFinObj, dateInvoice, totalInvoice);
    }

    public InvoiceSale(int idInvoice, int idFinSubj, Date dateInvoice, double totalInvoice) {
        super(idInvoice, idFinSubj, dateInvoice, totalInvoice);
    }

    public String getTableName() {
        return "invoices_sales";
    }

    public String getIdInvoiceInTable() {
        return "idInvoiceS";
    }

    public String getIdFinObjInTable() {
        return "idClient";
    }

    public String getDateInvoiceInTable() {
        return "dateInvoiceS";
    }

    public String getTotalInvoiceInTable() {
        return "totalInvoiceS";
    }

    public Invoice getInvoice(int idInvoice, int idFinSubj, Date dateInvoice, double totalInvoice) {
        return new InvoiceSale(idInvoice, idFinSubj, dateInvoice, totalInvoice);
    }

    @Override
    public FinanceSubject getNewFinanceSubject() {
        return new Client();
    }

    @Override
    public Class getFinanceSubjectClass() {
        return Client.class;
    }

    @Override
    public Class getInvoiceDetailClass() {
        return InvoiceDetailSale.class;
    }

    @Override
    public String getPriceMethodName() {
        return "getSaleUnitPrice";
    }
}
