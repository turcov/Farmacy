/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.reports;

import java.sql.Date;

/**
 *
 * @author Turkov S
 */
public class ActReport {

    private Date date;
    private Integer invNo;
    private String supplier;
    private Double quantityS;
    private String client;
    private Double quantityC;
    private Double saldo;

    public ActReport(Date date, Integer invNo, String supplier, Double quantityS, String client, Double quantityC, Double saldo) {
        this.date = date;
        this.invNo = invNo;
        this.supplier = supplier;
        this.quantityS = quantityS;
        this.client = client;
        this.quantityC = quantityC;
        this.saldo = saldo;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getInvNo() {
        return invNo;
    }

    public void setInvNo(Integer invNo) {
        this.invNo = invNo;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public Double getQuantityS() {
        return quantityS;
    }

    public void setQuantityS(Double quantityS) {
        this.quantityS = quantityS;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public Double getQuantityC() {
        return quantityC;
    }

    public void setQuantityC(Double quantityC) {
        this.quantityC = quantityC;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }
    
    
}
