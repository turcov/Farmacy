/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.gui.models;

import com.iucosoft.farmacy.dao.FinanceSubjectDaoIntf;
import com.iucosoft.farmacy.dao.InvoiceDaoIntf;
import com.iucosoft.farmacy.dao.impl.FinanceSubjectDaoImpl;
import com.iucosoft.farmacy.dao.impl.InvoiceDaoImpl;
import com.iucosoft.farmacy.exceptions.ConnectionInterruptedException;
import com.iucosoft.farmacy.model.FinanceSubject;
import com.iucosoft.farmacy.model.Invoice;
import com.iucosoft.farmacy.utils.Util;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Vector;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Turkov S
 */
public class InvoiceTableModel extends DefaultTableModel {

    InvoiceDaoIntf invoiceDao;
    FinanceSubjectDaoIntf finSubjDao;
    String[] columnNames = new String[4];
    FinanceSubject finSubj;

    public InvoiceTableModel(Class clas) throws ConnectionInterruptedException, InstantiationException, IllegalAccessException {
        invoiceDao = new InvoiceDaoImpl(clas);
        finSubj = ((Invoice) clas.newInstance()).getNewFinanceSubject();
        finSubjDao = new FinanceSubjectDaoImpl(finSubj.getClass());
        setColumns();
        refreshModel();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    private void setColumns() {
        columnNames[0] = "ID";
        columnNames[1] = finSubj.getClass().getSimpleName();
        columnNames[2] = "Date of invoice";
        columnNames[3] = "Sum of invoice";
        for (String aColumn : columnNames) {
            super.addColumn(aColumn);
        }
    }

    public void refreshModel() {
        List<Invoice> invoicesList = invoiceDao.findAllInvoices();
        refreshModel(invoicesList);
    }

    public void refreshModel(int row, Invoice invoice) {
        FinanceSubject fSubj;
        if (row >= 0) {
            super.setValueAt(invoice.getIdInvoice(), row, 0);
            fSubj = finSubjDao.findFinSubjectById(invoice.getIdFinSubj());
            super.setValueAt(fSubj.getName(), row, 1);
            super.setValueAt(invoice.getDateInvoice(), row, 2);
            super.setValueAt(Util.roundTo(invoice.getTotalInvoice()) + " MDL", row, 3);
        } else {
            super.removeRow(Math.abs(row) - 1);
        }

    }

    public void refreshModel(Invoice invoice) {
        FinanceSubject fSubj;
        Vector vector = new Vector();
        vector.add(invoice.getIdInvoice());
        fSubj = finSubjDao.findFinSubjectById(invoice.getIdFinSubj());
        if (fSubj != null) {
            vector.add(fSubj.getName());
        } else {
            vector.add("have no subject");
        }
        vector.add(invoice.getDateInvoice());
        vector.add(Util.roundTo(invoice.getTotalInvoice()) + " MDL");
        super.addRow(vector);
    }

    public void refreshModel(List<Invoice> invoicesList) {
        Vector vector;
        FinanceSubject fSubj;
        Util.clearModel(this);
        for (Invoice aInvoice : invoicesList) {
            vector = new Vector();
            vector.add(aInvoice.getIdInvoice());
            fSubj = finSubjDao.findFinSubjectById(aInvoice.getIdFinSubj());
            if (fSubj != null) {
                vector.add(fSubj.getName());
            } else {
                vector.add("have no subject");
            }
            vector.add(aInvoice.getDateInvoice());
            vector.add(Util.roundTo(aInvoice.getTotalInvoice()) + " MDL");
            super.addRow(vector);
        }

    }

}
