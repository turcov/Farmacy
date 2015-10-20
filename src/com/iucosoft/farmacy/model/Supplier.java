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
public class Supplier extends FinanceSubject {

    public Supplier() {
        super();
    }

    public Supplier(String name, String account) {
        super(name, account);
    }

    public Supplier(int id, String name, String account) {
        super(id, name, account);
    }

    @Override
    public String getAccountInTable() {
        return "accountSupplier"; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getNameInTable() {
        return "nameSupplier"; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getIdInTable() {
        return "idSupplier"; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getTableName() {
        return "suppliers"; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public FinanceSubject getFinanceSubject(int id, String name, String account) {
        return new Supplier(id,name,account);
    }

    @Override
    public Class getInvoiceClass() {
        return InvoicePurchase.class;
    }

}
