/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.dao;

import com.iucosoft.farmacy.exceptions.ConnectionInterruptedException;
import com.iucosoft.farmacy.exceptions.NoSuchMoneyException;
import com.iucosoft.farmacy.exceptions.StockException;
import com.iucosoft.farmacy.model.Invoice;
import java.sql.Date;
import java.util.List;


/**
 *
 * @author Turkov S
 */
public interface InvoiceDaoIntf {
    int addInvoice(Invoice invoice);
    void updateInvoice(Invoice invoice);
    void deleteInvoice(Invoice invoice)throws ConnectionInterruptedException,StockException,NoSuchMoneyException;
    Invoice findInvoiceById(int idInvoice);
    List<Invoice> findInvoicesByIdFinSubj(int idFinSubj);
    List<Invoice> findInvoicesByNamesFinSubject(String searchLine);
    List<Invoice> findInvoicesByDate(Date dateInv1,Date dateInv2);
    List<Invoice> findInvoicesBySumma(double summa1,double summa2);
    List<Invoice> findInvoicesByIdFinSubjDateSumma(Integer idFinSubj,Date dateInv1,Date dateInv2,Double summa1,Double summa2);
    List<Invoice> findAllInvoices();
    double getSumOfInvoices(Integer idFinSubj,Date dateInv1,Date dateInv2,Double summa1,Double summa2);
    double getMinSumInvoice(Integer idFinSubj);//
    double getMaxSumInvoice(Integer idFinSubj);//
    Date getMinDate(Integer idFinSubj);//
    Date getMaxDate(Integer idFinSubj);//
            
 
}
