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
public class Analog {
    private int idMedicament;
    private int idMedicamentAnalog;

    public Analog() {
    }

    public Analog(int idMedicamentAnalog) {
        this.idMedicamentAnalog = idMedicamentAnalog;
    }

    public Analog(int idMedicament, int idMedicamentAnalog) {
        this.idMedicament = idMedicament;
        this.idMedicamentAnalog = idMedicamentAnalog;
    }

    public int getIdMedicamentAnalog() {
        return idMedicamentAnalog;
    }

    public void setIdMedicamentAnalog(int idMedicamentAnalog) {
        this.idMedicamentAnalog = idMedicamentAnalog;
    }

    public int getIdMedicament() {
        return idMedicament;
    }

    public void setIdMedicament(int idMedicament) {
        this.idMedicament = idMedicament;
    }
    
}
