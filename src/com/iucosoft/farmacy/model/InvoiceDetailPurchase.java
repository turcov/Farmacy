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
public class InvoiceDetailPurchase extends InvoiceDetail{

    public InvoiceDetailPurchase() {
    }

    public InvoiceDetailPurchase(int id, int idInvoice, int idMedicament, double quantity, double price, double total) {
        super(id, idInvoice, idMedicament, quantity, price, total);
    }

    public InvoiceDetailPurchase(int idInvoice, int idMedicament, double quantity, double price, double total) {
        super(idInvoice, idMedicament, quantity, price, total);
    }

    
    @Override
    public String getTableName() {
        return "invoices_p_details";
    }

    @Override
    public String getIdInvoiceInTable() {
        return "idInvoiceP";
    }

    @Override
    public String getPriceInTable() {
        return "unitPrice";
    }

    @Override
    public InvoiceDetail getInvoiceDetail(int id, int idInvoice, int idMedicament, double quantity, double price, double total) {
        return new InvoiceDetailPurchase(id, idInvoice, idMedicament, quantity, price, total);
    }

    @Override
    public int coefQuant() {
        return -1;
    }

    @Override
    public Class getInvoiceClass() {
        return InvoicePurchase.class;
    }

}
