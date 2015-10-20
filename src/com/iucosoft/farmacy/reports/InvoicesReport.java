/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.reports;

import java.sql.Date;

/**
 *
 * @author Serguei
 */
public class InvoicesReport {

    private int idInvoice;
    private String nameFinSubj;
    private Date date;
    private String total;

    public InvoicesReport(int idInvoice, String nameFinSubj, Date date, String total) {
        this.idInvoice = idInvoice;
        this.nameFinSubj = nameFinSubj;
        this.date = date;
        this.total = total;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public int getIdInvoice() {
        return idInvoice;
    }

    public void setIdInvoice(int idInvoice) {
        this.idInvoice = idInvoice;
    }

    public String getNameFinSubj() {
        return nameFinSubj;
    }

    public void setNameFinSubj(String nameFinSubj) {
        this.nameFinSubj = nameFinSubj;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
