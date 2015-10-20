/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.dao;

import com.iucosoft.farmacy.exceptions.DeletingException;
import com.iucosoft.farmacy.exceptions.OverSizeFieldException;
import com.iucosoft.farmacy.model.Medicament;
import java.util.List;

/**
 *
 * @author Turkov S
 */
public interface MedicamentDaoIntf {
    int  createMedicament(Medicament medicament)throws OverSizeFieldException;
    void updateMedicament(Medicament medicament)throws OverSizeFieldException;
    void removeMedicament(Medicament medicament)throws DeletingException;

    Medicament findByIdMedicament(int id);
    List<Medicament> findByNameMedicament(String name);
    List<Medicament> findByIdCategoryMedicament(int idCategory);
    List<Medicament> findByLatinNameMedicament(String name);
    List<Medicament> findAllMedicaments();
    List<Medicament> findAllAnalogs(Medicament medicament);
    List<Medicament> findAllMedicamentExlusiveAnalogs(Medicament medicament);
    
}
