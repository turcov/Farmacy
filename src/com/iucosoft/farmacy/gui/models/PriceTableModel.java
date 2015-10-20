/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.gui.models;

import com.iucosoft.farmacy.dao.PriceDaoIntf;
import com.iucosoft.farmacy.dao.impl.PriceDaoImpl;
import com.iucosoft.farmacy.exceptions.ConnectionInterruptedException;
import com.iucosoft.farmacy.model.Category;
import com.iucosoft.farmacy.model.Price;
import com.iucosoft.farmacy.utils.Util;
import java.util.List;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Turkov S
 */
public class PriceTableModel extends DefaultTableModel {

    PriceDaoIntf priceDao;
    boolean isAdmin;
    String[] columnNames;

    public PriceTableModel(boolean isAdmin) throws ConnectionInterruptedException {
        this.isAdmin = isAdmin;
        priceDao = new PriceDaoImpl();
        if (isAdmin) {
            columnNames = new String[]{"ID", "Medicament name", "Unit price", "margin", "Sell unit Price"};
        } else {
            columnNames = new String[]{"ID", "Medicament name", "Sell unit Price"};
        }
        setColumnNames();
        refreshModel();
    }

    private void setColumnNames() {
        for (String column : columnNames) {
            super.addColumn(column);
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public void refreshModel() {
        Util.clearModel(this);
        List<Price> listMed = priceDao.findAllPriceList();
        refreshModel(listMed);
    }

//    public void refreshModel(Category cat){
//        List<Price> listMed = priceDao.findByCategoryPriceList(cat);
//        refreshModel(listMed);
//    }
//    public void refreshModel(String line){
//        List<Price> listMed = priceDao.findByNameMedicamentPriceList(line);
//        refreshModel(listMed);
//    }
    public void refreshModel(int index, Price price) {
        super.setValueAt(price.getIdMedicament(), index, 0);
        super.setValueAt(price.getNameMedicament(), index, 1);
        if (isAdmin) {
            super.setValueAt(price.getUnitPrice() + " MDL", index, 2);
            super.setValueAt(price.getMargin(), index, 3);
        }
        super.setValueAt(price.getSaleUnitPrice() + " MDL", index, 4);
    }

    public void refreshModel(List<Price> listMed) {
        Util.clearModel(this);
        Vector vector;
        for (Price aPrice : listMed) {
            vector = new Vector();
            vector.add(aPrice.getIdMedicament());
            vector.add(aPrice.getNameMedicament());
            if (isAdmin) {
                vector.add(aPrice.getUnitPrice() + " MDL");
                vector.add(aPrice.getMargin());
            }
            vector.add(aPrice.getSaleUnitPrice() + " MDL");
            super.addRow(vector);
        }

    }

}
