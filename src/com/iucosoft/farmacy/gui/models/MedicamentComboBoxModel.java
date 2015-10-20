/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.gui.models;

import com.iucosoft.farmacy.dao.MedicamentDaoIntf;
import com.iucosoft.farmacy.dao.impl.CategoryDaoImpl;
import com.iucosoft.farmacy.dao.impl.MedicamentDaoImpl;
import com.iucosoft.farmacy.exceptions.ConnectionInterruptedException;
import com.iucosoft.farmacy.model.Medicament;
import java.util.List;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author Turkov S
 */
public class MedicamentComboBoxModel extends DefaultComboBoxModel<Medicament> {

    MedicamentDaoIntf medDao;

    public MedicamentComboBoxModel() throws ConnectionInterruptedException {
        medDao = new MedicamentDaoImpl();
        refreshModel();
    }

    public void refreshModel() {
        super.removeAllElements();
        super.addElement(new Medicament("No medicament"));
        List<Medicament> listMed = medDao.findAllMedicaments();
        for (Medicament aMedicament : listMed) {
            super.addElement(aMedicament);
        }
    }

}
