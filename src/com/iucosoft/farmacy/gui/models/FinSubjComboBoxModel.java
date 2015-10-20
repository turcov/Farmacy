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
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author Turkov S
 */
public class FinSubjComboBoxModel extends DefaultComboBoxModel<FinanceSubject> {

    FinanceSubjectDaoIntf finSubjDao;
    FinanceSubject finSubj;

    public FinSubjComboBoxModel(Class clas) throws InstantiationException, IllegalAccessException, ConnectionInterruptedException {
        finSubj=(FinanceSubject)clas.newInstance();
        finSubj.setName("select "+clas.getSimpleName());
        finSubjDao = new FinanceSubjectDaoImpl(clas);
        refreshModel();
    }

    public void refreshModel() {
        super.removeAllElements();
        super.addElement(finSubj);
        List<FinanceSubject> finSubjList=finSubjDao.findAllFinSubects();
        for (FinanceSubject aFinanceSubject : finSubjList) {
            super.addElement(aFinanceSubject);
        }
    }

}
