/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.model;

import java.util.Objects;

/**
 *
 * @author Turkov S
 */
public abstract class FinanceSubject {

    private int id;
    private String name;
    private String account;

    public FinanceSubject() {
    }

    public FinanceSubject(String name, String account) {
        this.name = name;
        this.account = account;
    }

    public FinanceSubject(int id, String name, String account) {
        this.id = id;
        this.name = name;
        this.account = account;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.id;
        hash = 79 * hash + Objects.hashCode(this.name);
        hash = 79 * hash + Objects.hashCode(this.account);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FinanceSubject other = (FinanceSubject) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.account, other.account)) {
            return false;
        }
        return true;
    }

    
    
    public abstract String getTableName();

    public abstract String getIdInTable();

    public abstract String getNameInTable();

    public abstract String getAccountInTable();

    public abstract FinanceSubject getFinanceSubject(int id, String name, String account);
    
    public abstract Class getInvoiceClass();

}
