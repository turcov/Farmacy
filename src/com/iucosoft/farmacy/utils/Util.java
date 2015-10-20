/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.utils;

import com.iucosoft.farmacy.gui.JDialogInvoiceDetails;
import com.iucosoft.farmacy.model.Invoice;
import com.iucosoft.farmacy.model.Price;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Turkov S
 */
public class Util {

    private static final Logger LOG = Logger.getLogger(Util.class.getName());

    public static void selectRowInTable(JTable jTable, int row) {
        //If have any records
        if (jTable.getRowCount() > 0) {
            if (row >= 0) {
                if (row < jTable.getRowCount()) {
                    jTable.setRowSelectionInterval(row, row);
                } else {
                    jTable.setRowSelectionInterval(row - 1, row - 1);
                }
            } else {
                jTable.setRowSelectionInterval(0, 0);
            }

        }
    }

    public static int getLastInsertedIdInTable(Connection conn) {
        int lastId = 0;
        String sql = "SELECT last_insert_id()";
        Statement stat;
        try {
            stat = conn.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            if (rs.next()) {
                lastId = rs.getInt("last_insert_id()");
            }
            stat.close();
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return lastId;
    } 

    public static void clearModel(DefaultTableModel dfd) {
        for (int i = dfd.getRowCount() - 1; i >= 0; i--) {
            dfd.removeRow(i);
        }
    }

    public static Date parseToDateSql(java.util.Date dateUtil) {
        return new Date(dateUtil.getTime());
    }

    public static java.util.Date parseToDateUtil(Date dateSql) {
        return new java.util.Date(dateSql.getTime());
    }

    public static double truncDouble(double dd) {
        return (int) (dd * 100) / 100.0;
    }

    public static double roundTo(double dd) {
        return Math.round(dd * 100.0) / 100.0;
    }

    public static double roundUp(double dd) {
        double rez = roundTo(dd);
        if ((rez - dd) < 0) {
            return truncDouble(dd + .01);
        }
        return rez;
    }

    public static double getPriceofMedicament(Price price, Invoice inv) {
        Class clas = price.getClass();
        Class[] paramtypes = new Class[]{};
        Object[] args = new Object[]{};
        double priceOfMedicament = 0;
        try {
            Method method = clas.getMethod(inv.getPriceMethodName(), paramtypes);
            priceOfMedicament = (Double) method.invoke(price, args);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(JDialogInvoiceDetails.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(JDialogInvoiceDetails.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(JDialogInvoiceDetails.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(JDialogInvoiceDetails.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(JDialogInvoiceDetails.class.getName()).log(Level.SEVERE, null, ex);
        }
        return priceOfMedicament;
    }

    public static void setColumnWidthTable(JTable jTable, int... width) {
        for (int i = 0; i < width.length; i++) {
            int x = width[i];
            jTable.getColumnModel().getColumn(i).setResizable(false);
            jTable.getColumnModel().getColumn(i).setPreferredWidth(x);
        }
    }
}
