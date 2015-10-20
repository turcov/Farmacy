/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.dao;

import com.iucosoft.farmacy.exceptions.NoSuchMoneyException;
import com.iucosoft.farmacy.exceptions.StockException;
import com.iucosoft.farmacy.model.Invoice;
import com.iucosoft.farmacy.model.InvoiceDetail;
import java.util.List;

/**
 *
 * @author Turkov S
 */
public interface InvoiceDetailDaoIntf {

    int addInvoiceDetail(InvoiceDetail invDetail) throws StockException, NoSuchMoneyException;
    void updateInvoiceDetail(InvoiceDetail invDetail) throws StockException, NoSuchMoneyException;;
    void revomeInvoiceDetail(InvoiceDetail invDetail) throws StockException, NoSuchMoneyException;;
    void removeInvoiceDetailByInvoiceId(int idInvoice) throws StockException, NoSuchMoneyException;;

    InvoiceDetail findInvoiceDetailByIdIdInvoice(int id,int idInvoice);
    List<InvoiceDetail> findInvoiceDetailsByInvoice(Invoice invoice);
    List<InvoiceDetail> findInvoiceDetailByMedicament(int idMedicament);
    List<InvoiceDetail> findAllInvoiceDetails();

}
