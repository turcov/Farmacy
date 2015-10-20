/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.model;

import java.util.Objects;
import javax.swing.Icon;

/**
 *
 * @author Turkov S
 */
public class Medicament {
    private int idMedicament;
    private String nameMedicament;
    private int idCategory;
    private String latinNameMedicament;
    private byte[] iconMedicament;

    public Medicament(int idMedicament, String nameMedicament, int idCategoria, String latinNameMedicament, byte[] iconMedicament) {
        this.idMedicament = idMedicament;
        this.nameMedicament = nameMedicament;
        this.idCategory = idCategoria;
        this.latinNameMedicament = latinNameMedicament;
        this.iconMedicament=iconMedicament;
    }

    public Medicament(String nameMedicament, int idCategoria, String latinNameMedicament,byte[] iconMedicament) {
        this.nameMedicament = nameMedicament;
        this.idCategory = idCategoria;
        this.latinNameMedicament = latinNameMedicament;
        this.iconMedicament=iconMedicament;
    }

    public Medicament() {
    }

    public Medicament(String nameMedicament) {
        this.nameMedicament = nameMedicament;
    }

    public int getIdMedicament() {
        return idMedicament;
    }

    public void setIdMedicament(int idMedicament) {
        this.idMedicament = idMedicament;
    }

    public String getNameMedicament() {
        return nameMedicament;
    }

    public void setNameMedicament(String nameMedicament) {
        this.nameMedicament = nameMedicament;
    }

    public int getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(int idCategoria) {
        this.idCategory = idCategoria;
    }

    public String getLatinNameMedicament() {
        return latinNameMedicament;
    }

    public void setLatinNameMedicament(String latinNameMedicament) {
        this.latinNameMedicament = latinNameMedicament;
    }

    @Override
    public String toString() {
        return  nameMedicament ;
    }

    
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + this.idMedicament;
        hash = 83 * hash + Objects.hashCode(this.nameMedicament);
        hash = 83 * hash + this.idCategory;
        hash = 83 * hash + Objects.hashCode(this.latinNameMedicament);
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
        final Medicament other = (Medicament) obj;
        if (this.idMedicament != other.idMedicament) {
            return false;
        }
        if (!Objects.equals(this.nameMedicament, other.nameMedicament)) {
            return false;
        }
        if (this.idCategory != other.idCategory) {
            return false;
        }
        if (!Objects.equals(this.latinNameMedicament, other.latinNameMedicament)) {
            return false;
        }
        return true;
    }

    public byte[] getIconMedicament() {
        return iconMedicament;
    }

    public void setIconMedicament(byte[] iconMedicament) {
        this.iconMedicament = iconMedicament;
    }
    
    
}
