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
import com.iucosoft.farmacy.model.Category;
import com.iucosoft.farmacy.model.Medicament;
import java.util.List;
import javax.swing.DefaultListModel;

/**
 *
 * @author Serguei
 */
public class MedicamentListModel extends DefaultListModel<Medicament> {

    MedicamentDaoIntf medDao;

    public MedicamentListModel() throws ConnectionInterruptedException {
        medDao = new MedicamentDaoImpl();
    }

    public void refreshModel() {
        super.clear();
        List<Medicament> listMed = medDao.findAllMedicaments();
        for (Medicament amed : listMed) {
            super.addElement(amed);
        }
    }

    public void refreshAnalogsModel(Medicament medicament) {
        super.clear();
        List<Medicament> listMed = medDao.findAllMedicamentExlusiveAnalogs(medicament);
        for (Medicament amed : listMed) {
            super.addElement(amed);
        }
    }

}
