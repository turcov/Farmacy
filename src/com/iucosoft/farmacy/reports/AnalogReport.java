/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.reports;

/**
 *
 * @author Turkov S
 */
public class AnalogReport {
    private String analogName;
    private int nn; 
    
    public AnalogReport(String analogName,int nn) {
        this.analogName = analogName;
        this.nn=nn;
    }

    public String getAnalogName() {
        return analogName;
    }

    public int getNn() {
        return nn;
    }
    
}
