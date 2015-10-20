/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.dao;

import com.iucosoft.farmacy.exceptions.NoSuchMoneyException;
import java.sql.SQLException;

/**
 *
 * @author Turkov S
 */
public interface FarmacyBalanceDaoIntf {
    void setFarmacyBalance(double newBalance)throws NoSuchMoneyException;
    double getFarmacyBalance();
    String getAdminPassword();
    String getManagerPassword();
    void setAdminPassword(String password);
    void setManagerPassword(String password);
    void RestrictNegativeBalance()throws SQLException;
    void AllowNegativeBalance();
    void RestrictStockNegativeBalance()throws SQLException;
    void AllowStockNegativeBalance();
    boolean[] getPermissions();
    void setFarmacyName(String name);
    String getFarmacyName();
}
