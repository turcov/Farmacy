/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.dao.impl;

import com.iucosoft.farmacy.dao.FarmacyBalanceDaoIntf;
import com.iucosoft.farmacy.db.Config;
import com.iucosoft.farmacy.db.DataSourceFarmacy;
import com.iucosoft.farmacy.exceptions.ConnectionInterruptedException;
import com.iucosoft.farmacy.exceptions.NoSuchMoneyException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Turkov S
 */
public class FarmacyBalanceDaoImpl implements FarmacyBalanceDaoIntf {

    private static final Logger LOG = Logger.getLogger(CategoryDaoImpl.class.getName());
    private DataSourceFarmacy dataSource;
    private Connection conn;

    public FarmacyBalanceDaoImpl() throws ConnectionInterruptedException {
        dataSource = DataSourceFarmacy.getInstance();
        //conn = dataSource.getConnection();
    }

    double getFarmacyBalance(boolean toCloseConnection) {
        String sql = "SELECT * from farmacybalance WHERE idFarmacy=1";
        double rez = 0;
        Statement stat = null;
        try {
            conn = dataSource.getConnection();
            stat = conn.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            if (rs.next()) {
                rez = rs.getDouble("Balance");
            }
            LOG.info("selected succesfully");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error in sql", ex);
        } finally {
            if (stat != null) {
                try {
                    stat.close();
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
        return rez;
    }

    @Override
    public double getFarmacyBalance() {
        return getFarmacyBalance(true);
    }

    void setFarmacyBalance(double newBalance, boolean toCloseConnection) throws NoSuchMoneyException {
        String sql = "UPDATE farmacybalance SET balance=? WHERE idFarmacy=1";
        PreparedStatement pstat = null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setDouble(1, newBalance);
            pstat.executeUpdate();
            LOG.info("farmacy balance was susscefully updated");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error while updating farmacy balance, coderror=" + ex.getErrorCode());
            if (ex.getErrorCode() == 1264) {
                throw new NoSuchMoneyException("Not enough money on the balance");
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
    public void setFarmacyBalance(double newBalance) throws NoSuchMoneyException {
        setFarmacyBalance(newBalance, true);
    }

    @Override
    public String getAdminPassword() {
        String sql = "SELECT AdminPassword from farmacybalance WHERE idFarmacy=1";
        String password = null;
        Statement stat = null;
        try {
            conn = dataSource.getConnection();
            stat = conn.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            if (rs.next()) {
                password = rs.getString("AdminPassword");
            }
            LOG.info("selected succesfully");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error in sql", ex);
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
        return password;
    }

    @Override
    public String getManagerPassword() {
        String sql = "SELECT ManagerPassword from farmacybalance WHERE idFarmacy=1";
        String password = null;
        Statement stat = null;
        try {
            conn = dataSource.getConnection();
            stat = conn.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            if (rs.next()) {
                password = rs.getString("ManagerPassword");
            }
            LOG.info("selected succesfully");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error in sql", ex);
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
        return password;
    }

    @Override
    public void setAdminPassword(String password) {
        String sql = "UPDATE farmacybalance SET AdminPassword=? WHERE idFarmacy=1";
        PreparedStatement pstat = null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setString(1, password);
            pstat.executeUpdate();
            LOG.info("admin password was susscefully updated");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error updating admin password", ex);
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
    public void setManagerPassword(String password) {
        String sql = "UPDATE farmacybalance SET ManagerPassword=? WHERE idFarmacy=1";
        PreparedStatement pstat = null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setString(1, password);
            pstat.executeUpdate();
            LOG.info("manager password was susscefully updated");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error updating manager password", ex);
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
    public void RestrictNegativeBalance() throws SQLException {
        String sql = "ALTER TABLE farmacybalance CHANGE COLUMN"
                + " Balance Balance DOUBLE UNSIGNED NULL DEFAULT NULL";
        Statement stat = null;
        try {
            conn = dataSource.getConnection();
            Statement st = conn.createStatement();
            st.executeUpdate(sql);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
            if (ex.getErrorCode() == 1264) {
                throw new SQLException("This operation is possible when the account is not negative");
            }
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
    }

    @Override
    public void AllowNegativeBalance() {
        String sql = "ALTER TABLE farmacybalance CHANGE COLUMN"
                + " Balance Balance DOUBLE NULL DEFAULT NULL";
        Statement stat = null;
        try {
            conn = dataSource.getConnection();
            stat = conn.createStatement();
            stat.executeUpdate(sql);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
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
    }

    @Override
    public void RestrictStockNegativeBalance() throws SQLException {
        String sql = "ALTER TABLE stock_medicaments CHANGE COLUMN"
                + " Balance Balance DOUBLE UNSIGNED NULL DEFAULT NULL";
        Statement stat = null;
        try {
            conn = dataSource.getConnection();
            stat = conn.createStatement();
            stat.executeUpdate(sql);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            if (ex.getErrorCode() == 1264) {
                throw new SQLException("This operation is possible when the stock balance is not negative");
            }
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
    }

    @Override
    public void AllowStockNegativeBalance() {
        String sql = "ALTER TABLE stock_medicaments CHANGE COLUMN"
                + " Balance Balance DOUBLE NULL DEFAULT NULL";
        Statement stat = null;
        try {
            conn = dataSource.getConnection();
            stat = conn.createStatement();
            stat.executeUpdate(sql);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
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
    }

    @Override
    public boolean[] getPermissions() {
        boolean permissions[] = new boolean[2];
        try {
            conn = dataSource.getConnection();
            DatabaseMetaData dbmd = conn.getMetaData();
            ResultSet rs = dbmd.getColumns("", "", "farmacy.farmacybalance", "Balance");
            if (rs.next()) {
                if (rs.getString("TYPE_NAME").contains("UNSIGNED")) {
                    permissions[0] = false;
                } else {
                    permissions[0] = true;
                }
            }
            rs = dbmd.getColumns("", "", "farmacy.stock_medicaments", "Balance");
            if (rs.next()) {
                if (rs.getString("TYPE_NAME").contains("UNSIGNED")) {
                    permissions[1] = false;
                } else {
                    permissions[1] = true;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(FarmacyBalanceDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return permissions;
    }

    @Override
    public void setFarmacyName(String name) {
        String sql = "UPDATE farmacybalance SET FarmacyName=? WHERE idFarmacy=1";
        PreparedStatement pstat = null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setString(1, name);
            pstat.executeUpdate();
            LOG.info("Farmacy name was susscefully updated");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "Error updating Farmacy Name", ex);
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
    public String getFarmacyName() {
        String sql = "SELECT FarmacyName from farmacybalance WHERE idFarmacy=1";
        String name = null;
        Statement stat = null;
        try {
            conn = dataSource.getConnection();
            stat = conn.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            if (rs.next()) {
                name = rs.getString("FarmacyName");
            }
            LOG.info("selected succesfully");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error in sql", ex);
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
        return name;

    }

}
