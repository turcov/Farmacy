/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.dao;

import com.iucosoft.farmacy.model.Category;
import java.util.List;

/**
 *
 * @author Turkov S
 */
public interface CategoryDaoIntf {
    void createCategory(Category categ);
    void updateCategory(Category categ);
    void removeCategory(Category categ);

    Category findByIdCategory(int id);//
    List<Category> findByNameCategory(String name);//
    List<Category> findAllCategories();
    
}
