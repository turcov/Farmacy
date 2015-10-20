/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.gui.models;

import com.iucosoft.farmacy.dao.MedicamentDaoIntf;
import com.iucosoft.farmacy.dao.impl.MedicamentDaoImpl;
import com.iucosoft.farmacy.exceptions.ConnectionInterruptedException;
import com.iucosoft.farmacy.model.Medicament;
import java.util.List;
import javax.swing.DefaultListModel;


/**
 *
 * @author Serguei
 */
public class AnalogListModel extends DefaultListModel<Medicament>{

   MedicamentDaoIntf medDao;
    
    public AnalogListModel() throws ConnectionInterruptedException{
        medDao=new MedicamentDaoImpl();

    }
    
    public void refreshModel(Medicament medicament){
        super.removeAllElements();
       List<Medicament>listMed=medDao.findAllAnalogs(medicament);
        for (Medicament aMedicament : listMed) {
            super.addElement(aMedicament);
        }
    }
    
}
