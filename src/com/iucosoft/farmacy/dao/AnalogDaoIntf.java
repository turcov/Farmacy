/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.dao;

import com.iucosoft.farmacy.model.Analog;
import com.iucosoft.farmacy.model.Medicament;
import java.util.List;

/**
 *
 * @author Turkov S
 */
public interface AnalogDaoIntf {
    void createAnalog(Analog analog);
    void createAnalogs(List<Analog> analogList);
    void updateAnalog(Analog analog,int newAnalogId);
    void removeAnalog(Analog analog);
    void removeAnalogs(List<Analog> analogList);
    
    List<Integer> findAllAnalogsIds(int id);

}
