/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.dao.impl;

import com.iucosoft.farmacy.dao.CategoryDaoIntf;
import com.iucosoft.farmacy.db.DataSourceFarmacy;
import com.iucosoft.farmacy.exceptions.ConnectionInterruptedException;
import com.iucosoft.farmacy.model.Category;
import java.sql.Connection;
import java.sql.Date;
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
public class CategoryDaoImpl implements CategoryDaoIntf {

    private static final Logger LOG = Logger.getLogger(CategoryDaoImpl.class.getName());
    private DataSourceFarmacy dataSource;
    private Connection conn;

    public CategoryDaoImpl() throws ConnectionInterruptedException {
        dataSource = DataSourceFarmacy.getInstance();
        //conn = dataSource.getConnection();
    }

    @Override
    public void createCategory(Category categ) {
        String sql = "INSERT INTO categories_medicaments VALUES (null,?)";
        PreparedStatement pstat=null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setString(1, categ.getNameCategory());
            pstat.executeUpdate();
            LOG.info("category " + categ + " succesfully created ");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error creating category: " + categ, ex);
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
    public void updateCategory(Category categ) {
        String sql = "UPDATE categories_medicaments SET nameCategory=? WHERE idCategory=?";
        PreparedStatement pstat=null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setString(1, categ.getNameCategory());
            pstat.setInt(2, categ.getIdCategory());
            pstat.executeUpdate();
            LOG.info("category " + categ + " succesfully updated ");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error while updating category: " + categ, ex);
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
    public void removeCategory(Category categ) {
        String sql = "DELETE FROM categories_medicaments WHERE idCategory=?";
        PreparedStatement pstat=null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, categ.getIdCategory());
            pstat.executeUpdate();
            LOG.info("category " + categ + " was succesfully deleted: ");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error deleting category: " + categ, ex);
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
    public Category findByIdCategory(int id) {
        String sql = "SELECT * FROM categories_medicaments WHERE idCategory=?";
        Category categ = null;
        PreparedStatement pstat=null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, id);
            ResultSet rs=pstat.executeQuery();
            if (rs.next()) {
                categ=new Category(id, rs.getString("nameCategory"));
            }
            LOG.info("categories selected succesfully");
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

        return categ;
    }

    @Override
    public List<Category> findByNameCategory(String name) {
        String sql = "SELECT * FROM categories_medicaments WHERE nameCategory LIKE ?";
        List<Category> listCat=new ArrayList<>();
        Category categ = null;
        PreparedStatement pstat=null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setString(1,'%'+ name+'%');
            ResultSet rs=pstat.executeQuery();
            while (rs.next()) {
                categ=new Category(rs.getInt("idCategory"), rs.getString("nameCategory"));
                listCat.add(categ);
            }
            LOG.info("categories selected succesfully");
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

        return listCat;
    }

    @Override
    public List<Category> findAllCategories() {
        String sql = "SELECT * FROM categories_medicaments";
        List<Category> listCat=new ArrayList<>();
        Category categ = null;
        Statement stat=null;
        try {
            conn = dataSource.getConnection();
            stat = conn.createStatement();
            ResultSet rs=stat.executeQuery(sql);
            while (rs.next()) {
                categ=new Category(rs.getInt("idCategory"), rs.getString("nameCategory"));
                listCat.add(categ);
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

        return listCat;
    }
}
