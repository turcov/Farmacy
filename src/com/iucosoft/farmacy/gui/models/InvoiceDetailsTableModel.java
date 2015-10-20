/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.gui.models;

import com.iucosoft.farmacy.dao.InvoiceDetailDaoIntf;
import com.iucosoft.farmacy.dao.MedicamentDaoIntf;
import com.iucosoft.farmacy.dao.impl.InvoiceDetailDaoImpl;
import com.iucosoft.farmacy.dao.impl.MedicamentDaoImpl;
import com.iucosoft.farmacy.exceptions.ConnectionInterruptedException;
import com.iucosoft.farmacy.model.Invoice;
import com.iucosoft.farmacy.model.InvoiceDetail;
import com.iucosoft.farmacy.utils.Util;
import java.util.List;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Turkov S
 */
public class InvoiceDetailsTableModel extends DefaultTableModel {

    String[] colummns = {"ID", "Medicament", "Quantity", "Price", "Sum"};

    InvoiceDetailDaoIntf invoiceDetailDao;
    Invoice invoice;
    MedicamentDaoIntf medDao;

    public InvoiceDetailsTableModel(Invoice invoice) throws ConnectionInterruptedException, InstantiationException, IllegalAccessException {
        this.invoice = invoice;
        invoiceDetailDao = new InvoiceDetailDaoImpl(invoice.getInvoiceDetailClass());
        medDao = new MedicamentDaoImpl();
        setColumns();
        refreshModel();
    }

    
    @Override
    public boolean isCellEditable(int row, int column) {
        return false; 
    }
    
    private void setColumns() {
        for (String acolumn : colummns) {
            super.addColumn(acolumn);
        }
    }

    public void refreshModel(int row, InvoiceDetail invoiceDetail) {
        if (row >=0) {
            super.setValueAt(invoiceDetail.getId(), row, 0);
            super.setValueAt(medDao.findByIdMedicament(invoiceDetail.getIdMedicament()).getNameMedicament(), row, 1);
            super.setValueAt(invoiceDetail.getQuantity(), row, 2);
            super.setValueAt(invoiceDetail.getPrice()+" MDL", row, 3);
            super.setValueAt(Util.roundTo(invoiceDetail.getTotal())+" MDL", row, 4);
        } else {
            super.removeRow(Math.abs(row) - 1);
        }
    }

    public void refreshModel() {
        List<InvoiceDetail> invoiceDetailsList = invoiceDetailDao.findInvoiceDetailsByInvoice(invoice);
        Util.clearModel(this);
        Vector vector;
        for (InvoiceDetail aDetail : invoiceDetailsList) {
            vector = new Vector();
            vector.add(aDetail.getId());
            vector.add(medDao.findByIdMedicament(aDetail.getIdMedicament()).getNameMedicament());
            vector.add(aDetail.getQuantity());
            vector.add(aDetail.getPrice()+" MDL");
            vector.add(Util.roundTo(aDetail.getTotal())+" MDL");
            super.addRow(vector);
        }
    }

    public void refreshModel(InvoiceDetail invoiceDetail) {
        Vector vector = new Vector();
        vector.add(invoiceDetail.getId());
        vector.add(medDao.findByIdMedicament(invoiceDetail.getIdMedicament()).getNameMedicament());
        vector.add(invoiceDetail.getQuantity());
        vector.add(invoiceDetail.getPrice()+" MDL");
        vector.add(Util.roundTo(invoiceDetail.getTotal())+" MDL");
        super.addRow(vector);
    }
}
