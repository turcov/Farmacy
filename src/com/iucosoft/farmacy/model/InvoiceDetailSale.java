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
public class InvoiceDetailSale extends InvoiceDetail{

    public InvoiceDetailSale() {
    }

    public InvoiceDetailSale(int id, int idInvoice, int idMedicament, double quantity, double price, double total) {
        super(id, idInvoice, idMedicament, quantity, price, total);
    }

    public InvoiceDetailSale(int idInvoice, int idMedicament, double quantity, double price, double total) {
        super(idInvoice, idMedicament, quantity, price, total);
    }

    
    @Override
    public String getTableName() {
        return "invoices_s_details";
    }

    @Override
    public String getIdInvoiceInTable() {
        return "idInvoiceS";
    }

    @Override
    public String getPriceInTable() {
        return "saleUnitPrice";
    }

    @Override
    public InvoiceDetail getInvoiceDetail(int id, int idInvoice, int idMedicament, double quantity, double price, double total) {
        return new InvoiceDetailSale(id, idInvoice, idMedicament, quantity, price, total);
    }

    @Override
    public int coefQuant() {
        return 1;
    }

    @Override
    public Class getInvoiceClass() {
        return InvoiceSale.class;
    }


}
