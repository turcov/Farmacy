/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.dao.impl;

import com.iucosoft.farmacy.dao.InvoiceDetailDaoIntf;
import com.iucosoft.farmacy.db.DataSourceFarmacy;
import com.iucosoft.farmacy.exceptions.ConnectionInterruptedException;
import com.iucosoft.farmacy.exceptions.NoSuchMoneyException;
import com.iucosoft.farmacy.exceptions.StockException;
import com.iucosoft.farmacy.model.Invoice;
import com.iucosoft.farmacy.model.InvoiceDetail;
import com.iucosoft.farmacy.model.Stock;
import static com.iucosoft.farmacy.utils.Util.getLastInsertedIdInTable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Turkov S
 */
public class InvoiceDetailDaoImpl implements InvoiceDetailDaoIntf {

    private static final Logger LOG = Logger.getLogger(InvoiceDetailDaoImpl.class.getName());
    private DataSourceFarmacy dataSource;
    private Connection conn;
    private InvoiceDetail invDetailInstance;
    private StockDaoImpl stockDao;
    private InvoiceDaoImpl invoiceDao;
    private FarmacyBalanceDaoImpl farmacyDao;

    public InvoiceDetailDaoImpl(Class clas) throws ConnectionInterruptedException, InstantiationException, IllegalAccessException {
        dataSource = DataSourceFarmacy.getInstance();
        //conn = dataSource.getConnection();
        invDetailInstance = (InvoiceDetail) clas.newInstance();
        stockDao = new StockDaoImpl();
        farmacyDao = new FarmacyBalanceDaoImpl();
        invoiceDao = new InvoiceDaoImpl(invDetailInstance.getInvoiceClass());
    }

    private int getMaxId(Connection conn, InvoiceDetail invoiceDetail) throws SQLException {
        int id = 1;
        String sql = "SELECT MAX(id) FROM " + invDetailInstance.getTableName() + " WHERE "
                + invDetailInstance.getIdInvoiceInTable() + "=?";
        PreparedStatement pstat = conn.prepareStatement(sql);
        pstat.setInt(1, invoiceDetail.getIdInvoice());
        ResultSet rs = pstat.executeQuery();
        if (rs.next()) {
            id = rs.getInt(1)+1;
        }
        return id;
    }

