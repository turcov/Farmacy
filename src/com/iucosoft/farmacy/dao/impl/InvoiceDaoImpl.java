/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.dao.impl;

import com.iucosoft.farmacy.dao.InvoiceDaoIntf;
import com.iucosoft.farmacy.dao.InvoiceDetailDaoIntf;
import com.iucosoft.farmacy.db.DataSourceFarmacy;
import com.iucosoft.farmacy.exceptions.ConnectionInterruptedException;
import com.iucosoft.farmacy.exceptions.NoSuchMoneyException;
import com.iucosoft.farmacy.exceptions.StockException;
import com.iucosoft.farmacy.model.FinanceSubject;
import com.iucosoft.farmacy.model.Invoice;
import static com.iucosoft.farmacy.utils.Util.getLastInsertedIdInTable;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Turkov S
 */
public class InvoiceDaoImpl implements InvoiceDaoIntf {

    private static final Logger LOG = Logger.getLogger(CategoryDaoImpl.class.getName());
    private DataSourceFarmacy dataSource;
    private Connection conn;
    private Invoice invoiceInstance;

    public InvoiceDaoImpl(Class clas) throws ConnectionInterruptedException, InstantiationException, IllegalAccessException {
        dataSource = DataSourceFarmacy.getInstance();
        //conn = dataSource.getConnection();
        invoiceInstance = (Invoice) clas.newInstance();
    }

