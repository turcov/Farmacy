/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.dao.impl;

import com.iucosoft.farmacy.dao.AnalogDaoIntf;
import com.iucosoft.farmacy.db.DataSourceFarmacy;
import com.iucosoft.farmacy.exceptions.ConnectionInterruptedException;
import com.iucosoft.farmacy.model.Analog;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Serguei
 */
public class AnalogDaoImpl implements AnalogDaoIntf {

    private static final Logger LOG = Logger.getLogger(CategoryDaoImpl.class.getName());
    private DataSourceFarmacy dataSource;
    private Connection conn;
    

    public AnalogDaoImpl() throws ConnectionInterruptedException {
        dataSource = DataSourceFarmacy.getInstance();
        //conn = dataSource.getConnection();
    }

    @Override
    public void createAnalog(Analog analog) {
        String sql = "INSERT INTO medicament_analogs VALUES (?,?)";
        PreparedStatement pstat=null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, analog.getIdMedicament());
            pstat.setInt(2, analog.getIdMedicamentAnalog());
            pstat.executeUpdate();
            pstat.setInt(2, analog.getIdMedicament());
            pstat.setInt(1, analog.getIdMedicamentAnalog());
            pstat.executeUpdate();
            LOG.info("analog " + analog + " succesfully created ");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error creating analog: " + analog, ex);
        }finally {
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
    public void updateAnalog(Analog analog, int newAnalogId) {
        String sql = "UPDATE medicament_analogs SET idMedicamentAnalog=?"
                + " WHERE idMedicament=? AND idMedicamentAnalog=?";
        PreparedStatement pstat=null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, newAnalogId);
            pstat.setInt(2, analog.getIdMedicament());
            pstat.setInt(3, analog.getIdMedicamentAnalog());
            pstat.executeUpdate();
            LOG.info("analog " + analog + " succesfully updated ");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error while updating analog: " + analog, ex);
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
    public void removeAnalog(Analog analog) {
        String sql = "DELETE FROM medicament_analogs WHERE idMedicament=? AND idMedicamentAnalog=?";
        PreparedStatement pstat=null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, analog.getIdMedicament());
            pstat.setInt(2, analog.getIdMedicamentAnalog());
            pstat.executeUpdate();
            pstat.setInt(2, analog.getIdMedicament());
            pstat.setInt(1, analog.getIdMedicamentAnalog());
            pstat.executeUpdate();
            LOG.info("analog " + analog + " was succesfully deleted: ");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error deleting analog: " + analog, ex);
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
    public List<Integer> findAllAnalogsIds(int id) {
        String sql = "SELECT idMedicamentAnalog FROM medicament_analogs WHERE idMedicament=?";
        List<Integer> analogsIds = new ArrayList<>();
        PreparedStatement pstat=null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, id);
            ResultSet rs = pstat.executeQuery();
            while (rs.next()) {
                analogsIds.add(rs.getInt("idMedicamentAnalog"));
            }
            LOG.info("analogs selected succesfully");
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
        return analogsIds;
    }

    @Override
    public void createAnalogs(List<Analog> analogList) {
        String sql = "INSERT INTO medicament_analogs VALUES (?,?)";
        PreparedStatement pstat=null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            for (Analog analog : analogList) {
                pstat.setInt(1, analog.getIdMedicament());
                pstat.setInt(2, analog.getIdMedicamentAnalog());
                pstat.addBatch();
                pstat.setInt(2, analog.getIdMedicament());
                pstat.setInt(1, analog.getIdMedicamentAnalog());
                pstat.addBatch();
            }
            pstat.executeBatch();
            //pstat.executeUpdate();
            LOG.info("analogs list was succesfully created ");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error creating analogs list", ex);
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
    public void removeAnalogs(List<Analog> analogList) {
        String sql = "DELETE FROM medicament_analogs WHERE idMedicament=? AND idMedicamentAnalog=?";
        PreparedStatement pstat=null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            for (Analog analog : analogList) {
                pstat.setInt(1, analog.getIdMedicament());
                pstat.setInt(2, analog.getIdMedicamentAnalog());
                pstat.addBatch();
                pstat.setInt(2, analog.getIdMedicament());
                pstat.setInt(1, analog.getIdMedicamentAnalog());
                pstat.addBatch();
            }
            pstat.executeBatch();
            LOG.info("analogs list  was succesfully deleted: ");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error deleting analog analogs list", ex);
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

}