    @Override
    public int addInvoiceDetail(InvoiceDetail invDetail) throws StockException, NoSuchMoneyException {
        String sql = "INSERT INTO " + invDetailInstance.getTableName()
                + " VALUES (?,?,?,?,?,?)";
        int lastId = 1;
        PreparedStatement pstat=null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            farmacyDao.setFarmacyBalance(
                    farmacyDao.getFarmacyBalance(false)
                    + invDetail.getTotal() * invDetail.coefQuant(), false);
            Stock stock = stockDao.findByIdStock(
                    invDetail.getIdMedicament(), false);
            stock.setBalance(
                    stock.getBalance()
                    - invDetail.getQuantity() * invDetail.coefQuant());
            stockDao.updateStock(stock, false);
            Invoice invoice = invoiceDao.findInvoiceById(
                    invDetail.getIdInvoice(), false);
            invoice.setTotalInvoice(invoice.getTotalInvoice()
                    + invDetail.getTotal());
            invoiceDao.updateInvoice(invoice, false);
            pstat = conn.prepareStatement(sql);
            lastId = getMaxId(conn, invDetail);
            pstat.setInt(1, lastId);
            pstat.setInt(2, invDetail.getIdInvoice());
            pstat.setInt(3, invDetail.getIdMedicament());
            pstat.setDouble(4, invDetail.getQuantity());
            pstat.setDouble(5, invDetail.getPrice());
            pstat.setDouble(6, invDetail.getTotal());
            pstat.executeUpdate();
            conn.commit();
            LOG.info("invDetail " + invDetail + " was succesfully added");
        } catch (StockException ex) {
            LOG.info("error while saving invDetail " + invDetail + " . " + ex.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex1) {
                LOG.log(Level.SEVERE, null, ex1);
            }
            throw new StockException(ex.getMessage());
        } catch (NoSuchMoneyException ex) {
            LOG.info("error while saving invDetail " + invDetail + " . " + ex.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex1) {
                LOG.log(Level.SEVERE, null, ex1);
            }
            throw new NoSuchMoneyException(ex.getMessage());
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex1) {
                LOG.log(Level.SEVERE, null, ex1);
            }
            LOG.info("error while saving invDetail " + invDetail + " , code Error is " + ex.getErrorCode());
        } finally {
            if (pstat != null) {
                try {
                    pstat.close();
                } catch (SQLException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
        }
        return lastId;
    }

    @Override
    public void updateInvoiceDetail(InvoiceDetail invDetail) throws NoSuchMoneyException, StockException {
        String sql = "UPDATE " + invDetailInstance.getTableName() + " SET idMedicament = ?, "
                + "quantity=?, " + invDetailInstance.getPriceInTable() + "=?,"
                + "total=? WHERE id=? AND " + invDetailInstance.getIdInvoiceInTable() + "=?";
        PreparedStatement pstat=null;
        try {
            InvoiceDetail oldDetail
                    = findInvoiceDetailByIdIdInvoice(invDetail.getId(), invDetail.getIdInvoice());
            conn = dataSource.getConnection();   
            conn.setAutoCommit(false);
            farmacyDao.setFarmacyBalance(farmacyDao.getFarmacyBalance(false)
                    - oldDetail.getTotal() * oldDetail.coefQuant()
                    + invDetail.getTotal() * invDetail.coefQuant(), false);
            Stock oldStock = stockDao.findByIdStock(
                    oldDetail.getIdMedicament(), false);
            oldStock.setBalance(
                    oldStock.getBalance()
                    + oldDetail.getQuantity() * oldDetail.coefQuant());
            stockDao.updateStock(oldStock, false);
            Stock newStock = stockDao.findByIdStock(
                    invDetail.getIdMedicament(), false);
            newStock.setBalance(
                    newStock.getBalance()
                    - invDetail.getQuantity() * invDetail.coefQuant());
            stockDao.updateStock(newStock, false);
            Invoice invoice = invoiceDao.findInvoiceById(
                    invDetail.getIdInvoice(), false);
            invoice.setTotalInvoice(invoice.getTotalInvoice()
                    - oldDetail.getTotal()
                    + invDetail.getTotal());
            invoiceDao.updateInvoice(invoice, false);
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, invDetail.getIdMedicament());
            pstat.setDouble(2, invDetail.getQuantity());
            pstat.setDouble(3, invDetail.getPrice());
            pstat.setDouble(4, invDetail.getTotal());
            pstat.setInt(5, invDetail.getId());
            pstat.setInt(6, invDetail.getIdInvoice());
            pstat.executeUpdate();
            conn.commit();
            LOG.info("invDetail " + invDetail + " was succesfully updated");
        } catch (SQLException ex) {
            LOG.info("error while updating invoice " + invDetail + ", code Error is " + ex.getErrorCode());
        } catch (NoSuchMoneyException ex) {
            LOG.info("error while updating invDetail " + invDetail + ". " + ex.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex1) {
                LOG.log(Level.SEVERE, null, ex1);
            }
            throw new NoSuchMoneyException(ex.getMessage());
        } catch (StockException ex) {
            LOG.info("error while updating invDetail " + invDetail + ". " + ex.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex1) {
                LOG.log(Level.SEVERE, null, ex1);
            }
            throw new StockException(ex.getMessage());
        } finally {
            if (pstat != null) {
                try {
                    pstat.close();
                } catch (SQLException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @Override
    public void revomeInvoiceDetail(InvoiceDetail invDetail) throws StockException, NoSuchMoneyException {
        String sql = "DELETE FROM " + invDetailInstance.getTableName()
                + " WHERE id=? AND " + invDetailInstance.getIdInvoiceInTable() + "=?";
        PreparedStatement pstat=null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            farmacyDao.setFarmacyBalance(
                    farmacyDao.getFarmacyBalance(false)
                    - invDetail.getTotal() * invDetail.coefQuant(), false);
            Stock stock = stockDao.findByIdStock(
                    invDetail.getIdMedicament(), false);
            stock.setBalance(
                    stock.getBalance()
                    + invDetail.getQuantity() * invDetail.coefQuant());
            stockDao.updateStock(stock, false);

            Invoice invoice = invoiceDao.findInvoiceById(
                    invDetail.getIdInvoice(), false);
            invoice.setTotalInvoice(invoice.getTotalInvoice()
                    - invDetail.getTotal());
            invoiceDao.updateInvoice(invoice, false);

            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, invDetail.getId());
            pstat.setInt(2, invDetail.getIdInvoice());
            pstat.executeUpdate();

            conn.commit();
            LOG.info("invDetail " + invDetail + " was succesfully removed");
        } catch (StockException ex) {
            LOG.info("error while removing invDetail " + invDetail + " . " + ex.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex1) {
                LOG.log(Level.SEVERE, null, ex1);
            }
            throw new StockException(ex.getMessage());
        } catch (NoSuchMoneyException ex) {
            LOG.info("error while removing invDetail " + invDetail + " . " + ex.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex1) {
                LOG.log(Level.SEVERE, null, ex1);
            }
            throw new NoSuchMoneyException(ex.getMessage());
        } catch (SQLException ex) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex1) {
                LOG.log(Level.SEVERE, null, ex1);
            }
            LOG.info("error while removing invDetail " + invDetail + ", code Error is " + ex.getErrorCode());
        } finally {
            if (pstat != null) {
                try {
                    pstat.close();
                } catch (SQLException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    void removeInvoiceDetailByInvoiceId(int idInvoice, boolean toCloseConnection) throws StockException, NoSuchMoneyException {
        String sqlStock = "UPDATE stock_medicaments AS st SET balance=balance+"
                + "(SELECT SUM(quantity)*(" + invDetailInstance.coefQuant()
                + ") FROM " + invDetailInstance.getTableName()
                + " AS inv WHERE inv.idMedicament=st.idMedicament AND "
                + invDetailInstance.getIdInvoiceInTable() + "=?)"
                + "WHERE st.idMedicament IN (SELECT DISTINCT inv.idMedicament FROM "
                + invDetailInstance.getTableName() + " AS inv  "
                + "WHERE inv.idMedicament=st.idMedicament AND "
                + invDetailInstance.getIdInvoiceInTable() + "=?)";
        String sqlRemoving = "DELETE FROM " + invDetailInstance.getTableName()
                + " WHERE " + invDetailInstance.getIdInvoiceInTable() + "=?";
        PreparedStatement pstat=null;
        try {
            conn = dataSource.getConnection();
            if (toCloseConnection) {
                conn.setAutoCommit(false);
            }
            double sumInvoice = invoiceDao.findInvoiceById(idInvoice, false).getTotalInvoice();
            farmacyDao.setFarmacyBalance(farmacyDao.getFarmacyBalance(false)
                    - invDetailInstance.coefQuant() * sumInvoice, false);
            pstat = conn.prepareStatement(sqlStock);
            pstat.setInt(1, idInvoice);
            pstat.setInt(2, idInvoice);
            pstat.executeUpdate();
            pstat = conn.prepareStatement(sqlRemoving);
            pstat.setInt(1, idInvoice);
            pstat.executeUpdate();
            LOG.info("invDetails was succesfully removed by idInvoice=" + idInvoice);
        } catch (NoSuchMoneyException ex) {
            if (toCloseConnection) {
                LOG.info("error while removing by invoiceId. " + ex.getMessage());
                try {
                    if (conn != null) {
                        conn.rollback();
                    }
                } catch (SQLException ex1) {
                    LOG.log(Level.SEVERE, null, ex1);
                }
            }
            throw new NoSuchMoneyException(ex.getMessage());
        } catch (SQLException ex) {
            if (toCloseConnection) {
                LOG.log(Level.SEVERE, "error while removing by invoiceId " + idInvoice, ex);
                try {
                    if (conn != null) {
                        conn.rollback();
                    }
                } catch (SQLException ex1) {
                    LOG.log(Level.SEVERE, null, ex1);
                }
            }
            if (ex.getErrorCode() == 1264) {
                LOG.log(Level.SEVERE, "error while updating stock.Stock can't be negative");
                throw new StockException("Can't delete.This operation avoid negative stock");
            }
        } finally {
            if (pstat != null) {
                try {
                    pstat.close();
                } catch (SQLException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
            if (toCloseConnection) {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    @Override
    public void removeInvoiceDetailByInvoiceId(int idInvoice) throws StockException, NoSuchMoneyException {
        removeInvoiceDetailByInvoiceId(idInvoice, true);
    }

    @Override
    public InvoiceDetail findInvoiceDetailByIdIdInvoice(int id, int idInvoice) {
        String sql = "SELECT * from " + invDetailInstance.getTableName()
                + " WHERE id=? AND " + invDetailInstance.getIdInvoiceInTable() + "=?";
        InvoiceDetail invoiceDetail = null;
        PreparedStatement pstat=null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, id);
            pstat.setInt(2, idInvoice);
            ResultSet rs = pstat.executeQuery();
            if (rs.next()) {
                invoiceDetail = invDetailInstance.getInvoiceDetail(id, idInvoice,
                        rs.getInt("idMedicament"), rs.getDouble("quantity"),
                        rs.getDouble(invDetailInstance.getPriceInTable()),
                        rs.getDouble("total"));
            }
            LOG.info("invoiceDetail succesfully selected by id");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error selecting invoiceDetail by id", ex);
        } finally {
            if (pstat != null) {
                try {
                    pstat.close();
                } catch (SQLException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
        }
        return invoiceDetail;

    }

    @Override
    public List<InvoiceDetail> findInvoiceDetailsByInvoice(Invoice invoice) {
        String sql = "SELECT * from " + invDetailInstance.getTableName()
                + " WHERE " + invDetailInstance.getIdInvoiceInTable() + "=?";
        InvoiceDetail invoiceDetail = null;
        List<InvoiceDetail> listInvoiceDetails = new ArrayList<>();
        PreparedStatement pstat=null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, invoice.getIdInvoice());
            ResultSet rs = pstat.executeQuery();
            while (rs.next()) {
                invoiceDetail = invDetailInstance.getInvoiceDetail(
                        rs.getInt("id"), invoice.getIdInvoice(),
                        rs.getInt("idMedicament"), rs.getDouble("quantity"),
                        rs.getDouble(invDetailInstance.getPriceInTable()),
                        rs.getDouble("total"));
                listInvoiceDetails.add(invoiceDetail);
            }
            LOG.info("invoiceDetail succesfully selected by Invoice");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error selecting invoiceDetail by Invoice", ex);
        } finally {
            if (pstat != null) {
                try {
                    pstat.close();
                } catch (SQLException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
        }
        return listInvoiceDetails;

    }

    @Override
    public List<InvoiceDetail> findInvoiceDetailByMedicament(int idMedicament) {
        String sql = "SELECT * from " + invDetailInstance.getTableName()
                + " WHERE idMedicament=?";
        InvoiceDetail invoiceDetail = null;
        List<InvoiceDetail> listInvoiceDetails = new ArrayList<>();
        PreparedStatement pstat=null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, idMedicament);
            ResultSet rs = pstat.executeQuery();
            while (rs.next()) {
                invoiceDetail = invDetailInstance.getInvoiceDetail(
                        rs.getInt("id"), rs.getInt(invDetailInstance.getIdInvoiceInTable()),
                        idMedicament, rs.getDouble("quantity"),
                        rs.getDouble(invDetailInstance.getPriceInTable()),
                        rs.getDouble("total"));
                listInvoiceDetails.add(invoiceDetail);
            }
            LOG.info("invoiceDetail succesfully selected by Medicament");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error selecting invoiceDetail by Medicament", ex);
        } finally {
            if (pstat != null) {
                try {
                    pstat.close();
                } catch (SQLException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
        }
        return listInvoiceDetails;
    }

    @Override
    public List<InvoiceDetail> findAllInvoiceDetails() {
        String sql = "SELECT * from " + invDetailInstance.getTableName();
        InvoiceDetail invoiceDetail = null;
        List<InvoiceDetail> listInvoiceDetails = new ArrayList<>();
        Statement stat=null;
        try {
            conn = dataSource.getConnection();
            stat = conn.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            while (rs.next()) {
                invoiceDetail = invDetailInstance.getInvoiceDetail(
                        rs.getInt("id"), rs.getInt(invDetailInstance.getIdInvoiceInTable()),
                        rs.getInt("idMedicament"), rs.getDouble("quantity"),
                        rs.getDouble(invDetailInstance.getPriceInTable()),
                        rs.getDouble("total"));
                listInvoiceDetails.add(invoiceDetail);
            }
            LOG.info("invoiceDetail succesfully selected");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error selecting invoiceDetail", ex);
        } finally {
            if (stat != null) {
                try {
                    stat.close();
                } catch (SQLException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
        }
        return listInvoiceDetails;
    }

}
