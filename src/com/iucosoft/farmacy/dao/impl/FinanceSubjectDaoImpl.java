/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.dao.impl;

import com.iucosoft.farmacy.dao.FinanceSubjectDaoIntf;
import com.iucosoft.farmacy.dao.InvoiceDaoIntf;
import com.iucosoft.farmacy.db.DataSourceFarmacy;
import com.iucosoft.farmacy.exceptions.ConnectionInterruptedException;
import com.iucosoft.farmacy.exceptions.DeletingException;
import static com.iucosoft.farmacy.utils.Util.getLastInsertedIdInTable;
import com.iucosoft.farmacy.model.FinanceSubject;
import com.iucosoft.farmacy.model.Invoice;
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
 *
 */
public class FinanceSubjectDaoImpl implements FinanceSubjectDaoIntf {

    private static final Logger LOG = Logger.getLogger(CategoryDaoImpl.class.getName());
    private DataSourceFarmacy dataSource;
    private Connection conn;
    private FinanceSubject financeSubject;

    public FinanceSubjectDaoImpl(Class clas) throws ConnectionInterruptedException, InstantiationException, IllegalAccessException {
        dataSource = DataSourceFarmacy.getInstance();
        //conn = dataSource.getConnection();
        financeSubject = (FinanceSubject) clas.newInstance();
    }

    @Override
    public int addFinSubject(FinanceSubject finSubj) {
        String sql = "INSERT INTO " + financeSubject.getTableName()
                + " VALUES (null,?,?)";
        int lastId = 0;
        PreparedStatement pstat = null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setString(1, finSubj.getName());
            pstat.setString(2, finSubj.getAccount());
            pstat.executeUpdate();
            lastId = getLastInsertedIdInTable(conn);
            LOG.info("finSubj " + finSubj + " was succesfully added");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error while saving finSubj " + finSubj, ex);
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
    public void updateFinSubject(FinanceSubject finSubj) {
        String sql = "UPDATE " + financeSubject.getTableName() + " SET "
                + financeSubject.getNameInTable() + "=?, "
                + financeSubject.getAccountInTable() + "=? WHERE "
                + financeSubject.getIdInTable() + "=?";
        PreparedStatement pstat = null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setString(1, finSubj.getName());
            pstat.setString(2, finSubj.getAccount());
            pstat.setInt(3, finSubj.getId());
            pstat.executeUpdate();
            LOG.info("finSubj " + finSubj + " was succesfully updated");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error while updating finSubj " + finSubj, ex);
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
    public void deleteFinSubject(FinanceSubject finSubj) throws DeletingException {
        String sql = "DELETE from " + financeSubject.getTableName() + " WHERE "
                + financeSubject.getIdInTable() + "=?";
        PreparedStatement pstat = null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, finSubj.getId());
            pstat.executeUpdate();
            LOG.info("finSubj " + finSubj + " was succesfully deleted");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error while deleting finSubj " + finSubj + ". errorcode=" + ex.getErrorCode(), ex);
            if (ex.getErrorCode() == 1451) {
                throw new DeletingException("Unable to remove "
                        + finSubj.getClass().getSimpleName() + " \""
                        + finSubj.getName() + "\" from database.\n"
                        + "It has been used in invoices.\n"
                        + "First remove the related invoices");
            }
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
    public FinanceSubject findFinSubjectById(int id) {
        String sql = "SELECT * from " + financeSubject.getTableName() + " WHERE "
                + financeSubject.getIdInTable() + "=?";
        FinanceSubject finSubj = null;
        PreparedStatement pstat = null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, id);
            ResultSet rs = pstat.executeQuery();
            if (rs.next()) {
                finSubj = financeSubject.getFinanceSubject(id,
                        rs.getString(financeSubject.getNameInTable()),
                        rs.getString(financeSubject.getAccountInTable()));
            }
            LOG.info("finSubj succesfully selected by id");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error selecting finSubj by id", ex);
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
        return finSubj;
    }

    @Override
    public List<FinanceSubject> findFinSubjectByName(String searchLine) {
        String sql = "SELECT * from " + financeSubject.getTableName() + " WHERE "
                + financeSubject.getNameInTable() + " LIKE ?";
        FinanceSubject finSubj = null;
        List<FinanceSubject> finSubjList = new ArrayList<>();
        PreparedStatement pstat = null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setString(1, '%' + searchLine + '%');
            ResultSet rs = pstat.executeQuery();
            while (rs.next()) {
                finSubj = financeSubject.getFinanceSubject(rs.getInt(financeSubject.getIdInTable()),
                        rs.getString(financeSubject.getNameInTable()),
                        rs.getString(financeSubject.getAccountInTable()));
                finSubjList.add(finSubj);
            }
            LOG.info("finSubj succesfully selected by name");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error selecting finSubj by name", ex);
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
        return finSubjList;
    }

    @Override
    public List<FinanceSubject> findFinSubjectByAccount(String searchLine) {
        String sql = "SELECT * from " + financeSubject.getTableName() + " WHERE "
                + financeSubject.getAccountInTable() + " LIKE ?";
        FinanceSubject finSubj = null;
        List<FinanceSubject> finSubjList = new ArrayList<>();
        PreparedStatement pstat = null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setString(1, '%' + searchLine + '%');
            ResultSet rs = pstat.executeQuery();
            while (rs.next()) {
                finSubj = financeSubject.getFinanceSubject(rs.getInt(financeSubject.getIdInTable()),
                        rs.getString(financeSubject.getNameInTable()),
                        rs.getString(financeSubject.getAccountInTable()));
                finSubjList.add(finSubj);
            }
            LOG.info("finSubj succesfully selected by account");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error selecting finSubj by account", ex);
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
        return finSubjList;
    }

    @Override
    public List<FinanceSubject> findAllFinSubects() {
        String sql = "SELECT * from " + financeSubject.getTableName();
        FinanceSubject finSubj = null;
        List<FinanceSubject> finSubjList = new ArrayList<>();
        Statement stat = null;
        try {
            conn = dataSource.getConnection();
            stat = conn.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            while (rs.next()) {

                finSubj = financeSubject.getFinanceSubject(rs.getInt(financeSubject.getIdInTable()),
                        rs.getString(financeSubject.getNameInTable()),
                        rs.getString(financeSubject.getAccountInTable()));
                finSubjList.add(finSubj);
            }
            LOG.info("finSubj succesfully selected");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error selecting finSubj", ex);
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
        return finSubjList;

    }

}
