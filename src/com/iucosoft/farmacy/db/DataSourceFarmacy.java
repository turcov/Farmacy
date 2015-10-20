/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.db;

import com.iucosoft.farmacy.exceptions.ConnectionErrorException;
import com.iucosoft.farmacy.exceptions.ConnectionInterruptedException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Turkov S
 */
public class DataSourceFarmacy {

    private static final Logger LOG = Logger.getLogger(DataSourceFarmacy.class.getName());
    private static DataSourceFarmacy instance;

    private final String driverName;
    private final String dbURL;
    private final String user;
    private final char[] password;
    Connection conn;

    public String getUser() {
        return user;
    }

    public static DataSourceFarmacy getInstance() throws ConnectionInterruptedException {
        if (instance != null) {
            return instance;
        } else {
//            LOG.info("Connection interrupted");
            throw new ConnectionInterruptedException("Connection interrupted");
        }
    }

    public static DataSourceFarmacy getInstance(String driverName, String dbUrl, String user, char[] password) {
        if (instance == null) {
            instance = new DataSourceFarmacy(driverName, dbUrl, user, password);
        } else {
            if (!instance.driverName.equals(driverName)
                    || !(instance.dbURL.equals(dbUrl))||
                     !(instance.user.equals(user))||
                    !(instance.password.equals(password))) {
                instance.disconnect();
                instance = new DataSourceFarmacy(driverName, dbUrl, user, password);
            }
        }
        return instance;
    }

    public DataSourceFarmacy(String driverName, String dbURL, String user, char[] password) {
        this.driverName = driverName;
        this.dbURL = dbURL;
        this.user = user;
        this.password = password;
        loadDriver();
    }

    public void testConnection() throws ConnectionErrorException {
        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(dbURL, user, new String(password));
                LOG.info("connection succedeed");
                getConnection().close();
            }
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1045) {
                LOG.log(Level.SEVERE, "Wrong login or password", ex);
                throw new ConnectionErrorException("Wrong login or password");
            }
            if (ex.getErrorCode() == 0) {
                LOG.log(Level.SEVERE, "Error in ip address", ex);
                throw new ConnectionErrorException("Error in host name");
            }
        }
    }

    public Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(dbURL, user, new String(password));
                LOG.info("connection succedeed");
            }
        } catch (SQLException ex) {
            if (ex.getErrorCode() != 1045) {
                LOG.log(Level.SEVERE, "Error in connection", ex);
                return null;
                //   System.exit(-1);
            }
        }
        return conn;
    }

    private void loadDriver() {
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException ex) {
            LOG.log(Level.SEVERE, "Driver not loaded", ex);
            System.exit(-1);
        }
        LOG.info("driver loaded succesfully");
    }

    public void disconnect() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                conn = null;
                DriverManager.deregisterDriver(DriverManager.getDriver(driverName));
                LOG.info("connection closed succesfully");
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "error closing connection", ex);
        }
    }

}
