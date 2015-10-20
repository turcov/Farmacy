/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.dao;

import com.iucosoft.farmacy.exceptions.StockException;
import com.iucosoft.farmacy.model.Stock;
import java.util.List;

/**
 *
 * @author Serguei
 */
public interface StockDaoIntf {
    void updateStock(Stock stock) throws StockException;
    
    Stock findByIdStock(int idMedicament);
    List<Stock> findByNameMedicamentStockList(String searchLine);
    List<Stock> findByBalanceStockList(double balMin,double balMax);
    List<Stock> findAllStockList();
    double getMinBalance();
    double getMaxBalance();
}
