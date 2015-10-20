/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.dao.impl;

import com.iucosoft.farmacy.dao.PriceDaoIntf;
import com.iucosoft.farmacy.db.DataSourceFarmacy;
import com.iucosoft.farmacy.exceptions.ConnectionInterruptedException;
import com.iucosoft.farmacy.model.Category;
import com.iucosoft.farmacy.model.Price;
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
public class PriceDaoImpl implements PriceDaoIntf {

    private static final Logger LOG = Logger.getLogger(CategoryDaoImpl.class.getName());
    private DataSourceFarmacy dataSource;
    private Connection conn;

    public PriceDaoImpl() throws ConnectionInterruptedException {
        dataSource = DataSourceFarmacy.getInstance();
        //conn = dataSource.getConnection();
    }

//    @Override
//    public void createPrice(Price price) {
//        String sql = "INSERT INTO medicaments_prices VALUES (?,?,?,?)";
//        try {
//            PreparedStatement pstat = conn.prepareStatement(sql);
//            pstat.setInt(1, price.getIdMedicament());
//            pstat.setDouble(2, price.getUnitPrice());
//            pstat.setDouble(3, price.getMargin());
//            pstat.setDouble(4, price.getSaleUnitPrice());
//            pstat.executeUpdate();
//            pstat.close();
//            LOG.info("price for medicament_id=" + price.getIdMedicament() + " was succesfully created");
//        } catch (SQLException ex) {
//            LOG.log(Level.SEVERE, "error creating price for medicament_id=" + price.getIdMedicament(), ex);
//        }
//
//    }
    @Override
    public void updatePrice(Price price) {
        String sql = "UPDATE medicaments_prices SET unitPrice=?,margin=?,saleUnitPrice=?"
                + " WHERE idMedicament=?";
        PreparedStatement pstat=null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setDouble(1, price.getUnitPrice());
            pstat.setDouble(2, price.getMargin());
            pstat.setDouble(3, price.getSaleUnitPrice());
            pstat.setInt(4, price.getIdMedicament());
            pstat.executeUpdate();
            LOG.info("price for medicament_id=" + price.getIdMedicament() + " was succesfully updated");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error updating price for medicament_id=" + price.getIdMedicament(), ex);
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

//    @Override
//    public void removePrice(Price price) {
//        String sql = "DELETE FROM medicaments_prices WHERE idMedicament=?";
//        try {
//            PreparedStatement pstat = conn.prepareStatement(sql);
//            pstat.setInt(1, price.getIdMedicament());
//            pstat.executeUpdate();
//            pstat.close();
//            LOG.info("price for medicament_id=" + price.getIdMedicament() + " was succesfully deleted");
//        } catch (SQLException ex) {
//            LOG.log(Level.SEVERE, "error deleting price for medicament_id=" + price.getIdMedicament(), ex);
//        }
//    }
    @Override
    public Price findPriceById(int idMedicament) {
        String sql = "SELECT med.idMedicament,nameMedicament,unitPrice,margin,saleUnitPrice "
                + "FROM medicaments AS med LEFT JOIN medicaments_prices AS med_pr"
                + " ON(med.idMedicament=med_pr.idMedicament) WHERE med.idMedicament=? ORDER BY med.idMedicament";
        Price price = null;
        PreparedStatement pstat=null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, idMedicament);
            ResultSet rs = pstat.executeQuery();
            if (rs.next()) {
                price = new Price(idMedicament, rs.getDouble("unitPrice"),
                        rs.getDouble("margin"), rs.getDouble("saleUnitPrice"));
                price.setNameMedicament(rs.getString("nameMedicament"));
            }
            LOG.info("price was selected succesfully");
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
        return price;
    }

    @Override
    public List<Price> findByCategoryPriceList(Category cat) {
        String sql = "SELECT med.idMedicament,nameMedicament,unitPrice,margin,saleUnitPrice "
                + "FROM medicaments_prices AS med_pr LEFT JOIN medicaments AS med"
                + " ON(med.idMedicament=med_pr.idMedicament) WHERE idCategory=? ORDER BY med.idMedicament";
        Price price = null;
        List<Price> priceList = new ArrayList();
        PreparedStatement pstat=null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, cat.getIdCategory());
            ResultSet rs = pstat.executeQuery();
            while (rs.next()) {
                price = new Price(rs.getInt("idMedicament"), rs.getDouble("unitPrice"),
                        rs.getDouble("margin"), rs.getDouble("saleUnitPrice"));
                price.setNameMedicament(rs.getString("nameMedicament"));
                priceList.add(price);
            }
            LOG.info("price was selected succesfully");
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
        return priceList;

    }

    @Override
    public List<Price> findByNameMedicamentPriceList(String searchLine) {
        String sql = "SELECT med.idMedicament,nameMedicament,unitPrice,margin,saleUnitPrice "
                + "FROM medicaments_prices AS med_pr LEFT JOIN medicaments AS med"
                + " ON(med.idMedicament=med_pr.idMedicament) "
                + "WHERE nameMedicament LIKE ? ORDER BY med.idMedicament";
        Price price = null;
        List<Price> priceList = new ArrayList();
        PreparedStatement pstat=null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setString(1, '%' + searchLine + '%');
            ResultSet rs = pstat.executeQuery();
            while (rs.next()) {
                price = new Price(rs.getInt("idMedicament"), rs.getDouble("unitPrice"),
                        rs.getDouble("margin"), rs.getDouble("saleUnitPrice"));
                price.setNameMedicament(rs.getString("nameMedicament"));
                priceList.add(price);
            }
            LOG.info("price was selected succesfully");
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
        return priceList;

    }

    @Override
    public List<Price> findAllPriceList() {
        String sql = "SELECT med.idMedicament,nameMedicament,unitPrice,margin,saleUnitPrice "
                + "FROM medicaments_prices AS med_pr LEFT JOIN medicaments AS med"
                + " ON(med.idMedicament=med_pr.idMedicament) ORDER BY med.idMedicament";
        Price price = null;
        List<Price> priceList = new ArrayList();
        Statement stat=null;
        try {
            conn = dataSource.getConnection();
            stat = conn.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            while (rs.next()) {
                price = new Price(rs.getInt("idMedicament"), rs.getDouble("unitPrice"),
                        rs.getDouble("margin"), rs.getDouble("saleUnitPrice"));
                price.setNameMedicament(rs.getString("nameMedicament"));
                priceList.add(price);
            }
            LOG.info("price was selected succesfully");
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
        return priceList;
    }

}
