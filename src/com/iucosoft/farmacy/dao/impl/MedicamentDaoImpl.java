/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.dao.impl;

import com.iucosoft.farmacy.dao.MedicamentDaoIntf;
import com.iucosoft.farmacy.db.DataSourceFarmacy;
import com.iucosoft.farmacy.exceptions.ConnectionInterruptedException;
import com.iucosoft.farmacy.exceptions.DeletingException;
import com.iucosoft.farmacy.exceptions.OverSizeFieldException;
import com.iucosoft.farmacy.model.Medicament;
import static com.iucosoft.farmacy.utils.Util.getLastInsertedIdInTable;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
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
 * @author Serguei
 */
public class MedicamentDaoImpl implements MedicamentDaoIntf {

    private static final Logger LOG = Logger.getLogger(MedicamentDaoImpl.class.getName());

    private DataSourceFarmacy dataSource;
    private Connection conn;

    public MedicamentDaoImpl() throws ConnectionInterruptedException {
        dataSource = DataSourceFarmacy.getInstance();
        //conn = dataSource.getConnection();
    }

    @Override
    public int createMedicament(Medicament medicament) throws OverSizeFieldException {
        String sql = "INSERT INTO medicaments VALUES (null,?,?,?,?)";
        int lastId = 0;
        PreparedStatement pstat = null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setString(1, medicament.getNameMedicament());
            if (medicament.getIdCategory() > 0) {
                pstat.setInt(2, medicament.getIdCategory());
            } else {
                pstat.setNull(2, Types.INTEGER);
            }
            pstat.setString(3, medicament.getLatinNameMedicament());
            byte[] img = medicament.getIconMedicament();
            if (img != null) {
                pstat.setBinaryStream(4, new ByteArrayInputStream(img));
            } else {
                pstat.setBinaryStream(4, null);
            }
            pstat.executeUpdate();
            lastId = getLastInsertedIdInTable(conn);
            LOG.info("medicament " + medicament + " succesfully created ");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error creating medicament: " + medicament + ". Errorcode=" + ex.getErrorCode(), ex);
            if (ex.getErrorCode() == 1406) {
                throw new OverSizeFieldException("Cannot to save.Filesize is more than 65K.");
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
        return lastId;
    }

    @Override
    public void updateMedicament(Medicament medicament) throws OverSizeFieldException {
        String sql = "UPDATE medicaments SET nameMedicament=?,idCategory=?,"
                + "latinName=?,icon=? WHERE idMedicament=?";
        PreparedStatement pstat = null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setString(1, medicament.getNameMedicament());
            if (medicament.getIdCategory() > 0) {
                pstat.setInt(2, medicament.getIdCategory());
            } else {
                pstat.setNull(2, Types.INTEGER);
            }
            pstat.setString(3, medicament.getLatinNameMedicament());
            byte[] img = medicament.getIconMedicament();
            if (img != null) {
                pstat.setBinaryStream(4, new ByteArrayInputStream(img));
            } else {
                pstat.setBinaryStream(4, null);
            }
            pstat.setInt(5, medicament.getIdMedicament());
            pstat.executeUpdate();
            LOG.info("medicament " + medicament + " succesfully updated ");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error while updating medicament: " + medicament + ". Errorcode=" + ex.getErrorCode(), ex);
            if (ex.getErrorCode() == 1406) {
                throw new OverSizeFieldException("Cannot to update.Filesize is more than 65K");
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
    public void removeMedicament(Medicament medicament) throws DeletingException {
        String sql = "DELETE FROM medicaments WHERE idMedicament=?";
        PreparedStatement pstat = null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, medicament.getIdMedicament());
            pstat.executeUpdate();
            LOG.info("medicament " + medicament + " was succesfully deleted: ");
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error deleting medicament: " + medicament + ".Errorcode=" + ex.getErrorCode(), ex);
            if (ex.getErrorCode() == 1451) {
                throw new DeletingException("Unable to remove \"" + medicament.getNameMedicament()
                        + "\".\nIt has been used in invoices.\n"
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
    public Medicament findByIdMedicament(int id) {
        String sql = "SELECT * FROM medicaments WHERE idMedicament=?";
        Medicament medicament = null;
        PreparedStatement pstat = null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, id);
            ResultSet rs = pstat.executeQuery();
            if (rs.next()) {
                medicament = new Medicament(id, rs.getString("nameMedicament"),
                        rs.getInt("idCategory"), rs.getString("latinName"),
                        rs.getBytes("icon"));
            }
            LOG.info("medicaments selected succesfully");
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

        return medicament;
    }

    @Override
    public List<Medicament> findByNameMedicament(String name) {
        String sql = "SELECT * FROM medicaments WHERE nameMedicament LIKE ? or latinName LIKE ?";
        List<Medicament> listMedicaments = new ArrayList<>();
        Medicament medicament = null;
        PreparedStatement pstat = null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setString(1, '%' + name + '%');
            pstat.setString(2, '%' + name + '%');
            ResultSet rs = pstat.executeQuery();
            while (rs.next()) {
                medicament = new Medicament(rs.getInt("idMedicament"),
                        rs.getString("nameMedicament"), rs.getInt("idCategory"),
                        rs.getString("latinName"), rs.getBytes("icon"));
                listMedicaments.add(medicament);
            }
            LOG.info("medicaments selected succesfully");
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
        return listMedicaments;
    }

    @Override
    public List<Medicament> findByIdCategoryMedicament(int idCategory) {
        String sql = "SELECT * FROM medicaments WHERE idCategory=?";
        List<Medicament> listMedicaments = new ArrayList<>();
        Medicament medicament = null;
        PreparedStatement pstat = null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, idCategory);
            ResultSet rs = pstat.executeQuery();
            while (rs.next()) {
                medicament = new Medicament(rs.getInt("idMedicament"),
                        rs.getString("nameMedicament"), rs.getInt("idCategory"),
                        rs.getString("latinName"), rs.getBytes("icon"));
                listMedicaments.add(medicament);
            }
            LOG.info("medicaments selected succesfully");
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
        return listMedicaments;

    }

    @Override
    public List<Medicament> findByLatinNameMedicament(String name) {
        String sql = "SELECT * FROM medicaments WHERE latinName LIKE ?";
        List<Medicament> listMedicaments = new ArrayList<>();
        Medicament medicament = null;
        PreparedStatement pstat = null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setString(1, '%' + name + '%');
            ResultSet rs = pstat.executeQuery();
            while (rs.next()) {
                medicament = new Medicament(rs.getInt("idMedicament"),
                        rs.getString("nameMedicament"), rs.getInt("idCategory"),
                        rs.getString("latinName"), rs.getBytes("icon"));
                listMedicaments.add(medicament);
            }
            LOG.info("medicaments selected succesfully");
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
        return listMedicaments;
    }

    @Override
    public List<Medicament> findAllMedicaments() {
        String sql = "SELECT * FROM medicaments";
        List<Medicament> listMedicaments = new ArrayList<>();
        Medicament medicament = null;
        Statement stat = null;
        try {
            conn = dataSource.getConnection();
            stat = conn.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            while (rs.next()) {
                medicament = new Medicament(rs.getInt("idMedicament"),
                        rs.getString("nameMedicament"), rs.getInt("idCategory"),
                        rs.getString("latinName"), rs.getBytes("icon"));
                listMedicaments.add(medicament);
            }
            LOG.info("medicaments selected succesfully");
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
        return listMedicaments;
    }

    @Override
    public List<Medicament> findAllAnalogs(Medicament medicament) {
        String sql = "SELECT * from medicaments WHERE idMedicament IN (SELECT idMedicamentAnalog FROM medicament_analogs WHERE idMedicament=?)";
        List<Medicament> listMedicaments = new ArrayList<>();
        Medicament medAnalog = null;
        PreparedStatement pstat = null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, medicament.getIdMedicament());
            ResultSet rs = pstat.executeQuery();
            while (rs.next()) {
                medAnalog = new Medicament(rs.getInt("idMedicament"),
                        rs.getString("nameMedicament"), rs.getInt("idCategory"),
                        rs.getString("latinName"), rs.getBytes("icon"));
                listMedicaments.add(medAnalog);
            }
            LOG.info("medicaments selected succesfully");
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

        return listMedicaments;
    }

    @Override
    public List<Medicament> findAllMedicamentExlusiveAnalogs(Medicament medicament) {
        String sql = "SELECT * from medicaments WHERE idMedicament NOT IN "
                + "(SELECT idMedicamentAnalog FROM medicament_analogs WHERE idMedicament=?)"
                + " AND idMedicament<>?";
        List<Medicament> listMedicaments = new ArrayList<>();
        Medicament medAnalog = null;
        PreparedStatement pstat = null;
        try {
            conn = dataSource.getConnection();
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, medicament.getIdMedicament());
            pstat.setInt(2, medicament.getIdMedicament());
            ResultSet rs = pstat.executeQuery();
            while (rs.next()) {
                medAnalog = new Medicament(rs.getInt("idMedicament"),
                        rs.getString("nameMedicament"), rs.getInt("idCategory"),
                        rs.getString("latinName"), rs.getBytes("icon"));
                listMedicaments.add(medAnalog);
            }
            LOG.info("medicaments selected succesfully");
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

        return listMedicaments;

    }

}
