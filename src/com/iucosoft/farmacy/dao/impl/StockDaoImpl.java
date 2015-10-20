/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.dao.impl;

import com.iucosoft.farmacy.dao.StockDaoIntf;
import com.iucosoft.farmacy.db.DataSourceFarmacy;
import com.iucosoft.farmacy.exceptions.ConnectionInterruptedException;
import com.iucosoft.farmacy.exceptions.StockException;
import com.iucosoft.farmacy.model.Stock;
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
 * @author Serguei
 */
public class StockDaoImpl implements StockDaoIntf {

    private static final Logger LOG = Logger.getLogger(CategoryDaoImpl.class.getName());
    private DataSourceFarmacy dataSource;
    private Connection conn;

    public StockDaoImpl() throws ConnectionInterruptedException {
        dataSource = DataSourceFarmacy.getInstance();
        //conn = dataSource.getConnection();
    }

    void updateStock(Stock stock, boolean toCloseConnection) throws StockException {
        String sql = "UPDATE stock_medicaments SET balance=? WHERE idMedicament=?";
        PreparedStatement pstat=null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setDouble(1, stock.getBalance());
            pstat.setInt(2, stock.getIdMedicament());
            pstat.executeUpdate();
            LOG.info("stock " + stock + " succesfully updated ");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error while updating stock: " + stock);
            if (ex.getErrorCode() == 1264) {
                throw new StockException("Not enough chosen goods in stock");
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
    public void updateStock(Stock stock) throws StockException {
        updateStock(stock, true);
    }

    Stock findByIdStock(int idMedicament, boolean toCloseConnection) {
        String sql = "SELECT med.idMedicament,nameMedicament,balance "
                + "FROM medicaments AS med LEFT JOIN stock_medicaments AS st_med"
                + " ON(med.idMedicament=st_med.idMedicament) WHERE med.idMedicament=? ORDER BY med.idMedicament";
        Stock stock = null;
        PreparedStatement pstat=null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, idMedicament);
            ResultSet rs = pstat.executeQuery();
            if (rs.next()) {
                stock = new Stock(idMedicament, rs.getDouble("balance"));
                stock.setNameMedicament(rs.getString("nameMedicament"));
            }
            LOG.info("stock selected succesfully");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error in sql", ex);
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
        return stock;
    }

    @Override
    public Stock findByIdStock(int idMedicament) {
        return findByIdStock(idMedicament, true);
    }

    @Override
    public List<Stock> findByNameMedicamentStockList(String searchLine) {
        String sql = "SELECT med.idMedicament,nameMedicament,balance "
                + "FROM medicaments AS med LEFT JOIN stock_medicaments AS st_med"
                + " ON(med.idMedicament=st_med.idMedicament) WHERE med.NameMedicament LIKE ? ORDER BY med.idMedicament";
        List<Stock> listStock = new ArrayList<>();
        Stock stock = null;
        PreparedStatement pstat=null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setString(1, '%' + searchLine + '%');
            ResultSet rs = pstat.executeQuery();
            while (rs.next()) {
                stock = new Stock(rs.getInt("idMedicament"), rs.getDouble("balance"));
                stock.setNameMedicament(rs.getString("nameMedicament"));
                listStock.add(stock);
            }
            LOG.info("stock selected succesfully");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error in sql", ex);
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

        return listStock;
    }

    @Override
    public List<Stock> findByBalanceStockList(double balMin, double balMax) {
        String sql = "SELECT med.idMedicament,nameMedicament,balance "
                + "FROM medicaments AS med LEFT JOIN stock_medicaments AS st_med"
                + " ON(med.idMedicament=st_med.idMedicament) WHERE balance BETWEEN ? AND ? ORDER BY med.idMedicament";
        List<Stock> listStock = new ArrayList<>();
        Stock stock = null;
        PreparedStatement pstat=null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setDouble(1, balMin);
            pstat.setDouble(2, balMax);
            ResultSet rs = pstat.executeQuery();
            while (rs.next()) {
                stock = new Stock(rs.getInt("idMedicament"), rs.getDouble("balance"));
                stock.setNameMedicament(rs.getString("nameMedicament"));
                listStock.add(stock);
            }
            LOG.info("stock selected succesfully");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error in sql", ex);
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

        return listStock;
    }

    @Override
    public List<Stock> findAllStockList() {
        String sql = "SELECT med.idMedicament,nameMedicament,balance "
                + "FROM medicaments AS med LEFT JOIN stock_medicaments AS st_med"
                + " ON(med.idMedicament=st_med.idMedicament) ORDER BY med.idMedicament";
        List<Stock> listStock = new ArrayList<>();
        Stock stock = null;
        Statement stat=null;
        try {
            conn = dataSource.getConnection();
            stat = conn.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            while (rs.next()) {
                stock = new Stock(rs.getInt("idMedicament"), rs.getDouble("balance"));
                stock.setNameMedicament(rs.getString("nameMedicament"));
                listStock.add(stock);
            }
            LOG.info("stock selected succesfully");
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

        return listStock;
    }

    @Override
    public double getMinBalance() {
        String sql = "SELECT DISTINCT MIN(balance) from stock_medicaments";
        double min = 0.0;
        Statement stat=null;
        try {
            conn = dataSource.getConnection();
            stat = conn.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            if (rs.next()) {
                min = rs.getDouble("MIN(balance)");
            }
            LOG.info("stock selected succesfully");
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
        return min;
    }

    @Override
    public double getMaxBalance() {
        String sql = "SELECT DISTINCT MAX(balance) from stock_medicaments";
        double max = 0.0;
        Statement stat=null;
        try {
            conn = dataSource.getConnection();
            stat = conn.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            if (rs.next()) {
                max = rs.getDouble("MAX(balance)");
            }
            LOG.info("stock selected succesfully");
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

        return max;

    }

}
