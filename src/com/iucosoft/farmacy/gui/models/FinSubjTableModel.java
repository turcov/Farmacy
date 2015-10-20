/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.gui.models;

import com.iucosoft.farmacy.dao.FinanceSubjectDaoIntf;
import com.iucosoft.farmacy.dao.impl.FinanceSubjectDaoImpl;
import com.iucosoft.farmacy.exceptions.ConnectionInterruptedException;
import com.iucosoft.farmacy.model.FinanceSubject;
import java.util.List;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Turkov S
 */
public class FinSubjTableModel extends DefaultTableModel {

    String[] columnNames = new String[3];
    FinanceSubjectDaoIntf clSupDao;
    
    public FinSubjTableModel(Class clas) throws IllegalAccessException, InstantiationException, ConnectionInterruptedException {
        clSupDao = new FinanceSubjectDaoImpl(clas);
        setColumns(clas);
        refreshModel();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false; 
    }
    
    private void setColumns(Class clas) {
        columnNames[0] = "ID ";
        columnNames[1] = clas.getSimpleName() + "'s Name";
        columnNames[2] = clas.getSimpleName() + "'s Account";
        for (String columnName : columnNames) {
            super.addColumn(columnName);
        }

    }

    public void refreshModel(int row, FinanceSubject finSubj) {
        if (row >= 0) {
            super.setValueAt(finSubj.getId(), row, 0);
            super.setValueAt(finSubj.getName(), row, 1);
            super.setValueAt(finSubj.getAccount(), row, 2);
        } else {
            super.removeRow(Math.abs(row)-1);
        }
    }

    public void refreshModel(FinanceSubject finSubj) {
        Vector vector = new Vector();
        vector.add(finSubj.getId());
        vector.add(finSubj.getName());
        vector.add(finSubj.getAccount());
        super.addRow(vector);
    }

    public void refreshModel(List<FinanceSubject> fsList) {
        for (int i = getRowCount() - 1; i >= 0; i--) {
            super.removeRow(i);
        }
        Vector vector;
        for (FinanceSubject fs : fsList) {
            vector = new Vector();
            vector.add(fs.getId());
            vector.add(fs.getName());
            vector.add(fs.getAccount());
            super.addRow(vector);
        }
    }

    public void refreshModel() {
        List<FinanceSubject> fsList = clSupDao.findAllFinSubects();
        refreshModel(fsList);
    }

}
