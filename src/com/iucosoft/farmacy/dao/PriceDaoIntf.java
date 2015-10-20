/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.dao;

import com.iucosoft.farmacy.model.Category;
import com.iucosoft.farmacy.model.Price;
import java.util.List;

/**
 *
 * @author Serguei
 */
public interface PriceDaoIntf {

    //void createPrice(Price price);
    void updatePrice(Price price);
    //void removePrice(Price price);
    
    Price findPriceById(int idMedicament);
    List<Price> findByCategoryPriceList(Category cat);
    List<Price> findByNameMedicamentPriceList(String searchLine);
    List<Price> findAllPriceList();
}
