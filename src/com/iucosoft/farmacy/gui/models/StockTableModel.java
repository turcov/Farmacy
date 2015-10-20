/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.gui.models;

import com.iucosoft.farmacy.dao.StockDaoIntf;
import com.iucosoft.farmacy.dao.impl.StockDaoImpl;
import com.iucosoft.farmacy.exceptions.ConnectionInterruptedException;
import com.iucosoft.farmacy.model.Stock;
import com.iucosoft.farmacy.utils.Util;
import java.util.List;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Turkov S
 */
public class StockTableModel extends DefaultTableModel {

    StockDaoIntf stockDao;
    String[] columnNames = {"ID", "Name of the Medicament", "Balance"};
    public boolean canEdit[] = {false, false, false};

    public StockTableModel() throws ConnectionInterruptedException {
        stockDao = new StockDaoImpl();
        setColumnNames();
        refreshModel();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return canEdit[column];
    }

    Class[] types = new Class[]{
        java.lang.Integer.class, java.lang.String.class, java.lang.Double.class
    };

    public Class getColumnClass(int columnIndex) {
        return types[columnIndex];
    }

    private void setColumnNames() {
        for (String column : columnNames) {
            super.addColumn(column);
        }
    }

    public void refreshModel() {
        List<Stock> listMed = stockDao.findAllStockList();
        refreshModel(listMed);

    }

    public void refreshModel(List<Stock> listMed) {
        Util.clearModel(this);
        Vector vector;
        for (Stock aStock : listMed) {
            vector = new Vector();
            vector.add(aStock.getIdMedicament());
            vector.add(aStock.getNameMedicament());
            vector.add(aStock.getBalance());
            super.addRow(vector);
        }

    }

}
