/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.dao;

import com.iucosoft.farmacy.exceptions.ConnectionInterruptedException;
import com.iucosoft.farmacy.exceptions.DeletingException;
import com.iucosoft.farmacy.model.FinanceSubject;
import java.util.List;

/**
 *
 * @author Turkov S
 * 
 */
public interface FinanceSubjectDaoIntf {
    int addFinSubject(FinanceSubject finSubj);
    void updateFinSubject(FinanceSubject finSubj);
    void deleteFinSubject(FinanceSubject finSubj)throws DeletingException;
    FinanceSubject findFinSubjectById(int id);
    List<FinanceSubject> findFinSubjectByName(String name);
    List<FinanceSubject> findFinSubjectByAccount(String account);
    List<FinanceSubject> findAllFinSubects();
}
