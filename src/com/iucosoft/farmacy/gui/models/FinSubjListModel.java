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
import javax.swing.DefaultListModel;

/**
 *
 * @author iucosoft2javase2014
 */
public class FinSubjListModel extends DefaultListModel<FinanceSubject> {

    FinanceSubjectDaoIntf finSubjDao;
    FinanceSubject finSubj;

    public FinSubjListModel(Class clas) throws ConnectionInterruptedException, InstantiationException, IllegalAccessException  {
        finSubj = (FinanceSubject) clas.newInstance();
        finSubj.setName("All " + clas.getSimpleName() + 's');
        finSubjDao = new FinanceSubjectDaoImpl(clas);
        refreshModel();

    }

    public void refreshModel(FinanceSubject element, int index) {
        if (index > 0) {
            super.setElementAt(element, index);

        } else {
            super.removeElementAt(-index);
        }
    }

    public void refreshModel() {

        super.removeAllElements();
        super.addElement(finSubj);
        List<FinanceSubject> finSubjList = finSubjDao.findAllFinSubects();
        for (FinanceSubject aFinanceSubject : finSubjList) {
            super.addElement(aFinanceSubject);
        }

    }

}
