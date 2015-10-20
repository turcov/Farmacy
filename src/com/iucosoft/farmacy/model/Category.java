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
public class Category {
    private int idCategory;
    private String nameCategory;

    public Category() {
    }

    public Category(String nameCategory) {
        this.nameCategory = nameCategory;
    }

    public Category(int idCategory, String nameCategory) {
        this.idCategory = idCategory;
        this.nameCategory = nameCategory;
    }

    public String getNameCategory() {
        return nameCategory;
    }

    public void setNameCategory(String nameCategory) {
        this.nameCategory = nameCategory;
    }

    public int getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(int idCategory) {
        this.idCategory = idCategory;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + this.idCategory;
        hash = 47 * hash + Objects.hashCode(this.nameCategory);
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
        final Category other = (Category) obj;
        if (this.idCategory != other.idCategory) {
            return false;
        }
        if (!Objects.equals(this.nameCategory, other.nameCategory)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nameCategory;
    }
    
    
}