    @Override
    public int addInvoice(Invoice invoice) {
        String sql = "INSERT INTO " + invoiceInstance.getTableName()
                + " VALUES (null,?,?,?)";
        int lastId = 0;
        PreparedStatement pstat = null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            if (invoice.getIdFinSubj() > 0) {
                pstat.setInt(1, invoice.getIdFinSubj());
            } else {
                pstat.setNull(1, Types.INTEGER);
            }
            pstat.setDate(2, invoice.getDateInvoice());
            pstat.setDouble(3, invoice.getTotalInvoice());
            pstat.executeUpdate();
            lastId = getLastInsertedIdInTable(conn);
            invoice.setIdInvoice(lastId);
            LOG.info("invoice " + invoice + " was succesfully added");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error while saving invoice " + invoice, ex);
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

    void updateInvoice(Invoice invoice, boolean toCloseConnection) {
        String sql = "UPDATE " + invoiceInstance.getTableName() + " SET "
                + invoiceInstance.getIdFinObjInTable() + "=?, "
                + invoiceInstance.getDateInvoiceInTable() + "=?, "
                + invoiceInstance.getTotalInvoiceInTable() + "=? WHERE "
                + invoiceInstance.getIdInvoiceInTable() + "=?";
        PreparedStatement pstat = null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            if (invoice.getIdFinSubj() > 0) {
                pstat.setInt(1, invoice.getIdFinSubj());
            } else {
                pstat.setNull(1, Types.INTEGER);
            }
            pstat.setDate(2, invoice.getDateInvoice());
            pstat.setDouble(3, invoice.getTotalInvoice());
            pstat.setInt(4, invoice.getIdInvoice());
            pstat.executeUpdate();
            LOG.info("invoice " + invoice + " was succesfully updated");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error while updating invoice " + invoice, ex);
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
    public void updateInvoice(Invoice invoice) {
        updateInvoice(invoice, true);
    }

    @Override
    public void deleteInvoice(Invoice invoice) throws ConnectionInterruptedException, StockException, NoSuchMoneyException {
        String sql = "DELETE FROM " + invoiceInstance.getTableName() + " WHERE "
                + invoiceInstance.getIdInvoiceInTable() + "=?";
        PreparedStatement pstat = null;
        try {
            InvoiceDetailDaoImpl invDao = new InvoiceDetailDaoImpl(invoiceInstance.getInvoiceDetailClass());
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            invDao.removeInvoiceDetailByInvoiceId(invoice.getIdInvoice(), false);
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, invoice.getIdInvoice());
            pstat.executeUpdate();
            pstat.close();
            conn.commit();
            LOG.info("invoice " + invoice + " was succesfully removed");
        } catch (StockException ex) {
            LOG.info("error while removing invoice " + invoice + " . " + ex.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex1) {
                LOG.log(Level.SEVERE, null, ex1);
            }
            throw new StockException(ex.getMessage());
        } catch (NoSuchMoneyException ex) {
            LOG.info("error while removing invoice " + invoice + " . " + ex.getMessage());
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
            LOG.log(Level.SEVERE, "error while removing invoice " + invoice, ex);
        } catch (InstantiationException | IllegalAccessException ex) {
            LOG.log(Level.SEVERE, null, ex);
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

    Invoice findInvoiceById(int idInvoice, boolean toCloseConnection) {
        String sql = "SELECT * from " + invoiceInstance.getTableName() + " WHERE "
                + invoiceInstance.getIdInvoiceInTable() + "=?";
        Invoice invoice = null;
        PreparedStatement pstat = null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, idInvoice);
            ResultSet rs = pstat.executeQuery();
            if (rs.next()) {
                invoice = invoiceInstance.getInvoice(idInvoice,
                        rs.getInt(invoiceInstance.getIdFinObjInTable()),
                        rs.getDate(invoiceInstance.getDateInvoiceInTable()),
                        rs.getDouble(invoiceInstance.getTotalInvoiceInTable()));
            }
            LOG.info("invoice succesfully selected by id");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error selecting invoice by id", ex);
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
        return invoice;

    }

    @Override
    public Invoice findInvoiceById(int idInvoice) {
        return findInvoiceById(idInvoice, true);
    }

    @Override
    public List<Invoice> findInvoicesByIdFinSubj(int idFinSubj) {
        String sql = "SELECT * from " + invoiceInstance.getTableName() + " WHERE "
                + invoiceInstance.getIdFinObjInTable() + "=?";
        Invoice invoice = null;
        List<Invoice> invoicesList = new ArrayList<>();
        PreparedStatement pstat = null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, idFinSubj);
            ResultSet rs = pstat.executeQuery();
            while (rs.next()) {
                invoice = invoiceInstance.getInvoice(rs.getInt(invoiceInstance.getIdInvoiceInTable()),
                        idFinSubj,
                        rs.getDate(invoiceInstance.getDateInvoiceInTable()),
                        rs.getDouble(invoiceInstance.getTotalInvoiceInTable()));
                invoicesList.add(invoice);
            }
            LOG.info("invoice succesfully selected by idFinSubj");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error selecting invoice by idFinSubj", ex);
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
        return invoicesList;

    }

    @Override
    public List<Invoice> findInvoicesByNamesFinSubject(String searchLine) {
        FinanceSubject finSubj = invoiceInstance.getNewFinanceSubject();
        String sql = "SELECT * from " + invoiceInstance.getTableName() + " WHERE " + invoiceInstance.getIdFinObjInTable() + " IN (SELECT "
                + finSubj.getIdInTable() + " FROM " + finSubj.getTableName() + " WHERE " + finSubj.getNameInTable() + " LIKE ?)";
        Invoice invoice = null;
        List<Invoice> invoicesList = new ArrayList<>();
        PreparedStatement pstat = null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setString(1, '%' + searchLine + '%');
            ResultSet rs = pstat.executeQuery();
            while (rs.next()) {
                invoice = invoiceInstance.getInvoice(rs.getInt(invoiceInstance.getIdInvoiceInTable()),
                        rs.getInt(invoiceInstance.getIdFinObjInTable()),
                        rs.getDate(invoiceInstance.getDateInvoiceInTable()),
                        rs.getDouble(invoiceInstance.getTotalInvoiceInTable()));
                invoicesList.add(invoice);
            }
            LOG.info("invoice succesfully selected by NameFinSubj");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error selecting invoice by NameFinSubj", ex);
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
        return invoicesList;

    }

    @Override
    public List<Invoice> findInvoicesByDate(Date dateInv1, Date dateInv2) {
        String sql = "SELECT * from " + invoiceInstance.getTableName() + " WHERE "
                + invoiceInstance.getDateInvoiceInTable() + " BETWEEN ? AND ?";
        Invoice invoice = null;
        List<Invoice> invoicesList = new ArrayList<>();
        PreparedStatement pstat = null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setDate(1, dateInv1);
            pstat.setDate(2, dateInv2);
            ResultSet rs = pstat.executeQuery();
            while (rs.next()) {
                invoice = invoiceInstance.getInvoice(rs.getInt(invoiceInstance.getIdInvoiceInTable()),
                        rs.getInt(invoiceInstance.getIdFinObjInTable()),
                        rs.getDate(invoiceInstance.getDateInvoiceInTable()),
                        rs.getDouble(invoiceInstance.getTotalInvoiceInTable()));
                invoicesList.add(invoice);
            }
            LOG.info("invoice succesfully selected by Date");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error selecting invoice by Date", ex);
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
        return invoicesList;
    }

    @Override
    public List<Invoice> findInvoicesBySumma(double summa1, double summa2) {
        String sql = "SELECT * from " + invoiceInstance.getTableName() + " WHERE "
                + invoiceInstance.getTotalInvoiceInTable() + " BETWEEN ? AND ?";
        Invoice invoice = null;
        List<Invoice> invoicesList = new ArrayList<>();
        PreparedStatement pstat = null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setDouble(1, summa1);
            pstat.setDouble(2, summa2);
            ResultSet rs = pstat.executeQuery();
            while (rs.next()) {
                invoice = invoiceInstance.getInvoice(rs.getInt(invoiceInstance.getIdInvoiceInTable()),
                        rs.getInt(invoiceInstance.getIdFinObjInTable()),
                        rs.getDate(invoiceInstance.getDateInvoiceInTable()),
                        rs.getDouble(invoiceInstance.getTotalInvoiceInTable()));
                invoicesList.add(invoice);
            }
            LOG.info("invoice succesfully selected by summa");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error selecting invoice by summa", ex);
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
        return invoicesList;
    }

    @Override
    public List<Invoice> findAllInvoices() {
        String sql = "SELECT * from " + invoiceInstance.getTableName();
        Invoice invoice = null;
        List<Invoice> invoicesList = new ArrayList<>();
        Statement stat = null;
        try {
            conn = dataSource.getConnection();
            stat = conn.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            while (rs.next()) {
                invoice = invoiceInstance.getInvoice(rs.getInt(invoiceInstance.getIdInvoiceInTable()),
                        rs.getInt(invoiceInstance.getIdFinObjInTable()),
                        rs.getDate(invoiceInstance.getDateInvoiceInTable()),
                        rs.getDouble(invoiceInstance.getTotalInvoiceInTable()));
                invoicesList.add(invoice);
            }
            LOG.info("invoice succesfully selected");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error selecting invoice", ex);
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
        return invoicesList;
    }

    private double getMaxSumInvoice(Integer idFinSubj, boolean toCloseConnection) {
        String sql = "SELECT DISTINCT MAX(" + invoiceInstance.getTotalInvoiceInTable()
                + ") from " + invoiceInstance.getTableName() + " WHERE "
                + invoiceInstance.getIdFinObjInTable() + " BETWEEN ? AND ?";
        double max = 0;
        PreparedStatement pstat = null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            if (idFinSubj != null) {
                pstat.setInt(1, idFinSubj);
                pstat.setInt(2, idFinSubj);
            } else {
                pstat.setInt(1, Integer.MIN_VALUE);
                pstat.setInt(2, Integer.MAX_VALUE);
            }
            ResultSet rs = pstat.executeQuery();
            if (rs.next()) {
                max = rs.getDouble("MAX(" + invoiceInstance.getTotalInvoiceInTable() + ")");
            }
            LOG.info("invoice succesfully selected");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error selecting invoice", ex);
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
        return max;

    }

    @Override
    public double getMaxSumInvoice(Integer idFinSubj) {
        return getMaxSumInvoice(idFinSubj, true);
    }

    @Override
    public double getSumOfInvoices(Integer idFinSubj, Date dateInv1, Date dateInv2, Double summa1, Double summa2) {
        String sql = "SELECT SUM(" + invoiceInstance.getTotalInvoiceInTable()
                + ") from " + invoiceInstance.getTableName() + " WHERE "
                + invoiceInstance.getIdFinObjInTable() + " BETWEEN ? AND ? AND "
                + invoiceInstance.getDateInvoiceInTable() + " BETWEEN ? AND ? AND "
                + invoiceInstance.getTotalInvoiceInTable() + " BETWEEN ? AND ?";
        double sum = 0.0;
        PreparedStatement pstat = null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            if (idFinSubj != null) {
                pstat.setInt(1, idFinSubj);
                pstat.setInt(2, idFinSubj);
            } else {
                pstat.setInt(1, 0);
                pstat.setInt(2, Integer.MAX_VALUE);
            }
            if (dateInv1 != null) {
                pstat.setDate(3, dateInv1);
            } else {
                pstat.setDate(3, getMinDate(idFinSubj, false));
            }
            if (dateInv2 != null) {
                pstat.setDate(4, dateInv2);
            } else {
                pstat.setDate(4, getMaxDate(idFinSubj, false));
            }
            if (summa1 != null) {
                pstat.setDouble(5, summa1);
            } else {
                pstat.setDouble(5, getMinSumInvoice(idFinSubj, false));
            }
            if (summa2 != null) {
                pstat.setDouble(6, summa2);
            } else {
                pstat.setDouble(6, getMaxSumInvoice(idFinSubj, false));
            }
            ResultSet rs = pstat.executeQuery();
            if (rs.next()) {
                sum = rs.getDouble("SUM(" + invoiceInstance.getTotalInvoiceInTable() + ")");
            }
            LOG.info("invoice succesfully selected");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error selecting invoice", ex);
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
        return sum;
    }

    private double getMinSumInvoice(Integer idFinSubj, boolean toCloseConnection) {
        String sql = "SELECT DISTINCT MIN(" + invoiceInstance.getTotalInvoiceInTable()
                + ") from " + invoiceInstance.getTableName() + " WHERE "
                + invoiceInstance.getIdFinObjInTable() + " BETWEEN ? AND ?";
        double min = 0;
        PreparedStatement pstat=null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            if (idFinSubj != null) {
                pstat.setInt(1, idFinSubj);
                pstat.setInt(2, idFinSubj);
            } else {
                pstat.setInt(1, Integer.MIN_VALUE);
                pstat.setInt(2, Integer.MAX_VALUE);
            }
            ResultSet rs = pstat.executeQuery();
            if (rs.next()) {
                min = rs.getDouble("MIN(" + invoiceInstance.getTotalInvoiceInTable() + ")");
            }
            LOG.info("invoice succesfully selected");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error selecting invoice", ex);
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
        return min;

    }

    @Override
    public double getMinSumInvoice(Integer idFinSubj) {
        return getMinSumInvoice(idFinSubj, true);
    }

    private Date getMinDate(Integer idFinSubj, boolean toCloseConnection) {
        String sql = "SELECT DISTINCT MIN(" + invoiceInstance.getDateInvoiceInTable()
                + ") from " + invoiceInstance.getTableName() + " WHERE "
                + invoiceInstance.getIdFinObjInTable() + " BETWEEN ? AND ?";
        Date minDate = null;
        PreparedStatement pstat=null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            if (idFinSubj != null) {
                pstat.setInt(1, idFinSubj);
                pstat.setInt(2, idFinSubj);
            } else {
                pstat.setInt(1, Integer.MIN_VALUE);
                pstat.setInt(2, Integer.MAX_VALUE);
            }
            ResultSet rs = pstat.executeQuery();
            if (rs.next()) {
                minDate = rs.getDate("MIN(" + invoiceInstance.getDateInvoiceInTable() + ")");
            }
            LOG.info("invoice succesfully selected");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error selecting invoice", ex);
        } finally {
            if (toCloseConnection && pstat != null) {
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
        return minDate;
    }

    @Override
    public Date getMinDate(Integer idFinSubj) {
        return getMinDate(idFinSubj, true);
    }

    private Date getMaxDate(Integer idFinSubj, boolean toCloseConnection) {
        String sql = "SELECT DISTINCT MAX(" + invoiceInstance.getDateInvoiceInTable()
                + ") from " + invoiceInstance.getTableName() + " WHERE "
                + invoiceInstance.getIdFinObjInTable() + " BETWEEN ? AND ?";
        Date maxDate = null;
        PreparedStatement pstat=null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            if (idFinSubj != null) {
                pstat.setInt(1, idFinSubj);
                pstat.setInt(2, idFinSubj);
            } else {
                pstat.setInt(1, Integer.MIN_VALUE);
                pstat.setInt(2, Integer.MAX_VALUE);
            }
            ResultSet rs = pstat.executeQuery();
            if (rs.next()) {
                maxDate = rs.getDate("MAX(" + invoiceInstance.getDateInvoiceInTable() + ")");
            }
            LOG.info("invoice succesfully selected");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error selecting invoice", ex);
        } finally {
            if (toCloseConnection && pstat != null) {
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
        return maxDate;
    }

    @Override
    public Date getMaxDate(Integer idFinSubj) {
        return getMaxDate(idFinSubj, true);
    }

    @Override
    public List<Invoice> findInvoicesByIdFinSubjDateSumma(Integer idFinSubj, Date dateInv1, Date dateInv2, Double summa1, Double summa2) {
        String sql = "SELECT * from " + invoiceInstance.getTableName() + " WHERE "
                + invoiceInstance.getIdFinObjInTable() + " BETWEEN ? AND ? AND "
                + invoiceInstance.getDateInvoiceInTable() + " BETWEEN ? AND ? AND "
                + invoiceInstance.getTotalInvoiceInTable() + " BETWEEN ? AND ?";
        List<Invoice> invoicesList = new ArrayList<>();
        Invoice invoice;
        PreparedStatement pstat=null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            if (idFinSubj != null) {
                pstat.setInt(1, idFinSubj);
                pstat.setInt(2, idFinSubj);
            } else {
                pstat.setInt(1, 0);
                pstat.setInt(2, Integer.MAX_VALUE);
            }
            if (dateInv1 != null) {
                pstat.setDate(3, dateInv1);
            } else {
                pstat.setDate(3, getMinDate(idFinSubj, false));
            }
            if (dateInv2 != null) {
                pstat.setDate(4, dateInv2);
            } else {
                pstat.setDate(4, getMaxDate(idFinSubj, false));
            }
            if (summa1 != null) {
                pstat.setDouble(5, summa1);
            } else {
                pstat.setDouble(5, getMinSumInvoice(idFinSubj, false));
            }
            if (summa2 != null) {
                pstat.setDouble(6, summa2);
            } else {
                pstat.setDouble(6, getMaxSumInvoice(idFinSubj, false));
            }
            ResultSet rs = pstat.executeQuery();
            while (rs.next()) {
                invoice = invoiceInstance.getInvoice(rs.getInt(invoiceInstance.getIdInvoiceInTable()),
                        rs.getInt(invoiceInstance.getIdFinObjInTable()),
                        rs.getDate(invoiceInstance.getDateInvoiceInTable()),
                        rs.getDouble(invoiceInstance.getTotalInvoiceInTable()));
                invoicesList.add(invoice);
            }
            LOG.info("invoice succesfully selected by ....");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error selecting invoice by ....", ex);
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

        return invoicesList;
    }

}
