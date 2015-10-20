/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.gui.models;

import com.iucosoft.farmacy.dao.CategoryDaoIntf;
import com.iucosoft.farmacy.dao.MedicamentDaoIntf;
import com.iucosoft.farmacy.dao.impl.CategoryDaoImpl;
import com.iucosoft.farmacy.dao.impl.MedicamentDaoImpl;
import com.iucosoft.farmacy.exceptions.ConnectionInterruptedException;
import com.iucosoft.farmacy.model.Category;
import com.iucosoft.farmacy.model.Medicament;
import com.iucosoft.farmacy.utils.Util;
import java.util.List;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Turkov S
 */
public class MedicamentTableModel extends DefaultTableModel {

    MedicamentDaoIntf medDao;
    CategoryDaoIntf catDao;
    String[] columnNames = {"ID", "Medicament name", "Category", "Latin Name"};

    public MedicamentTableModel() throws ConnectionInterruptedException {
        medDao = new MedicamentDaoImpl();
        catDao = new CategoryDaoImpl();
        setColumnNames();
        refreshModel();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    void setColumnNames() {
        for (String acolumn : columnNames) {
            super.addColumn(acolumn);
        }
    }

    //public void refreshModel
    public void refreshModel() {
        List<Medicament> listMed = medDao.findAllMedicaments();
        refreshModel(listMed);
    }

    public void refreshModel(int row, Medicament medicament) {
        if (row >= 0) {
            super.setValueAt(medicament.getIdMedicament(), row, 0);
            super.setValueAt(medicament.getNameMedicament(), row, 1);
            if (medicament.getIdCategory() > 0) {
                super.setValueAt(catDao.findByIdCategory(medicament.getIdCategory()).getNameCategory(), row, 2);
            } else {
                super.setValueAt("No category", row, 2);
            }
            super.setValueAt(medicament.getLatinNameMedicament(), row, 3);
        } else {
            super.removeRow(Math.abs(row) - 1);
        }
    }

    public void refreshModel(Medicament medicament) {
        Vector vector = new Vector();
        vector.add(medicament.getIdMedicament());
        vector.add(medicament.getNameMedicament());
        if (medicament.getIdCategory() > 0) {
            vector.add(catDao.findByIdCategory(medicament.getIdCategory()).getNameCategory());
        } else {
            vector.add(new Category("No category").getNameCategory());
        }
        vector.add(medicament.getLatinNameMedicament());
        super.addRow(vector);

    }

    public void refreshModel(List<Medicament> list) {
        Util.clearModel(this);
        Vector vector;
        for (Medicament amedicament : list) {
            vector = new Vector();
            vector.add(amedicament.getIdMedicament());
            vector.add(amedicament.getNameMedicament());
            if (amedicament.getIdCategory() > 0) {
                vector.add(catDao.findByIdCategory(amedicament.getIdCategory()).getNameCategory());
            } else {
                vector.add(new Category("No category").getNameCategory());
            }
            vector.add(amedicament.getLatinNameMedicament());
            super.addRow(vector);
        }
    }

}
