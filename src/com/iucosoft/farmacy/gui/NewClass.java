/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.gui;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Turkov S
 */
public class NewClass {
    public static void main(String[] args) {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"/bin/sh","-c","rpm -qa | grep test"});
        } catch (IOException ex) {
            Logger.getLogger(NewClass.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
