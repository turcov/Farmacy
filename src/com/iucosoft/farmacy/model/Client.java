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
public class Client extends FinanceSubject{

    public Client() {
        super();
    }

    public Client(String name, String account) {
        super(name, account);
    }

    public Client(int id, String name, String account) {
        super(id, name, account);
    }

    @Override
    public String getAccountInTable() {
        return "accountClient"; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getNameInTable() {
        return "nameClient"; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getIdInTable() {
        return "idClient"; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getTableName() {
        return "clients"; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public FinanceSubject getFinanceSubject(int id, String name, String account) {
        return new Client(id, name, account);
    }

    @Override
    public Class getInvoiceClass() {
        return InvoiceSale.class;
    }
}
