/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.reports;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Serguei
 */
public class Report {

    private String farmacyName;
    private String header;
    private String innerData;
    private String footer;
    private List data = new ArrayList();

    
    public void setFarmacyName(String farmacyName) {
        this.farmacyName = farmacyName;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public void setInnerData(String innerData) {
        this.innerData = innerData;
    }

    public Report() {
    }

    public List getData() {
        return data;
    }

    public void setData(List data) {
        this.data = data;
    }

    public String getFarmacyName() {
        return farmacyName;
    }

    public String getHeader() {
        return header;
    }

    public String getInnerData() {
        return innerData;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

}
