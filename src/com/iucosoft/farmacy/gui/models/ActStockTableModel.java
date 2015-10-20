/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.gui.models;

import com.iucosoft.farmacy.dao.FinanceSubjectDaoIntf;
import com.iucosoft.farmacy.dao.InvoiceDaoIntf;
import com.iucosoft.farmacy.dao.InvoiceDetailDaoIntf;
import com.iucosoft.farmacy.dao.impl.FinanceSubjectDaoImpl;
import com.iucosoft.farmacy.dao.impl.InvoiceDaoImpl;
import com.iucosoft.farmacy.dao.impl.InvoiceDetailDaoImpl;
import com.iucosoft.farmacy.model.Client;
import com.iucosoft.farmacy.model.FinanceSubject;
import com.iucosoft.farmacy.model.Invoice;
import com.iucosoft.farmacy.model.InvoiceDetail;
import com.iucosoft.farmacy.model.InvoiceDetailPurchase;
import com.iucosoft.farmacy.model.InvoiceDetailSale;
import com.iucosoft.farmacy.model.InvoicePurchase;
import com.iucosoft.farmacy.model.InvoiceSale;
import com.iucosoft.farmacy.model.Supplier;
import com.iucosoft.farmacy.utils.Util;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Turkov S
 */
public class ActStockTableModel extends DefaultTableModel {

    private class Act implements Comparable {

        private Date date;
        private int invNo;
        private String finSubject;
        private double quantity;

        public Act() {
        }

        public Act(Date date, int invNo, String finSubject, double quantity) {
            this.date = date;
            this.invNo = invNo;
            this.finSubject = finSubject;
            this.quantity = quantity;
        }

        public double getQuantity() {
            return quantity;
        }

        public void setQuantity(double quantity) {
            this.quantity = quantity;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public String getFinSubject() {
            return finSubject;
        }

        public void setFinSubject(String finSubject) {
            this.finSubject = finSubject;
        }

        @Override
        public int compareTo(Object t) {
            return this.date.compareTo(((Act) t).getDate());
        }

        public int getInvNo() {
            return invNo;
        }

        public void setInvNo(int invNo) {
            this.invNo = invNo;
        }

    }

    public ActStockTableModel(Integer idMedicament) {
        setColumnNames();
        refreshModel(idMedicament);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    private List<Act> createListAct(int idMedicament) {
        List<Act> listAct = new ArrayList<>();
        try {
            InvoiceDetailDaoIntf invDetailDao = new InvoiceDetailDaoImpl(InvoiceDetailPurchase.class);
            List<InvoiceDetail> invDetails = invDetailDao.findInvoiceDetailByMedicament(idMedicament);
            InvoiceDaoIntf invDao = new InvoiceDaoImpl(InvoicePurchase.class);
            Invoice invoice;
            FinanceSubjectDaoIntf finSubjDao = new FinanceSubjectDaoImpl(Supplier.class);
            FinanceSubject finSubj;
            for (InvoiceDetail invDetail : invDetails) {
                invoice = invDao.findInvoiceById(invDetail.getIdInvoice());
                finSubj = finSubjDao.findFinSubjectById(invoice.getIdFinSubj());
                listAct.add(
                        new Act(invoice.getDateInvoice(), 
                                invDetail.getIdInvoice(), 
                                finSubj.getName(),
                                invDetail.getQuantity()));
            }
            invDetailDao = new InvoiceDetailDaoImpl(InvoiceDetailSale.class);
            invDetails = invDetailDao.findInvoiceDetailByMedicament(idMedicament);
            invDao = new InvoiceDaoImpl(InvoiceSale.class);
            finSubjDao = new FinanceSubjectDaoImpl(Client.class);
            for (InvoiceDetail invDetail : invDetails) {
                invoice = invDao.findInvoiceById(invDetail.getIdInvoice());
                finSubj = finSubjDao.findFinSubjectById(invoice.getIdFinSubj());
                listAct.add(new Act(invoice.getDateInvoice(), invDetail.getIdInvoice(), finSubj.getName(), -invDetail.getQuantity()));
            }
            Collections.sort(listAct);
        } catch (Exception ex) {
            Logger.getLogger(ActStockTableModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listAct;
    }

    private void setColumnNames() {
        super.addColumn("Date");
        super.addColumn("InvNo");
        super.addColumn("Supplier");
        super.addColumn("Quantity");
//        super.addColumn("Nr.inv");
        super.addColumn("Client");
        super.addColumn("Quantity");
        super.addColumn("Saldo");
    }

    public void refreshModel(Integer idMedicament) {
        Util.clearModel(this);
        if (idMedicament == null) {
            return;
        }
        List<Act> listAct = createListAct(idMedicament);
        Vector vector;
        double saldo = 0;
        for (Act act : listAct) {
            vector = new Vector();
            if (act.quantity != 0) {
                vector.add(act.getDate());
                vector.add(act.getInvNo());
                if (act.quantity > 0) {
                    vector.add(act.getFinSubject());
                    vector.add(act.getQuantity());
                    vector.add(null);
                    vector.add(null);
                } else {
                    vector.add(null);
                    vector.add(null);
                    vector.add(act.getFinSubject());
                    vector.add(-act.getQuantity());
                }
                vector.add(saldo += act.quantity);
            }
            super.addRow(vector);
        }
    }

}
