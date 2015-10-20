/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.gui.models;

import com.iucosoft.farmacy.dao.CategoryDaoIntf;
import com.iucosoft.farmacy.dao.impl.CategoryDaoImpl;
import com.iucosoft.farmacy.exceptions.ConnectionInterruptedException;
import com.iucosoft.farmacy.model.Category;
import java.util.List;
import javax.swing.DefaultListModel;

/**
 *
 * @author Serguei
 */
public class CategoryListModel extends DefaultListModel<Category> {

    CategoryDaoIntf catDao;

    public CategoryListModel() throws ConnectionInterruptedException {
        catDao = new CategoryDaoImpl();
        refreshModel();
    }

    public void refreshModel() {
        super.clear();
        List<Category> listCat = catDao.findAllCategories();
        for (Category aCat : listCat) {
            super.addElement(aCat);
        }
    }

}
